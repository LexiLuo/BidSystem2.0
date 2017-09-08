package multiAgent.agentHelper;

import DO.bid;
import DO.landlord;
import DO.tenant;
import VO.Consult;
import dao.daoImpl.bidDao;
import dao.daoImpl.landlordDao;
import multiAgent.agent.tenantAgent;
import multiAgent.AIDecision.tenant.calScore.CalPoints;
import multiAgent.AIDecision.tenant.calScore.ComfortablePerson;
import multiAgent.AIDecision.tenant.calScore.EconomicalPerson;
import multiAgent.ontology.Bid;
import multiAgent.ontology.Order;
import multiAgent.ontology.Room;
import multiAgent.ontology.RoomType;
import smile.classification.RandomForest;

import jade.util.leap.List;
import jade.util.leap.ArrayList;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created by zy on 17/5/8.
 * evaluate the quality of the room(bid) which is from the landlord
 */
public class ValueCal {
    private String character = null;

    private RandomForest forest = null;
    private HashMap<RoomType,Integer> roomPoint = new HashMap<RoomType, Integer>();

    //the result of bid
    private List reject = new ArrayList();
    private List GoodBid = new ArrayList();

    private List GoodScore = new ArrayList();

    private static int init_maxPrice = 0;
    private static int init_minPrice = 0;
    private static int init_avePrice = 0;
    private static int goodLevel = 11;

    public ValueCal(){
        this.fill_hashmap();
    }

    public void initPrice(List bids){
        int sumPrice = 0;
        int maxPrice = ((Bid)bids.get(0)).getPrice();
        int minPrice = ((Bid)bids.get(0)).getPrice();
        for(int i=0;i<bids.size();i++){
            int tempPrice = ((Bid)bids.get(i)).getPrice();
            sumPrice+= tempPrice;
            if(tempPrice>maxPrice){
                maxPrice = tempPrice;
            }else if(tempPrice<minPrice){
                minPrice = tempPrice;
            }
        }
        init_avePrice = sumPrice/(bids.size());
        init_maxPrice = maxPrice;
        init_minPrice = minPrice;
    }

    public List ScreenBids(List bids, tenant user, Order order, boolean InNegotiation, jade.core.Agent agent){
        List resultBids = new ArrayList();

        int goodBidScore = 0;

        //deal with the detail scores
        CalPoints calPoints = null;
        if(user.getPreference().equals("economical")){
            calPoints = new EconomicalPerson();
            for(int i=0;i<bids.size();i++){
                Bid tempbid = ((Bid)bids.get(i));
                Room room = tempbid.getRoom();
//                landlord l =  landlordDao.findlandlordByid(room.getLandlordId());
                bid bid_fortrain = new bid();
                calPoints.setBid(bid_fortrain);

                bid_fortrain.setLandlordid(room.getLandlordId());
                bid_fortrain.setRoomid(room.getRoomId());
                bid_fortrain.setOrderid(tempbid.getId());

                double priceScore = calPoints.calPrice(init_maxPrice,init_minPrice,init_avePrice,tempbid.getPrice());
//                int roomScore = calPoints.calRoom(room.getType(),order.getRoomType(),roomPoint);
                double facilityScore = calPoints.calFacility(tempbid.getFacilities(),order.getFacilities());
                double siteScore = calPoints.calsite(tempbid.getAroundsites());
                double sum = priceScore+facilityScore+siteScore;
                sum = new BigDecimal(sum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                bid_fortrain.setPrice((int)(priceScore*100));
                bid_fortrain.setArroundsite(siteScore*100+"");
                bid_fortrain.setFacility(facilityScore*100+"");
                bid_fortrain.setScore(sum*100+"");
                bid_fortrain.setRoomtype("1");
                System.out.println("竞标书 ID 是"+tempbid.getLandlordId().getName() +" 并且他的分数是:"+sum);
                /*
                 思路：
                     1.在少数据量的情况下根据原有算法判断是否过滤Bid，即没有4步骤
                     2.对于直接拒绝的Bid以后可以不进行数据的持久化：但是需要比较完善的拒绝策略。比如有个数据特征偏差太大或特征总分离预期差距过大
                     3.对Bid进行量化，抽象出不同特征维度算分，目前分价格、设施、周边。在实际开发中，可以充分考虑特征选择的问题和如何对特征
                       进行量化的问题。理论上不同维度应该具有不同的权证，具体的量化过程也应该是动态的，就是对于量化过程中的那些参数，初始时可以用户
                       或系统自定义，之后具有学习的过程。目前想不到合适的算法。
                     4.对于randomForest用来预测是否还价或接受竞标书，可以作为第一层判断，可以自主学习，具有一定的精确度。缺点：不能预测期望分。
                       randomForest能够根据不同维度的特征值对结果进行预测，即还价还是接受竞标书。
                     5.在4的前提下可以进行，可以进行协商前的预期价格估算等，在setConsult函数中实现。个人认为这和用户特征、Bid特征等有关系。
                       目前只是简单的计算。5步骤实际是在4的基础上进行价格的处理。
                     6.与房东协商重复3、4、5步骤，目前结束条件是 房东不愿意降价或者Bid特征分达到Good标准。该标准应该是动态的，起始由用户决定。
                 */
                if(sum<6){
                    reject.add(tempbid);
                    bid_fortrain.setResult(0);
                }else if(sum>=goodLevel){
                    goodBidScore+=sum;
                    GoodBid.add(tempbid);
                    GoodScore.add(sum);
                    bid_fortrain.setResult(1);
                }else{
                    bid_fortrain.setResult(0);
                    resultBids.add(tempbid);
                }
                //持久化
                bidDao.insert(bid_fortrain);
                if(!InNegotiation){
                    this.initConsult(tempbid.getRoom().getLandlordId(),user.getName(),user.getEconomic(),sum,tempbid.getPrice(),agent);
                }else{
                    this.setConsult(tempbid.getRoom().getLandlordId(),user.getEconomic(),sum,tempbid.getPrice(),agent);
                }
            }
        }else if(user.getPreference().equals("comfortable")){
            calPoints = new ComfortablePerson();
            for(int i=0;i<bids.size();i++){
                Bid tempbid = (Bid)bids.get(i);
                Room room = tempbid.getRoom();

                bid bid_fortrain = new bid();
                calPoints.setBid(bid_fortrain);

                bid_fortrain.setLandlordid(room.getLandlordId());
                bid_fortrain.setRoomid(room.getRoomId());
                bid_fortrain.setOrderid(tempbid.getId());
                double priceScore = calPoints.calPrice(init_maxPrice,init_minPrice,init_avePrice,tempbid.getPrice());
                double facilityScore = calPoints.calFacility(tempbid.getFacilities(),order.getFacilities());
                double siteScore = calPoints.calsite(tempbid.getAroundsites());
                double sum = priceScore+facilityScore+siteScore;
                sum = new BigDecimal(sum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                bid_fortrain.setPrice((int)(priceScore*100));
                bid_fortrain.setArroundsite(siteScore*100+"");
                bid_fortrain.setFacility(facilityScore*100+"");
                bid_fortrain.setScore(sum*100+"");
                bid_fortrain.setRoomtype("1");
                System.out.println("竞标书 ID 是"+tempbid.getLandlordId().getName() +" 并且他的分数是:"+sum);
                if(sum < 6){
                    reject.add(tempbid);
                    bid_fortrain.setResult(0);
                }else if(sum>=goodLevel){
                    goodBidScore+=sum;
                    GoodBid.add(tempbid);
                    GoodScore.add(sum);
                    bid_fortrain.setResult(1);
                }else{
                    resultBids.add(tempbid);
                    bid_fortrain.setResult(0);
                }
                //持久化
                bidDao.insert(bid_fortrain);
                if(!InNegotiation){
                    this.initConsult(tempbid.getRoom().getLandlordId(),user.getName(),user.getEconomic(),sum,tempbid.getPrice(),agent);
                }else{
                    this.setConsult(tempbid.getRoom().getLandlordId(),user.getEconomic(),sum,tempbid.getPrice(),agent);
                }

            }
        }else{
        }

        if(!InNegotiation){
            if(GoodBid.size()==1){          //只有唯一的好的Bid
                return null;
            }else if(GoodBid.size()>1){     //有多个好的Bid
                goodLevel = goodBidScore/(GoodBid.size());
                return resultBids;
            }else if(GoodBid.size()==0){        //没有好的Bid
                if(resultBids.size()==0){       //全部拒绝
                    return null;
                }else{
                    goodLevel=11;
                    return resultBids;
                }
            }
        }else{
            if(resultBids.size()!=0){
                return resultBids;
            }else{
                return null;
            }
        }

        return null;
    }

    public List getReject() {
        return reject;
    }

    public List getGoodBid() {
        return GoodBid;
    }

    public List getGoodScore() {
        return GoodScore;
    }

    public void setGoodScore(List goodScore) {
        GoodScore = goodScore;
    }

    //fill the hashmap
    private void fill_hashmap(){
        roomPoint.put(RoomType.Standard,1);
        roomPoint.put(RoomType.Superior,3);
        roomPoint.put(RoomType.Special,2);
        roomPoint.put(RoomType.Business,4);
        roomPoint.put(RoomType.Deluxe,5);
    }

    private void initConsult(int landlordid, String tenantName, String economy, double score, int price, jade.core.Agent agent){
        landlord lord = landlordDao.findlandlordByid(landlordid);
        int minReduction = 0;
        int maxReduction = (price>=init_minPrice)?(((price-init_minPrice)*100)/price):5;
        if(maxReduction == 0){
            maxReduction = 10;
        }
        if(economy.equals("poor")){
            minReduction = maxReduction/3+1;
        }else if(economy.equals("normal")){
            minReduction = maxReduction/5+1;
        }else if(economy.equals("rich")){
            minReduction = 1;
        }
        System.out.println("最大降价幅度："+maxReduction+" 最小降价幅度:"+minReduction);

        String level = "";
        if(score>=goodLevel){
            level = "good";
        }else if(score<6){
            level = "bad";
        }else{
            level = "middle";
        }
        Consult consult = new Consult(tenantName,lord.getLandlordname(),minReduction,maxReduction,0,level,price);
        java.util.List<Consult> consults = new java.util.ArrayList<Consult>();
        consults.add(consult);
        ((tenantAgent)agent).setConsult(landlordid,consults);
    }

    private void setConsult(int landlordid, String economy, double score, int price, jade.core.Agent agent){
        java.util.List<Consult> consults = ((tenantAgent)agent).getConsult(landlordid);
        Consult oneConsult = consults.get(consults.size() - 1);
        int minReduction = 0;
        int maxReduction = (price>=init_minPrice)?(((price-init_minPrice)*100)/price):5;
        if(maxReduction == 0){
            maxReduction = 10;
        }
        if(economy.equals("poor")){
            minReduction = maxReduction/3+1;
        }else if(economy.equals("normal")){
            minReduction = maxReduction/5+1;
        }else if(economy.equals("rich")){
            minReduction = 1;
        }
        System.out.println("最大降价幅度："+maxReduction+"  最小降价幅度:"+minReduction);

        String level = "";
        if(score>=goodLevel){
            level = "好";
        }else if(score<6){
            level = "差";
        }else{
            level = "中";
        }
        Consult result = new Consult(oneConsult.getTenantName(), oneConsult.getLandlordName(), minReduction, maxReduction, 0, level, price);
        consults.add(result);
        ((tenantAgent)agent).setConsult(landlordid,consults);
    }


    private void saveBid(int tenantid,String name,int result){
        bid saveobject = new bid();
    }

}

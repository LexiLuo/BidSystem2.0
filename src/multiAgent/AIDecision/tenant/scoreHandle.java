package multiAgent.AIDecision.tenant;

import DO.bid;
import DO.landlord;
import DO.tenant;
import VO.Consult;
import dao.daoImpl.landlordDao;
import jade.core.Agent;
import jade.util.leap.List;
import multiAgent.AIDecision.tenant.calScore.CalPoints;
import multiAgent.AIDecision.tenantData;
import multiAgent.agent.tenantAgent;
import multiAgent.ontology.Bid;
import multiAgent.ontology.Order;
import multiAgent.ontology.Room;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by H77 on 2017/9/5.
 * 该channel的职责是量化不同bid的特征维度
 */
public class scoreHandle extends channel {
    CalPoints cal;
    Agent agent;
    tenant user;
    private int init_maxPrice = 0;
    private int init_minPrice = 0;
    private int init_avePrice = 0;
    ArrayList<bid> trainBids;
    public scoreHandle(CalPoints cal ,Agent agent ,tenant user){
        this.cal = cal;
        this.agent = agent;
        this.user = user;
    }

    public tenantData handle(List bids , Order order ,tenantData data) {
        //初始化价格
        init(bids);
        for(int i = 0; i < bids.size(); i++){
            Bid b =(Bid)bids.get(i);
            Room room = b.getRoom();
            bid bidFortrain = new bid();
            cal.setBid(bidFortrain);

            bidFortrain.setLandlordid(room.getLandlordId());
            bidFortrain.setRoomid(room.getRoomId());
            bidFortrain.setOrderid(b.getId());

            double priceScore = cal.calPrice(init_maxPrice,init_minPrice,init_avePrice,b.getPrice());
            double facilityScore = cal.calFacility(b.getFacilities(),order.getFacilities());
            double siteScore = cal.calsite(b.getAroundsites());
            double sum = priceScore+facilityScore+siteScore;
            sum = new BigDecimal(sum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            bidFortrain.setPrice((int)(priceScore*100));
            bidFortrain.setArroundsite(siteScore*100+"");
            bidFortrain.setFacility(facilityScore*100+"");
            bidFortrain.setScore(sum*100+"");
            bidFortrain.setRoomtype("1");
            System.out.println("竞标书 ID 是"+b.getLandlordId().getName() +" 并且他的分数是:"+sum);
            setConsult(b,sum,data);
            trainBids.add(bidFortrain);
        }
        data.setTrainBids(trainBids);
        return data;
    }

    public void init(List bids){
        //初始化价格
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
        //初始化训练集合
        trainBids = new ArrayList<bid>();
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
    public void setUser(tenant user) {
        this.user = user;
    }

    public void setConsult(Bid b, double score , tenantData data){

        int landlordid = b.getRoom().getLandlordId();
        landlord lord = landlordDao.findlandlordByid(landlordid);
        int minReduction = 0;
        int price = b.getPrice();
        int maxReduction = (price>=init_minPrice)?(((price-init_minPrice)*100)/price):5;
        if(maxReduction == 0){
            maxReduction = 10;
        }
        String economy = user.getEconomic();
        if(economy.equals("poor")){
            minReduction = maxReduction/3+1;
        }else if(economy.equals("normal")){
            minReduction = maxReduction/5+1;
        }else if(economy.equals("rich")){
            minReduction = 1;
        }
        System.out.println("最大降价幅度："+maxReduction+" 最小降价幅度:"+minReduction);
        String level = "";
        if(score>= data.getGoodLevel()){
            level = "good";
        }else if(score < 6){
            level = "bad";
        }else{
            level = "middle";
        }
        Consult consult = new Consult(user.getName(),lord.getLandlordname(),minReduction,maxReduction,0,level,price);
        java.util.List<Consult> consults =
                ((tenantAgent)agent).getConsult(landlordid) == null ? new ArrayList<Consult>() : ((tenantAgent)agent).getConsult(landlordid);
        consults.add(consult);
        ((tenantAgent)agent).setConsult(landlordid,consults);
    }
}

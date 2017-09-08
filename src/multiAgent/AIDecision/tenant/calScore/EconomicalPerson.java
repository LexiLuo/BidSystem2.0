package multiAgent.AIDecision.tenant.calScore;

import DO.bid;
import jade.util.leap.List;
import multiAgent.ontology.MapObject;
import multiAgent.ontology.MapObjects;

import java.math.BigDecimal;

/**
 * Created by sam on 2017/5/25.
 */
public class EconomicalPerson implements CalPoints {


    bid bid;

    public  EconomicalPerson(){
    }
    public DO.bid getBid() {
        return bid;
    }

    public void setBid(DO.bid bid) {
        this.bid = bid;
    }


//    DecimalFormat df   = new DecimalFormat("######0.00");

    //max 1
    //average 3
    //min 6
    //在max average之间算分策略  3-(targetPrice-average)*（3-1）/(max-average)
    //avrage min 之间算分策略  6-（targetPrice-min)*(6-3)/(average-min)
    public double calPrice(int max, int min, int average, int targetPrice) {   //max 6 points
        double score = 1.0;
        if(targetPrice < average){
            double rate = (targetPrice-min)*100/(average -min);
            score = 6.0 - rate * 3.0 / 100.0;
        }else if (targetPrice > average){
            double rate = (targetPrice-average)*100/(max - average);
            score = 3.0 - rate*2.0/ 100.0;
        }else{
            score = 3.0;
        }
        double result = new BigDecimal(score).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        bid.setPrice(targetPrice);
        return result;
    }

//    public int calRoom(String bidRoomtype, String orderRoomType,HashMap<RoomType,Integer> roomPoint) {  //max 3 points
//        RoomType roomType = RoomType.valueOf(bidRoomtype);
//        int getInt = roomPoint.get(roomType);
//        RoomType order = RoomType.valueOf(orderRoomType);
//        int orderInt = roomPoint.get(order);
//        bid.setRoomtype(orderRoomType);
//        if(getInt == orderInt){
//            return 2;
//        }else if(getInt > orderInt){
//            return 3;
//        }else{
//            return 1;
//        }
//    }
    //一个facilities 用一个int表示，目前只有8种类型的facility
    // 低8位分别按顺序对应wifi,park,cooking,breakfast,toilet,aircodition,laundry,morningcall 分别表示这种类型的facilities是否有
    // 后8位在前8位的基础上，用来表示该种facilities是否是属于order需要的
    // 算分策略  2：1 ,假设潜在facilities n   order需要的为m
    // order分 : 2/m    额外分： 1/(n-m)
    public double calFacility(List bidFacilities, List orderFacilities) {  //max 4 points
          int num = 8;
          int orderSize = orderFacilities.size();
          int extraSize = num - orderSize;
          double order_score = 2.8 / orderSize;
          double extra_score = 1.2 / extraSize;
          double score = 0;
          String isOrder = "";
          String facilitys ="";
          for(int i = 0 ; i < bidFacilities.size() ; i++){
               String facility = (String)bidFacilities.get(i);
               facilitys = facilitys+facility+":";
               if(orderFacilities.contains(facility)){
                   score += order_score;
                   isOrder = isOrder+"1";
               }else{
                   isOrder = isOrder+"0";
                   score += extra_score;
               }
          }
          String str = isOrder+"-"+facilitys;
          bid.setFacility(str);
        double result = new BigDecimal(score).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result;
    }
    // 一个List aroundSites 用一个int表示 为训练做准备
    // 距离 2000 1000 500 200 对于aroundSites划分4块区域，由一个int表示  一块区域最多可以累计2^8=256个地点
    // 算分策略  0.05 0.1 0.18 0.25
    // 目前最多一共统计20个地点，如果没有该地点不存在就是0分
    public double calsite(List aroundSites) {  //max 5 points
        List bus = null;
        List stores = null;
        for(int i=0;i<aroundSites.size();i++){
            if(((MapObjects)aroundSites.get(i)).getKeyWords().equals("公交")){
                bus = ((MapObjects)aroundSites.get(i)).getObjects();
            }else if(((MapObjects)aroundSites.get(i)).getKeyWords().equals("超市")){
                stores = ((MapObjects)aroundSites.get(i)).getObjects();
            }
        }
        int num1 = 0;
        int num2 = 0;
        int num3 = 0;
        int num4 = 0;
        double score = 0;
        for( int i = 0 ; i < bus.size() ; i++) {
            MapObject area = (MapObject) bus.get(i);
            int distance = Integer.parseInt(area.getDistance());
            if (distance <= 200) {
                score += 0.25;
                num1++;
            } else if (distance <= 500) {
                score += 0.18;
                num2++;
            } else if (distance <= 1000) {
                score += 0.1;
                num3++;
            } else if (distance <= 2000) {
                score += 0.05;
                num4++;
            }
            /*  超过2000m的 算做0分 */
        }
        for( int i = 0 ; i <stores.size() ; i++){
            MapObject area = (MapObject) stores.get(i);
            int distance = Integer.parseInt(area.getDistance());
            if (distance <= 200) {
                score += 0.25;
                num1++;
            } else if (distance <= 500) {
                score += 0.18;
                num2++;
            } else if (distance <= 1000) {
                score += 0.1;
                num3++;
            } else if (distance <= 2000) {
                score += 0.05;
                num4++;
            }
            /*  超过2000m的 算做0分 */
        }
        String arrounsite = num4+":"+num3+":"+num2+":"+num1;
        bid.setArroundsite(arrounsite);
        double result = new BigDecimal(score).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result;
    }

}

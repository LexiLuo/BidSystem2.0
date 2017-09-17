package multiAgent.AIDecision.tenant.calScore;

import DO.bid;
import jade.util.leap.List;
import multiAgent.ontology.MapObject;
import multiAgent.ontology.MapObjects;

import java.math.BigDecimal;

/**
 * Created by sam on 2017/5/25.
 */
public class ComfortablePerson implements CalPoints {

    //max 1.0
    //average 2.5
    //min 5
    //在max average之间算分策略  1.5-(targetPrice-average)*（1.5-0.5）/(max-average)
    //avrage min 之间算分策略  3-（targetPrice-min)*(3-1.5)/(average-min)
     bid bid;
    public double calPrice(int max, int min, int average, int targetPrice) {   //max 5 points

        double score = 2.5;
        if(targetPrice < average){
            double rate = (targetPrice-min)*100/(average -min);
            score = 5 - rate*2.5/100.0;
        }else if(targetPrice > average){
            double rate = (targetPrice-average)*100/(max - average);
            score = 2.5 - rate*1.5/100.0;
        }else{
            score = 2.5;
        }
        bid.setPrice(targetPrice);
//        double result = new BigDecimal(score).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return score;
    }

//    public int calRoom(String bidRoomtype, String orderRoomType,HashMap<RoomType,Integer> roomPoint) {  //max 4 points
//        RoomType roomType = RoomType.valueOf(bidRoomtype);
//        int getInt = roomPoint.get(roomType);
//        RoomType order = RoomType.valueOf(orderRoomType);
//        int orderInt = roomPoint.get(order);
//        bid.setRoomtype(orderRoomType);
//        if(getInt == orderInt){
//            return 3;
//        }else if(getInt > orderInt){
//            return 4;
//        }else{
//            return 1;
//        }
//    }
    // 算分策略  2.5：1.5 ,假设潜在facilities n   order需要的为m
    // order分 : 2.5/m    额外分： 1.5/(n-m)
    public double calFacility(List bidFacilities, List orderFacilities) {  //max 5 points
        int num = 8;
        int orderSize = orderFacilities.size();
        int extraSize = num - orderSize;
        double order_score = 3.6 / orderSize;
        double extra_score = 1.4 / extraSize;
        double score = 0;
        String isOrder = "";
        String facilitys ="";
        for(int i = 0 ; i < bidFacilities.size() ; i++){
            String facility = (String)bidFacilities.get(i);
            facilitys = facilitys+facility+":";
            if(orderFacilities.contains(facility)){
                score = score + order_score;
                isOrder = isOrder+"1";
            }else{
                score = score + extra_score;
                isOrder = isOrder+"0";
            }
        }
        String str = isOrder+"-"+facilitys;
        bid.setFacility(str);
//        double result = new BigDecimal(score).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return score;
    }
    //距离  2000 1000 500 200    对于aroundSites划分4块区域，由一个int表示  一块区域最多可以累计2^8=256个地点
    //      0.04 0.08 0.18 0.25
    //目前一共最多20个，如果没有 就是0分
    //算分策略  4/20=0.2   最优分
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
                score += 0.08;
                num3++;
            } else if (distance <= 2000) {
                score += 0.04;
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
                score += 0.08;
                num3++;
            } else if (distance <= 2000) {
                score += 0.04;
                num4++;
            }
            /*  超过2000m的 算做0分 */
        }
        String arrounsite = num4+":"+num3+":"+num2+":"+num1;
        bid.setArroundsite(arrounsite);
        double result = new BigDecimal(score).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result;
    }

    public void setBid(bid bid) {
        this.bid = bid;
    }
}

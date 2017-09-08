package multiAgent.AIDecision.tenant.calScore;

import jade.util.leap.List;
import multiAgent.ontology.RoomType;

import java.util.HashMap;

/**
 * Created by sam on 2017/5/25.
 */
public interface CalPoints {
    //to calculate the score of price
    public double calPrice(int max,int min,int average,int targetPrice);

    //to calculate the score of facilities
    public double calFacility(List bidFacilities,List orderFacilities);

    //to calculate the score of aroundSites
    public double calsite(List aroundSites);

    public void setBid(DO.bid bid);
}

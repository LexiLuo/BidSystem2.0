package multiAgent.AIDecision.tenant;

import DO.bid;
import jade.core.AID;
import jade.util.leap.List;
import multiAgent.AIDecision.tenantData;
import multiAgent.ontology.Bid;
import multiAgent.ontology.Order;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by H77 on 2017/9/5.
 */
public class scoreJudge extends channel{

    public tenantData handle(List bids, Order order, tenantData data) {
        double goodLevel = data.getGoodLevel();
        ArrayList<bid> trainBids = data.getTrainBids();
        HashMap<AID,Integer> status = data.getStatus();
        for(int i = 0 ; i < trainBids.size() ; i++) {
            bid b = trainBids.get(i);
            Bid bb = (Bid)bids.get(i);
            if(!status.containsKey(bb.getLandlordId())) {
                if (goodLevel * 100 < Double.parseDouble(b.getScore())) {
                    status.put(bb.getLandlordId(), 1);
                } else {
                    status.put(bb.getLandlordId(), 0);
                }
            }

        }
        return data;
    }
}

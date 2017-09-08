package multiAgent.AIDecision.tenant;

import DO.bid;
import jade.core.AID;
import jade.util.leap.List;
import multiAgent.AIDecision.tenant.channel;
import multiAgent.AIDecision.tenantData;
import multiAgent.AIDecision.tenant_ScoreAI;
import multiAgent.ontology.Bid;
import multiAgent.ontology.Order;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by H77 on 2017/9/5.
 * 该类是randomForest，用于预测是否还价或者接受竞标书，具有一定的学习能力
 */
public class forestPredict extends channel {

    tenant_ScoreAI forest;
    public tenantData handle(List bids, Order order, tenantData data) {
        ArrayList<bid> trains = data.getTrainBids();
        HashMap<AID,Integer> status = data.getStatus();
        for(int i = 0 ; i < trains.size() ;i++){
            bid b = trains.get(i);
            Bid bid = (Bid)bids.get(i);
            //不是被拒绝的bid
            if(!status.containsKey(bid.getLandlordId())){
                int result = forest.predict(b);
                status.put(bid.getLandlordId(),result);
            }
        }
        return data;
    }
    public tenant_ScoreAI getForest() {
        return forest;
    }

    public void setForest(tenant_ScoreAI forest) {
        this.forest = forest;
    }
}

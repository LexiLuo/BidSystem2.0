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
 * 该步骤的目的是过滤掉数据特征偏差较大或总体离预期较远的bid
 */
public class bidFilter extends channel {

    HashMap<AID,Integer> status; //1 接受  0 还价 2 拒绝
    public tenantData handle(List bids, Order order, tenantData data) {

        init();
        ArrayList<bid>  trains = data.getTrainBids();
        int length = trains.size();
        for(int i = 0 ; i < length ; i++){
            //设置reject的订单的状态
            bid b = trains.get(i);
            Bid bid = (Bid)bids.get(i);
            if(totalFail(Double.parseDouble(b.getScore())) || individualFail(b))  status.put(bid.getLandlordId(),2);
        }
        data.setStatus(status);
        return data;
    }
    public void init(){
        status = new HashMap<AID, Integer>();
    }

    public boolean totalFail(double score){
        if(score < 750)  return false;
        return true;
    }
    public boolean individualFail(bid b){
        if(b.getPrice() < 200 || Double.parseDouble(b.getFacility()) < 250 || Double.parseDouble(b.getArroundsite()) < 200) return false;
        return true;
    }

}

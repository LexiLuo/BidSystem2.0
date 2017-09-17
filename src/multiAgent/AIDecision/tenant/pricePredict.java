package multiAgent.AIDecision.tenant;

import DO.bid;
import dao.daoImpl.bidDao;
import jade.core.AID;
import jade.util.leap.List;
import multiAgent.AIDecision.tenantData;
import multiAgent.ontology.Bid;
import multiAgent.ontology.Order;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by H77 on 2017/9/5.
 * 该函数用来价格预测的预处理
 */
public class pricePredict extends channel {
    public tenantData handle(List bids, Order order, tenantData data) {
        int num = data.getNum();
        double goodLevel = data.getGoodLevel();
        double goodScore = goodLevel*num;

        HashMap<AID,Integer> status = data.getStatus();
        ArrayList<bid> trainBids = data.getTrainBids();
        List goodBid = data.getGoods() == null ? new jade.util.leap.ArrayList() : data.getGoods();
        ArrayList<bid> goodTrans = data.getGoodTrains() == null ? new ArrayList<bid>() : data.getGoodTrains();

        List resultBids = new jade.util.leap.ArrayList();
        for(int i = 0 ; i < bids.size() ; i++){
            Bid b = (Bid)bids.get(i);
            bid bb = trainBids.get(i);
            int type = status.get(b.getLandlordId());
            if(type == 0){
                resultBids.add(b);
            }else if( type == 1){
                goodBid.add(b);
                goodTrans.add(bb);
                goodScore = goodScore + Double.parseDouble(bb.getScore())/100;
                num++;
            }
            if(type < 2){
                bb.setResult(type);
                bidDao.insert(bb);
            }
        }
        goodLevel = goodScore / num;
        data.setGoodLevel(goodLevel);
        data.setNum(num);
        data.setGoods(goodBid);
        data.setGoodTrains(goodTrans);
        data.setConsults(resultBids);
        return data;
    }
}

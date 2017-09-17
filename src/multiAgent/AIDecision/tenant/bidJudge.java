package multiAgent.AIDecision.tenant;

import DO.bid;
import dao.daoImpl.bidDao;
import jade.util.leap.List;
import multiAgent.AIDecision.tenantData;
import multiAgent.ontology.Order;

import java.util.ArrayList;

/**
 * Created by H77 on 2017/9/5.
 * 在少数据量的时候，根据原有的算法进行判别；
 * 在数据量到达一定的量可以使用forest的时候 通过forest进行判断
 */
public class bidJudge extends channel {
    channel forest;
    channel scoreJudge;
    public bidJudge(channel forest , channel scoreJudge){
        this.forest = forest;
        this.scoreJudge = scoreJudge;
    }
    public tenantData handle(List bids, Order order, tenantData data) {
        ArrayList<bid> trainBids =(ArrayList<bid>) bidDao.selectAllBids();
        if(trainBids.size() < 1000){
          data = scoreJudge.handle(bids,order,data);
        }else{
          data = forest.handle(bids,order,data);
        }
        return data;
    }
}

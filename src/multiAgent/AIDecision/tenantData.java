package multiAgent.AIDecision;

import DO.bid;
import jade.core.AID;
import jade.util.leap.List;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by H77 on 2017/9/5.
 */
public class tenantData {

    ArrayList<bid> trainBids;
    HashMap<AID,Integer> status;
    double goodLevel;
    int num;

    List consults;
    List goods;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double getGoodLevel() {
        return goodLevel;
    }

    public void setGoodLevel(double goodLevel) {
        this.goodLevel = goodLevel;
    }

    public void setTrainBids(ArrayList<bid> trainBids) {
        this.trainBids = trainBids;
    }

    public ArrayList<bid> getTrainBids() {
        return trainBids;
    }

    public HashMap<AID, Integer> getStatus() {
        return status;
    }

    public void setStatus(HashMap<AID, Integer> status) {
        this.status = status;
    }

    public List getConsults() {
        return consults;
    }

    public void setConsults(List consults) {
        this.consults = consults;
    }

    public List getGoods() {
        return goods;
    }

    public void setGoods(List goods) {
        this.goods = goods;
    }

}

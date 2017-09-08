package multiAgent.AIDecision.landlord.factor;

import DO.room;
import dao.daoImpl.roomDao;
import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.ontology.Order;

/**
 * Created by H77 on 2017/7/31.
 */
public class limitFactor extends factor {
     //判断是否还有协商的必要
    public Reduction judge(Order order, Reduction reduction) {

        room room = roomDao.findRoomByLandlordAndType(lord.getLandlordid(), order.getRoomType());
        // 空房率
        double availability = room.getRestnum() * 1.00 / room.getTotalnum();
        double minDegree = reduction.getMinDegree() / (availability / reduction.getVacancy());
        minDegree = minDegree > reduction.getMinDegree() ? reduction.getMinDegree() : minDegree;
        if(reduction.getCurrent_Price() / room.getPrice() < minDegree){
            //停止谈判
            reduction.setOn(false);
            reduction.setReduction(false);
        }

        return reduction;
    }
}

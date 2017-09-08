package multiAgent.AIDecision.landlord.factor;

import DO.room;
import dao.daoImpl.roomDao;
import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.ontology.Order;

/**
 * Created by H77 on 2017/7/24.
 */
public class vacancyFactor extends factor {
    public Reduction judge(Order order, Reduction reduction) {

        room room = roomDao.findRoomByLandlordAndType(lord.getLandlordid(), order.getRoomType());
        // 空房率
        double availability = room.getRestnum() * 1.00 / room.getTotalnum();

        double minDegree = reduction.getMinDegree() / (availability / reduction.getVacancy());
        minDegree = minDegree > reduction.getMinDegree() ? reduction.getMinDegree() : minDegree;

        double maxDegree = reduction.getMaxDegree() / (availability / reduction.getVacancy());
        maxDegree = maxDegree > reduction.getMaxDegree() ? reduction.getMaxDegree() : maxDegree;

        int current_price = reduction.getCurrent_Price();
        int init_price = room.getPrice();
        reduction.setOn(false);
        if ((current_price * 1.0 / init_price) > maxDegree) {
            //接收降价，降价额为最高降价幅度
            int reduction_int = (int) ( reduction.getMaxReduction()/ (2 - availability));
            reduction.setReduction(reduction_int);
            reduction.setReduction(true);
        } else if ((current_price * 1.0 / init_price) > minDegree) {
            //接收降价，降价额为最低降价幅度
            int reduction_int = (int) ((1 + availability / 2) * reduction.getMinReduction());
            reduction.setReduction(reduction_int);
            reduction.setReduction(true);
        } else {
            //不接受降价
            reduction.setReduction(false);
        }
        return reduction;
    }
}

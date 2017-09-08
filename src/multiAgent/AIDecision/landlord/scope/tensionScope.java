package multiAgent.AIDecision.landlord.scope;

import DO.room;
import dao.daoImpl.roomDao;
import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.scopeFactor;
import multiAgent.ontology.Order;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by H77 on 2017/8/9.
 */
public class tensionScope extends scopeFactor {
    public Reduction scopeJudge(Order order, Reduction reduction) {
        Date start = order.getStartTime();
        Date end = order.getEndTime();
        room room = roomDao.findRoomByLandlordAndType(lord.getLandlordid(), order.getRoomType());
        int init_price = room.getPrice();
        int current_price = reduction.getCurrent_Price();

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(start);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(end);
        int days = cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR);

        if(days >= 4 && current_price*1.0 / init_price > 0.9){
            reduction.setMaxReduction(reduction.getMaxReduction()*1.1);
            reduction.setMinReduction(reduction.getMinReduction()*1.15);
        }else if(days >= 3 && current_price*1.0 / init_price > 0.8){
            reduction.setMaxReduction(reduction.getMaxReduction()*1.05);
            reduction.setMinReduction(reduction.getMinReduction()*1.1);
        }else if(current_price*1.0 / init_price < 0.7){
            reduction.setOn(false);
            reduction.setReduction(false);
        }else{
            //暂时对上述3种情况需要处理减价情况
        }
        return reduction;
    }
}

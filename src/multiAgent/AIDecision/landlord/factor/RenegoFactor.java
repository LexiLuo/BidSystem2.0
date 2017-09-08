package multiAgent.AIDecision.landlord.factor;

import DO.room;
import dao.daoImpl.roomDao;
import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.ontology.Order;

/**
 * Created by H77 on 2017/8/6.
 */
public class RenegoFactor extends factor {
    int count = 1;
    public Reduction judge(Order order, Reduction reduction) {
        if(count >1){
            room room = roomDao.findRoomByLandlordAndType(lord.getLandlordid(), order.getRoomType());
            double availability = room.getRestnum() * 1.00 / room.getTotalnum();
            double minDegree = reduction.getMinDegree() / (availability / reduction.getVacancy());
            minDegree = minDegree > reduction.getMinDegree() ? reduction.getMinDegree() : minDegree;
            double maxDegree = reduction.getMaxDegree() / (availability / reduction.getVacancy());
            maxDegree = maxDegree > reduction.getMaxDegree() ? reduction.getMaxDegree() : maxDegree;
            int current_price = reduction.getCurrent_Price();
            int init_price = room.getPrice();
            String s = lord.getCharacteristic();
            if(s.equals("Amiable")){
                if ((current_price * 1.0 / init_price) > minDegree) {
                    //接收降价，降价额为最低降价幅度
                    int reduction_int = (int) ((1 + availability / 2) * reduction.getMinReduction());
                    reduction.setReduction(reduction_int);
                    reduction.setReduction(true);
                }else{
                    reduction.setReduction(false);
                }
            }else if(s.equals("Promotion")){
                if ((current_price * 1.0 / init_price) > maxDegree) {
                    //接收降价，降价额为最高降价幅度
                    int reduction_int = (int) ( reduction.getMaxReduction()/ (2 - availability));
                    reduction.setReduction(reduction_int);
                    reduction.setReduction(true);
                }else if((current_price * 1.0 / init_price) > minDegree){
                    //接收降价，降价额为最低降价幅度
                    int reduction_int = (int) ((1 + availability / 2) * reduction.getMinReduction());
                    reduction.setReduction(reduction_int);
                    reduction.setReduction(true);
                }
            }else{
                reduction.setReduction(false);
            }
            reduction.setOn(false);
        }
        count++;
        return reduction;
    }
}

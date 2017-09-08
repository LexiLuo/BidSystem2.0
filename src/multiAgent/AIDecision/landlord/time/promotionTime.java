package multiAgent.AIDecision.landlord.time;

import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.timeFactor;
import multiAgent.ontology.Order;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by H77 on 2017/8/9.
 */
public class promotionTime extends timeFactor {
    public Reduction timeJudge(Order order, Reduction reduction) {
        Date start = order.getStartTime();
        Date end = order.getEndTime();
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(start);
        cal2.setTime(end);
        //关于入住时间的考虑 是双休日还是工作日 0是星期1
        int DayOfWeek = cal1.get(Calendar.DAY_OF_WEEK) - 1;
        int days = cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR);
        int count = 0;
        int weekCount = 0;
        while (count < days) {
            if ((DayOfWeek % 7) == 0 || (DayOfWeek % 7) == 6) weekCount++;
            count++;
            DayOfWeek++;
        }
        double weekendRate = weekCount * 1.0 / days;
        if (weekendRate > 0.6) {
            reduction.setMaxReduction(reduction.getMaxReduction() * 0.95);
        }
        return reduction;
    }
}

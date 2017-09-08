package multiAgent.AIDecision.landlord.factor;

import com.sun.org.apache.xpath.internal.operations.Or;
import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.ontology.Order;
import util.DateUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by H77 on 2017/7/24.
 */
public abstract class timeFactor extends factor {

    public abstract Reduction timeJudge(Order order , Reduction reduction);

    public Reduction judge(Order order, Reduction reduction) {
        reduction = timeJudge(order,reduction);
        return reduction;
    }

}

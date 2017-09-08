package multiAgent.AIDecision.landlord.factor;

import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.ontology.Order;

/**
 * Created by H77 on 2017/7/24.
 */
public abstract class scoreFactor extends factor {

    public abstract Reduction scoreJudge(Order order ,Reduction reduction);

    public Reduction judge(Order order, Reduction reduction) {
        reduction = scoreJudge(order,reduction);
        return reduction;
    }
}

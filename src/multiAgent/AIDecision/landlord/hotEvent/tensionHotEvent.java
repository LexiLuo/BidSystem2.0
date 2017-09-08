package multiAgent.AIDecision.landlord.hotEvent;

import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.hotEventFactor;
import multiAgent.ontology.Order;

/**
 * Created by H77 on 2017/8/9.
 */
public class tensionHotEvent extends hotEventFactor {
    public Reduction hotEventJudge(Order order, Reduction reduction) {
        int i = (int)(Math.random()*100);
        if(i % 10 == 0) {
            hotEvent = true;
            reduction.setHotEvent(hotEvent);
            reduction.setMaxReduction(reduction.getMaxReduction()*0.8);
            reduction.setOn(false);
            reduction.setReduction(true);
        }
        return reduction;
    }
}

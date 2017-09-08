package multiAgent.AIDecision.landlord.score;

import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.scoreFactor;
import multiAgent.ontology.Order;

/**
 * Created by H77 on 2017/8/9.
 */
public class normalScore extends scoreFactor {
    public Reduction scoreJudge(Order order, Reduction reduction) {
        if(Double.parseDouble(lord.getComment()) >= 4.5){
            reduction.setMaxReduction(reduction.getMaxReduction()*0.95);
            reduction.setMinReduction(reduction.getMinReduction()*0.9);
        }else if(Double.parseDouble(lord.getComment()) > 4.0){
            reduction.setMaxReduction(reduction.getMaxReduction()*1.05);
            reduction.setMinReduction(reduction.getMinReduction()*1.1);
        }else{
            reduction.setMaxReduction(reduction.getMaxReduction()*1.1);
            reduction.setMinReduction(reduction.getMinReduction()*1.15);
        }
        return reduction;
    }
}

package multiAgent.AIDecision.landlord.score;

import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.scoreFactor;
import multiAgent.ontology.Order;

/**
 * Created by H77 on 2017/8/9.
 */
public class promotionScore extends scoreFactor {
    public Reduction scoreJudge(Order order, Reduction reduction) {
        if(Double.parseDouble(lord.getComment()) >= 4.5){

        }else if(Double.parseDouble(lord.getComment()) > 4.0){
            reduction.setMaxReduction(reduction.getMaxReduction()*1.08);
            reduction.setMinReduction(reduction.getMinReduction()*1.1);
        }else{
            reduction.setMaxReduction(reduction.getMaxReduction()*1.12);
            reduction.setMinReduction(reduction.getMinReduction()*1.13);
        }
        return reduction;
    }
}

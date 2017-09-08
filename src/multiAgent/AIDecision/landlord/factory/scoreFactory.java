package multiAgent.AIDecision.landlord.factory;

import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.AIDecision.landlord.scope.normalScope;
import multiAgent.AIDecision.landlord.scope.promotionScope;
import multiAgent.AIDecision.landlord.scope.tensionScope;
import multiAgent.AIDecision.landlord.score.normalScore;
import multiAgent.AIDecision.landlord.score.promotionScore;
import multiAgent.AIDecision.landlord.score.tensionScore;

/**
 * Created by H77 on 2017/8/9.
 */
public class scoreFactory {
    public static factor getInstance(String type){
        factor f;
        if("Tension".equals(type)){
            f = new tensionScore();
        }else if("Promotion".equals(type)){
            f = new promotionScore();
        }else{
            f = new normalScore();
        }
        return f;
    }
}

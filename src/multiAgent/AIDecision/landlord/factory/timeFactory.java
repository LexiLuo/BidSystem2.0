package multiAgent.AIDecision.landlord.factory;

import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.AIDecision.landlord.scope.normalScope;
import multiAgent.AIDecision.landlord.scope.promotionScope;
import multiAgent.AIDecision.landlord.scope.tensionScope;
import multiAgent.AIDecision.landlord.time.normalTime;
import multiAgent.AIDecision.landlord.time.promotionTime;
import multiAgent.AIDecision.landlord.time.tensionTime;

/**
 * Created by H77 on 2017/8/9.
 */
public class timeFactory {
    public static factor getInstance(String type){
        factor f;
        if("Tension".equals(type)){
            f = new tensionTime();
        }else if("Promotion".equals(type)){
            f = new promotionTime();
        }else{
            f = new normalTime();
        }
        return f;
    }
}

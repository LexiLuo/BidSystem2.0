package multiAgent.AIDecision.landlord.factory;

import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.AIDecision.landlord.scope.normalScope;
import multiAgent.AIDecision.landlord.scope.promotionScope;
import multiAgent.AIDecision.landlord.scope.tensionScope;

/**
 * Created by H77 on 2017/8/9.
 */
public class scopeFactory {
    public static factor getInstance(String type){
        factor f;
        if("Tension".equals(type)){
           f = new tensionScope();
        }else if("Promotion".equals(type)){
           f = new promotionScope();
        }else{
           f = new normalScope();
        }
        return f;
    }
}

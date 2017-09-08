package multiAgent.AIDecision.landlord.factory;

import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.AIDecision.landlord.hotEvent.normalHotEvent;
import multiAgent.AIDecision.landlord.hotEvent.promotionHotEvent;
import multiAgent.AIDecision.landlord.hotEvent.tensionHotEvent;
import multiAgent.AIDecision.landlord.scope.normalScope;
import multiAgent.AIDecision.landlord.scope.promotionScope;
import multiAgent.AIDecision.landlord.scope.tensionScope;

/**
 * Created by H77 on 2017/8/9.
 */
public class hotEventFactory {
    public static factor getInstance(String type){
        factor f;
        if("Tension".equals(type)){
            f = new tensionHotEvent();
        }else if("Promotion".equals(type)){
            f = new promotionHotEvent();
        }else{
            f = new normalHotEvent();
        }
        return f;
    }
}

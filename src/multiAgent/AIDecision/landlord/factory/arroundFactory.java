package multiAgent.AIDecision.landlord.factory;

import multiAgent.AIDecision.landlord.arround.normalArround;
import multiAgent.AIDecision.landlord.arround.promotionArround;
import multiAgent.AIDecision.landlord.arround.tensionArround;
import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.AIDecision.landlord.hotEvent.normalHotEvent;
import multiAgent.AIDecision.landlord.hotEvent.promotionHotEvent;
import multiAgent.AIDecision.landlord.hotEvent.tensionHotEvent;

/**
 * Created by H77 on 2017/8/9.
 */
public class arroundFactory {
    public static factor getInstance(String type){
        factor f;
        if("Tension".equals(type)){
            f = new tensionArround();
        }else if("Promotion".equals(type)){
            f = new promotionArround();
        }else{
            f = new normalArround();
        }
        return f;
    }
}

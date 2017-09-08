package multiAgent.AIDecision.landlord.factor;

import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.ontology.Order;

/**
 * Created by H77 on 2017/7/31.
 */
public class preferentialFactor extends factor {
    //优惠策略 相当于优惠券
    //促销如果有优惠策略 减上限
    //目前默认都有优惠策略
    boolean firstUse = true;
    public Reduction judge(Order order, Reduction reduction) {
        String economy = lord.getCharacteristic();
        if(firstUse){
            if(economy.equals("Affordable")){
                reduction.setMaxReduction(reduction.getMaxReduction()*1.08);
                reduction.setMinReduction(reduction.getMinReduction()*1.05);
            }else if(economy.equals("Amiable")){
                reduction.setMaxReduction(reduction.getMaxReduction()*1.12);
                reduction.setMinReduction(reduction.getMinReduction()*1.1);
            }else if(economy.equals("Promotion")){
                reduction.setMaxReduction(reduction.getMaxReduction()*1.12);
                reduction.setMinReduction(reduction.getMinReduction()*1.1);
                reduction.setMinDegree(reduction.getMinDegree()*0.95);
                reduction.setMaxDegree(reduction.getMaxDegree()*0.95);
            }
            firstUse = false;
        }
        return reduction;
    }
}

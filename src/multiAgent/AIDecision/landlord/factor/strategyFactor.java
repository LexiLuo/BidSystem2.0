package multiAgent.AIDecision.landlord.factor;

import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.ontology.Order;

/**
 * Created by H77 on 2017/7/24.
 */
public class strategyFactor extends factor {
    public Reduction judge(Order order, Reduction reduction) {
        String economy = lord.getCharacteristic();
        if(economy.equals("Tension")){
            reduction.setOn(false);
            reduction.setReduction(false);
        }else if(economy.equals("Affordable")){
            reduction.setMinDegree(0.9);
            reduction.setMaxDegree(0.95);
            reduction.setVacancy(0.6);
        }else if(economy.equals("Amiable")){
            reduction.setMinDegree(0.85);
            reduction.setMaxDegree(0.92);
            reduction.setVacancy(0.55);
        }else if(economy.equals("Promotion")){
            reduction.setMinDegree(0.78);
            reduction.setMaxDegree(0.85);
            reduction.setVacancy(0.5);
        }
        return reduction;
    }
}

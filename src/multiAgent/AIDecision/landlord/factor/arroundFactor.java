package multiAgent.AIDecision.landlord.factor;

import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.ontology.MapObject;
import multiAgent.ontology.Order;
import util.MapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by H77 on 2017/7/24.
 */
public abstract class arroundFactor extends factor {

    public abstract Reduction arroundJudge(Order order , Reduction reduction);

    public Reduction judge(Order order, Reduction reduction) {
        reduction = arroundJudge(order,reduction);
        return reduction;
    }
}

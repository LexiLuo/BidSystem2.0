package multiAgent.AIDecision.landlord.factor;

import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.ontology.Order;


/**
 * Created by H77 on 2017/8/6.
 */
public abstract class hotEventFactor extends factor {
    //周围是否有热门活动 可以导致不降价 或者升价
    //对于房客方也需要考虑这个因素
    boolean firstUse = true;
    public boolean hotEvent = false;

    public abstract Reduction hotEventJudge(Order order , Reduction reduction);

    public Reduction judge(Order order, Reduction reduction) {
        if(firstUse){
            reduction = hotEventJudge(order,reduction);
        }
        return reduction;
    }
}

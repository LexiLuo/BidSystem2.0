package multiAgent.AIDecision.tenant;

import DO.bid;
import jade.util.leap.List;
import multiAgent.AIDecision.tenantData;
import multiAgent.ontology.Order;

/**
 * Created by H77 on 2017/9/5.
 */
public abstract class channel {
    public abstract tenantData handle(List bids , Order order , tenantData data);
}

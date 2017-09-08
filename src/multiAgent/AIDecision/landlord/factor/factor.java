package multiAgent.AIDecision.landlord.factor;

import DO.landlord;
import multiAgent.AIDecision.Reduction;
import multiAgent.ontology.Order;

/**
 * Created by H77 on 2017/7/24.
 */
public abstract class factor {

    public landlord lord;
    public abstract Reduction judge(Order order, Reduction reduction);

    public landlord getLand() {
        return lord;
    }
    public void setLand(landlord land) {
        this.lord = land;
    }
}

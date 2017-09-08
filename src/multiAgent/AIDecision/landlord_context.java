package multiAgent.AIDecision;

import DO.landlord;
import multiAgent.AIDecision.landlord.factor.*;
import multiAgent.AIDecision.landlord.factory.*;
import multiAgent.ontology.Negotiation;
import multiAgent.ontology.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H77 on 2017/7/24.
 */
public class landlord_context {
    private List<factor> factors;
    private landlord land;
    public landlord_context(landlord land){
        factors = new ArrayList<factor>();
        factor client = new clientFactor();
        client.setLand(land);
        factor limit = new limitFactor();
        limit.setLand(land);
        factor arround = arroundFactory.getInstance(land.getCharacteristic());
        arround.setLand(land);
        factor facility = new facilityFactor();
        facility.setLand(land);
        factor scope = scopeFactory.getInstance(land.getCharacteristic());
        scope.setLand(land);
        factor score = scoreFactory.getInstance(land.getCharacteristic());
        score.setLand(land);
        factor strategy = new strategyFactor();
        strategy.setLand(land);
        factor time = timeFactory.getInstance(land.getCharacteristic());
        time.setLand(land);
        factor vacancy = new vacancyFactor();
        vacancy.setLand(land);
        factor hotEvent = hotEventFactory.getInstance(land.getCharacteristic());
        hotEvent.setLand(land);
        RenegoFactor renego = new RenegoFactor();
        renego.setLand(land);
        factors.add(strategy);
        factors.add(limit);
        factors.add(renego);
        factors.add(hotEvent);
        factors.add(client);
        factors.add(time);
        factors.add(scope);
        factors.add(score);
        factors.add(facility);
        factors.add(arround);
        factors.add(vacancy);
        this.land = land;
    }

    public Negotiation calculate(Negotiation nego , Order order){
        int max = nego.getMaxReduction();
        int min = nego.getMinReduction();
        int current_price = nego.getActualPrice();
        Reduction  reduction = new Reduction(0,max,min,true);
        reduction.setCurrent_Price(current_price);
        for(factor f : factors){
            reduction = f.judge(order,reduction);
            if(!reduction.isOn){
                break;
            }
        }
        if(!reduction.isReduction()) {
            nego.setResult(0);
        }else{
            //先得到计算后的价格
            int redu = reduction.getReduction();
            redu = redu < max ? redu : max;
            redu = redu > min ? redu :min;
            nego.setActualPrice((int) (current_price * (100 - redu) / 100));
            nego.setResult(1);
            //判断用户是否近期有订单
            if(reduction.isPrePrice){
                //如果差距过大 意味着还需要协商，不如设置为近期协商的价格
                double pre = reduction.getPrePrice();
                if(pre / nego.getActualPrice() < 0.8){
                    nego.setActualPrice((int)pre);
                }
            }
        }
        return nego;
    }
}

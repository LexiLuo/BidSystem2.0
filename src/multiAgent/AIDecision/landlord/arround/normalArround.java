package multiAgent.AIDecision.landlord.arround;

import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.arroundFactor;
import multiAgent.ontology.MapObject;
import multiAgent.ontology.Order;
import util.MapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by H77 on 2017/8/9.
 */
public class normalArround extends arroundFactor {
    public Reduction arroundJudge(Order order, Reduction reduction) {
        List<String> keywords = new ArrayList<String>();
        keywords.add("超市");
        keywords.add("公交");
        keywords.add("餐饮");
        Map<String,List<MapObject>> maps = MapUtil.searchAroundSite(keywords,lord.getLongitude().toString(),lord.getLatitude().toString());

        List<MapObject> stores = maps.get("超市");
        List<MapObject> bus = maps.get("公交");
        List<MapObject> foods = maps.get("餐饮");

        int num_store = 0;
        int num_bus = 0;
        int num_food = 0;
        for( int i = 0 ; i < bus.size() ; i++) {
            MapObject area = bus.get(i);
            int distance = Integer.parseInt(area.getDistance());
            if (distance <= 500) {
                num_bus++;
            }
            /*  超过2000m的 算做0分 */
        }
        for( int i = 0 ; i <stores.size() ; i++){
            MapObject area = stores.get(i);
            int distance = Integer.parseInt(area.getDistance());
            if(distance <= 300){
                num_store++;
            }
        }
        for( int i = 0 ; i <foods.size() ; i++){
            MapObject area = foods .get(i);
            int distance = Integer.parseInt(area.getDistance());
            if(distance <= 300){
                num_food++;
            }
        }
        if(num_bus >= 5 && num_store >=5 && num_food >=5){
            reduction.setMaxReduction(reduction.getMaxReduction()*0.9);
            reduction.setMinReduction(reduction.getMinReduction()*0.95);
        }else if(num_bus >= 3 && num_store >=3 && num_food >=3){
            reduction.setMaxReduction(reduction.getMaxReduction()*0.95);
        }else{
            reduction.setMaxReduction(reduction.getMaxReduction()*1.05);
            reduction.setMinReduction(reduction.getMinReduction()*1.05);
        }

        return reduction;
    }
}

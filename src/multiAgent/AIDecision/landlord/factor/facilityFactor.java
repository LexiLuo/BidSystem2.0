package multiAgent.AIDecision.landlord.factor;

import DO.tenant;
import dao.daoImpl.tenantDao;
import jade.util.leap.List;
import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.ontology.Order;

/**
 * Created by H77 on 2017/7/24.
 */
public class facilityFactor extends factor {

    public Reduction judge(Order order, Reduction reduction) {
        List facilities = order.getFacilities();
        String str = lord.getFeature();
        String client = order.getCustomer();
        tenant t = tenantDao.getTenantByName(client);

        boolean result = true;
        for(int i = 0 ; i < facilities.size() ; i++){
            if(!str.contains((String)facilities.get(i))){
                result = false;
                break;
            }
        }
        if(!result){
            //不满足 需要考虑用户特点
            if(t.getPreference().equals("economical")) {
                reduction.setMaxReduction(reduction.getMaxReduction() * 1.1);
                reduction.setMinReduction(reduction.getMinReduction() * 1.15);
            }else if(t.getPreference().equals("comfortable")){
                //竞争性比较差 少降点看情况
                reduction.setMaxReduction(reduction.getMaxReduction() * 1.05);
                reduction.setMinReduction(reduction.getMinReduction() * 1.1);
            }
        }else{
            //都满足 说明竞争性强
            reduction.setMaxReduction(reduction.getMaxReduction()*0.95);
            reduction.setMinReduction(reduction.getMinReduction()*0.9);
        }

        return reduction;
    }
}

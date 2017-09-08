package multiAgent.AIDecision.landlord.factor;

import DO.tender;
import dao.daoImpl.tenderDao;
import multiAgent.AIDecision.Reduction;
import multiAgent.AIDecision.landlord.factor.factor;
import multiAgent.ontology.Order;

import java.util.List;

/**
 * Created by H77 on 2017/7/31.
 */
public class clientFactor extends factor {
    public Reduction judge(Order order, Reduction reduction) {
        String economy = lord.getCharacteristic();
        int landlordid = lord.getLandlordid();
        String client = order.getCustomer();
        List<tender> tenders = tenderDao.findTenders(landlordid,client);
        if(tenders == null || tenders.size() == 0) return reduction;
        //如果是常客需要考虑近期的消费记录
        int count = 0;
        double prePrice = 0;
        for(tender t : tenders){
            if(order.getRoomType().equals(t.getRoomtype())){
                count++;
                prePrice += t.getPrice();
            }
        }
        //在降价协商的时候 需要考虑之前的价格 精准降价 偏离幅度不能太大
        prePrice = prePrice /count;
        reduction.setPrePrice(true);
        reduction.setPrePrice(prePrice);
        //先默认Amiable Promotion类型的 对于新房客 具有一定的促销比率
        if(economy.equals("Amiable")){
            reduction.setMaxReduction(reduction.getMaxReduction()*1.05);
            reduction.setMinReduction(reduction.getMinReduction()*1.05);
        }else if(economy.equals("Promotion")){
            reduction.setMaxReduction(reduction.getMaxReduction()*1.1);
            reduction.setMinReduction(reduction.getMinReduction()*1.12);
        }
        return reduction;
    }
}

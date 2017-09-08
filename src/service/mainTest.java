package service;

import DO.tenant;
import DO.tender;
import VO.BidInfo;
import VO.OrderInfo;
import dao.daoImpl.tenderDao;
import jade.core.AID;
import jade.util.leap.ArrayList;
import jade.util.leap.List;
import multiAgent.ontology.Order;
import service.impl.managerServiceImpl;
import service.impl.tenantServiceImpl;

import java.util.Date;

/**
 * Created by H77 on 2017/5/15.
 */
public class mainTest {

    public static void main(String[] args){
         tenantService tenant = tenantServiceImpl.getInstance();
//        user user = new user("h77","123456","male","student",18);
//         tenant.createTenant(user);
         managerService manager = managerServiceImpl.getInstance();
         manager.initSystem();
         tenant user = tenant.findTenant(1);
         Date dStart = new Date(117,8,1);
         Date dEnd = new Date(117,8,3);
         Date dCreate = new Date(117,7,31);
         List facility = new ArrayList();
         facility.add("wifi");
         facility.add("park");
//         facility.add("aircodition");
         facility.add("toilet");
         OrderInfo order = new OrderInfo(user.getId(),
                 "浙江省慈溪市附海镇",
                 "Business",
                 "Standard",
                 1,
                 100,
                 600,
                 facility,
                 dStart,
                 dEnd);
//         OrderInfo order = new OrderInfo(user.getId(),
//                 "南京市南大",
//                 "tourism",
//                 "Superior",
//                 1,
//                 230,
//                 750,
//                 facility,
//                 dStart,
//                 dEnd);

//         java.util.List<tender> tenders = tenderDao.findTenders(4,"hqq");

         tenant.createAgent(user.getId());
         java.util.List<BidInfo> bids =  tenant.Order(user.getName(),order);
         for(BidInfo bid : bids){
              System.out.println(bid.toString());
         }
    }
}

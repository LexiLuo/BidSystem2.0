package multiAgent.behavior.listener;

import DO.landlord;
import DO.tenant;
import VO.BidInfo;
import VO.Consult;
import dao.daoImpl.landlordDao;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.ArrayList;
import multiAgent.AIDecision.tenantData;
import multiAgent.agent.tenantAgent;
import multiAgent.agentHelper.ValueCal;
import multiAgent.behavior.message.negotiation;
import multiAgent.ontology.*;
import jade.util.leap.List;
import service.common.agentHandler;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by H77 on 2017/5/6.
 */
public class tenantListener extends CyclicBehaviour {

    private Codec codec = new SLCodec();
    private Ontology ontology = BidOntology.getInstance();
    private int responseNum = 0;
    private int lowerPriceNum = 0;
    private int currentResponse = 0;
    private int status = 0;  // 0表示初始状态，n表示第n次协商，Agent之间采取三次协商协议
    private List bids = new ArrayList();            //用于下次的negotiation
    private List finalBids = new ArrayList();       //不再降价的bid
    private List allbids = new ArrayList();         //用于存储全部的Bid信息
    private Map<AID, Bid> mapped = new HashMap<AID, Bid>();
    private ValueCal cal = null;
    private tenantAgent agent;

    public tenantListener(Agent agent, ValueCal c) {
        super(agent);
        this.agent = (tenantAgent) agent;
        cal = c;
    }

    public void action() {
        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchLanguage(codec.getName()),
                MessageTemplate.MatchOntology(ontology.getName()));
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.INFORM) {
                //得到返回的房东竞标集合，下一步是与房东之间的智能讨价还价过程
                System.out.println(myAgent.getName() + " 收到筛选结果");
                try {
                    ContentElement ce = myAgent.getContentManager().extractContent(msg);
                    Action act = (Action) ce;
                    if (act.getAction() instanceof OrderResponse) {
                        OrderResponse orderResponse = (OrderResponse) act.getAction();
                        //完成映射表
                        if (orderResponse == null || orderResponse.getBids() == null) {
                            System.out.println("没有符合意向的房源");
                        } else {

                            allbids = orderResponse.getBids();      //保存全部的Bid信息
                            for (int i = 0; i < allbids.size(); i++) {
                                Bid bid = (Bid) allbids.get(i);
                                mapped.put(bid.getLandlordId(), bid);
                            }
                            tenant t = ((tenantAgent) myAgent).getOwner();
                            Order order = ((tenantAgent) myAgent).getOrder(t.getId());

                            tenantData data = agent.getContext().handle(allbids,order);
                            List response = data.getConsults();

                            if (data.getGoods().size() == 1) {
                                java.util.List<BidInfo> resultBids = this.creatBidInfo(data.getGoods());
                                agent.putResult(resultBids);
                                negotiation neg = new negotiation(myAgent,allbids,order.getId(),true);
                                myAgent.addBehaviour(neg);
                            } else if(response.size() == 0){
                                //拒绝了所有的订单
                                System.out.println("竞标房源都不符合条件");
                                negotiation neg = new negotiation(myAgent,allbids,order.getId(),true);
                                myAgent.addBehaviour(neg);
                            }else{
                                responseNum = response.size();
                                myAgent.addBehaviour(new negotiation(myAgent, response, order.getId(),false));
                                status = 1; //进入1次协商
                            }

                        }
                    }
                } catch (Codec.CodecException e) {
                    e.printStackTrace();
                } catch (UngroundedException e) {
                    e.printStackTrace();
                } catch (OntologyException e) {
                    e.printStackTrace();
                }
            } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                //收到房源的回应
                currentResponse++;
                try {
                    ContentElement ce = myAgent.getContentManager().extractContent(msg);
                    Action act = (Action) ce;
                    if (act.getAction() instanceof Negotiation) {
                        Negotiation negotiation = (Negotiation) act.getAction();
                        if (negotiation.getResult() == 1) {       //diminish the price        //and these bids will be calculate scores
                            System.out.println("房源" + msg.getSender().getName() + "接收降价");
                            System.out.println("降低到" + negotiation.getActualPrice());
                            lowerPriceNum++;
                            Bid tempbid = mapped.get(msg.getSender());
                            tempbid.setPrice(negotiation.getActualPrice());
                            mapped.put(msg.getSender(), tempbid);        //覆盖negotiation后的bid
                            this.setConsult(tempbid, negotiation.getActualPrice(), true);
                            bids.add(tempbid);
                        } else if (negotiation.getResult() == 0) {        //don't diminish the price
                            // stop calculating the score and if it is good ,the GoodBid had one in the last process of calculate scores
                            System.out.println("房源" + msg.getSender().getName() + "拒绝降价");
                            Bid tempbid = mapped.get(msg.getSender());
                            this.setConsult(tempbid, 0, false);
                            finalBids.add(tempbid);
                        } else {
                            // stop calculating the score and if it is good ,the GoodBid had one in the last process of calculate scores
                            System.out.println("房源" + msg.getSender().getName() + "未响应降价");
                            Bid tempbid = mapped.get(msg.getSender());
                            this.setConsult(tempbid, 0, false);
                            finalBids.add(tempbid);
                        }   //对于后两者不再进行讨价还价
                    }
                    if (currentResponse == responseNum) {
                        //收到全部的回复后 处理协商结果
                        System.out.println("第"+status+"次协商结果处理");
                        if (lowerPriceNum == 0) {
                            //所有房源都不降价
                            System.out.println("在第"+status+"次协商中 所有房源都不降价了");
                            tenant t = agent.getOwner();
                            Order order = agent.getOrder(t.getId());
                            //返回现在的最好的房源
                            tenantData data = agent.getContext().handle(allbids,order);
                            java.util.List<BidInfo> resultBids = this.creatBidInfo(data.getGoods());
                            agent.putResult(resultBids);
                            negotiation neg = new negotiation(myAgent,allbids,order.getId(),true);
                            myAgent.addBehaviour(neg);
                        } else {
                            System.out.println("第"+status+"次协商结果处理");
                            tenant t = agent.getOwner();
                            Order order = agent.getOrder(t.getId());
                            tenantData te = agent.getContext().handle(bids,order);
                            negotiation neg = null;
                            if (te.getConsults().size() == 0) {
                                java.util.List<BidInfo> resultBids = this.creatBidInfo(te.getGoods());
                                agent.putResult(resultBids);
                                neg = new negotiation(myAgent,allbids,order.getId(),true);
                            } else {
                                //需要重新协商的订单
                                responseNum = te.getConsults().size();
                                neg = new negotiation(myAgent, te.getConsults(), order.getId(),false);
                                bids.clear();
                            }
                            myAgent.addBehaviour(neg);
                            lowerPriceNum = 0;
                            currentResponse = 0;
                        }
                    }

                } catch (Codec.CodecException e) {
                    e.printStackTrace();
                } catch (OntologyException e) {
                    e.printStackTrace();
                }
            } else {
                //用来测试的
                String content = msg.getContent();
                System.out.println("房客Agent：" + content);
            }
            /*
              可以扩展监听其它类型的Message
             */
        } else {
            block();
        }
    }

    private void setConsult(Bid bid, int actual, boolean pass) {
        int landlordid = bid.getRoom().getLandlordId();
        java.util.List<Consult> consults = ((tenantAgent) myAgent).getConsult(landlordid);
        Consult oneConsult = consults.get(consults.size() - 1);
        Consult result;
        if (pass) {   //pass the negotiation
            result = new Consult(oneConsult.getTenantName(), oneConsult.getLandlordName(), oneConsult.getMinReduction(), oneConsult.getMaxReduction(), 1, oneConsult.getBidLevel(), actual);
        } else {  //reject the reduction
            result = new Consult(oneConsult.getTenantName(), oneConsult.getLandlordName(), oneConsult.getMinReduction(), oneConsult.getMaxReduction(), 2, oneConsult.getBidLevel(), oneConsult.getActualPrice());
        }
        consults.add(result);
        ((tenantAgent) myAgent).setConsult(landlordid,consults);
    }

    public java.util.List<BidInfo> creatBidInfo(List GoodBid) {
        java.util.List<BidInfo> resultBidInfo = new java.util.ArrayList<BidInfo>();
        for (int i = 0; i < cal.getGoodBid().size(); i++) {
            Bid bid = (Bid) cal.getGoodBid().get(i);
            Room r = bid.getRoom();
            landlord l =  landlordDao.findlandlordByid(r.getLandlordId());
            java.util.List<String> facilitys = new java.util.ArrayList<String>();
            for(int j = 0 ; j <bid.getFacilities().size() ; j++){
                facilitys.add((String)bid.getFacilities().get(j));
            }
            BidInfo info = new BidInfo(l.getLandlordname(),l.getLandlordtype(),r.getType(),bid.getPrice()+"",r.getPrice()+"",bid.getNum(),facilitys,((tenantAgent) myAgent).getConsult(l.getLandlordid()));
            info.setScore((Double)cal.getGoodScore().get(i));
            info.setLocation(l.getDetailaddress());
            resultBidInfo.add(info);
        }
        return resultBidInfo;
    }

}

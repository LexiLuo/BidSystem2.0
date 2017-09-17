package multiAgent.behavior.listener;

import DO.landlord;
import DO.room;
import DO.tender;
import dao.daoImpl.roomDao;
import dao.daoImpl.tenderDao;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import multiAgent.agent.landlordAgent;
import multiAgent.agentHelper.FileUtil;
import multiAgent.behavior.logical.landlordDealTender;
import multiAgent.ontology.*;
import util.DateUtil;

import java.util.Date;
import java.util.Map;

/**
 * Created by H77 on 2017/5/6.
 *
 */
public class landlordListener extends CyclicBehaviour {

    private Codec codec = new SLCodec();
    private Ontology ontology = BidOntology.getInstance();
    private landlordAgent landAgent ;
    public landlordListener(Agent agent){
        super(agent);
        this.landAgent = (landlordAgent)agent;
    }

    public void action() {
        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchLanguage(codec.getName()),
                MessageTemplate.MatchOntology(ontology.getName()));
        ACLMessage msg = myAgent.receive(mt);

        if(msg != null){
            if(msg.getPerformative() == ACLMessage.PROPOSE){
                ContentElement ce = null;
                try {
                    ce = myAgent.getContentManager().extractContent(msg);
                    Action act = (Action) ce;
                    Tender tender = (Tender) act.getAction();
                    Order order = tender.getOrder();
                    System.out.println("landlord" + myAgent.getName() + "收到信息地址" + order.getAddress() + " 价格区间:" + order.getMinPrice()+"—"+order.getMaxPrice());
                    FileUtil.append("房东Agnet" + myAgent.getName() + "收到订单地址" + order.getAddress() + " 价格区间:" + order.getMinPrice()+"—"+order.getMaxPrice());
                    myAgent.addBehaviour(new landlordDealTender(myAgent,tender,msg.getSender()));
                } catch (Codec.CodecException e) {
                    e.printStackTrace();
                } catch (OntologyException e) {
                    e.printStackTrace();
                }

            }else if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
                //监听客户端协商消息
                try {
                    ContentElement ce = myAgent.getContentManager().extractContent(msg);
                    Action act = (Action) ce;
                    if(act.getAction() instanceof Negotiation){
                        Negotiation negotiation = (Negotiation)act.getAction();

                        Map<String,Order> map = ((landlordAgent)myAgent).getOrderToNegotiate();
                        //房客Agent结束协商过程的时候会通知房东Agent
                        if(negotiation.getResult() == 2){
                             //获取协商的最后情况
                             Order order = map.get(negotiation.getId());
                             map.remove(negotiation.getId());
                             //该房东Agent没有需要处理的关于order的协商
                             negotiation = landAgent.getContext().calculate(negotiation,order);
                            //对于竞价结果需要持久化
                            tender tender = new tender(order.getId()+"",
                                    landAgent.getOwner().getLandlordid(),
                                    order.getCustomer(),
                                    negotiation.getActualPrice(),
                                    order.getAddress(),
                                    order.getStartTime(),
                                    order.getEndTime(),
                                    order.getRoomType(),
                                    order.getRoomNum(),
                                    order.getCreateTime(),
                                    order.getFacilities().toString(),
                                    order.getHotelType());
                            tenderDao.saveTender(tender);
                             if(map.isEmpty()){
                                 landAgent.doDelete();
                             }
                        }else {
                            System.out.println(myAgent.getName()+" 收到房客的协商消息");
                            FileUtil.append("房东Agent"+myAgent.getName()+" 收到房客的协商消息");
                            Order order = map.get(negotiation.getId());
                            negotiation = landAgent.getContext().calculate(negotiation,order);

                            //回复给房客
                            Action sendAct = new Action();
                            sendAct.setActor(myAgent.getAID());
                            sendAct.setAction(negotiation);

                            ACLMessage message = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                            message.addReceiver(msg.getSender());
                            message.setLanguage(codec.getName());
                            message.setOntology(ontology.getName());

                            myAgent.getContentManager().fillContent(message, sendAct);
                            //发消息
                            myAgent.send(message);
                        }

                    }
                } catch (Codec.CodecException e) {
                    e.printStackTrace();
                } catch (OntologyException e) {
                    e.printStackTrace();
                }
            }
            /*
               可以扩展监听其它类型的消息
             */
        }else{
            block();
        }

    }
}

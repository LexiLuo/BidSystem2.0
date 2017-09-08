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
                    System.out.println("landlord" + myAgent.getName() + "收到信息地址" + order.getAddress() + " 价格:" + order.getMinPrice()+"—"+order.getMaxPrice());
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
                            Order order = map.get(negotiation.getId());
                            Date start = order.getStartTime();
                            Date end = order.getEndTime();
                            landlord lord = ((landlordAgent) myAgent).getOwner();
                            String economy = lord.getCharacteristic(); //房东经济情况
                            room room = roomDao.findRoomByLandlordAndType(lord.getLandlordid(), order.getRoomType());

                            int init_price = room.getPrice();  //该房间的标价
                            int current_price = negotiation.getActualPrice();  //此次协商前的房间价格
                            int max = negotiation.getMaxReduction();  //房客希望的最高降价幅度
                            int min = negotiation.getMinReduction();  //房客希望的最低降价幅度
                            // 空房率
                            double availability = room.getRestnum() * 1.00 / room.getTotalnum();
                            /* 这部分是测试的代码 */
//                            Negotiation a = new Negotiation();
//                            a.setMaxReduction(max);
//                            a.setMinReduction(min);
//                            a.setActualPrice(current_price);
//                            a.setResult(negotiation.getResult());
                            negotiation = landAgent.getContext().calculate(negotiation,order);
                            //房东需要判断2个价格幅度区间，一个是对应max降价幅度 一个是对应min降价幅度
//                            if (DateUtil.isHoliday(start) || DateUtil.isHoliday(end)) {
//                                //节假日不接受降价
//                                negotiation.setResult(0);
//                            } else {
//
//                                if (economy.equals("Tension")) {
//                                    //不接受降价
//                                    negotiation.setResult(0);
//                                } else if (economy.equals("Affordable")) {
//                                    //空房率的标准设为60%
//                                    double minDegree = 0.9 / (availability / 0.6);
//                                    minDegree = minDegree > 0.9 ? 0.9 : minDegree;
//                                    if ((current_price * 1.0 / init_price) > minDegree) {
//
//                                        //接收降价，降价额为最低降价幅度
//                                        negotiation.setResult(1);
//                                        int reduction = (int) ((1 + availability / 2) * min);
//                                        reduction = reduction < max ? reduction : max;
//                                        negotiation.setActualPrice((int) (current_price * (100 - reduction) / 100));
//                                    } else {
//                                        //不接受降价
//                                        negotiation.setResult(0);
//                                    }
//                                } else if (economy.equals("Amiable")) {
//                                    //空房率的标准设为55%
//                                    double minDegree = 0.85 / (availability / 0.55);
//                                    minDegree = minDegree > 0.85 ? 0.85 : minDegree;
//
//                                    double maxDegree = 0.92 / (availability / 0.55);
//                                    maxDegree = maxDegree > 0.92 ? 0.92 : maxDegree;
//                                    if ((current_price * 1.0 / init_price) > maxDegree) {
//                                        //接收降价，降价额为最高降价幅度
//                                        negotiation.setResult(1);
//                                        int reduction = (int) (max / (2 - availability));
//                                        reduction = reduction > min ? reduction : min;
//                                        negotiation.setActualPrice((int) (current_price * (100 - reduction) / 100));
//
//                                    } else if ((current_price * 1.0 / init_price) > minDegree) {
//                                        //接收降价，降价额为最低降价幅度
//                                        negotiation.setResult(1);
//                                        int reduction = (int) ((1 + availability / 2) * min);
//                                        reduction = reduction < max ? reduction : max;
//                                        negotiation.setActualPrice((int) (current_price * (100 - reduction) / 100));
//                                    } else {
//                                        //不接受降价
//                                        negotiation.setResult(0);
//                                    }
//                                } else if (economy.equals("Promotion")) {
//                                    //空房率的标准设为50%
//                                    double minDegree = 0.78 / (availability / 0.5);
//                                    minDegree = minDegree > 0.78 ? 0.78 : minDegree;
//
//                                    double maxDegree = 0.85 / (availability / 0.5);
//                                    maxDegree = maxDegree > 0.85 ? 0.85 : maxDegree;
//
//                                    if ((current_price * 1.0 / init_price) > minDegree) {
//                                        //接收降价，降价额为最高降价幅度
//                                        negotiation.setResult(1);
//                                        int reduction = (int) (max / (2 - availability));
//                                        reduction = reduction > min ? reduction : min;
//                                        negotiation.setActualPrice((int) (current_price * (100 - reduction) / 100));
//                                    } else if ((current_price * 1.0 / init_price) > 0.8) {
//                                        //接收降价，降价额为最低降价幅度
//                                        negotiation.setResult(1);
//                                        int reduction = (int) ((1 + availability / 2) * min);
//                                        reduction = reduction < max ? reduction : max;
//                                        negotiation.setActualPrice((int) (current_price * (100 - reduction) / 100));
//                                    } else {
//                                        //不接受降价
//                                        negotiation.setResult(0);
//                                    }
//                                }
//                            }

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
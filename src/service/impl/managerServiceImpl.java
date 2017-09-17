package service.impl;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import multiAgent.agentHelper.FileUtil;
import service.common.agentHandler;
import service.managerService;
import util.Constants;

import java.io.File;

/**
 * Created by H77 on 2017/5/15.
 */
public class managerServiceImpl implements managerService {

    private static managerService manager = new managerServiceImpl();
    private managerServiceImpl(){};
    public static managerService getInstance(){return manager;}
    public void initSystem() {
        //初始化存储文件位置
        File f = new File(Constants.root);
        if(!f.exists()){
            f.mkdir();
        }
        FileUtil.init("协商日志"+System.currentTimeMillis()+".txt");

        Runtime rt = Runtime.instance();
        rt.setCloseVM(true);
        Profile pMain = new ProfileImpl("127.0.0.1",1099,null);
        AgentContainer container = rt.createMainContainer(pMain);
        agentHandler.containers.put("main",container);
        System.out.println("系统初始化");
        FileUtil.append("智能反向竞价系统初始化");
        System.out.println("---------------------");
        FileUtil.append("---------------------");
        System.out.println("初始化ManagerAgent");
        FileUtil.append("初始化管理Agent组");

        try {
            AgentController consultAgent = container.createNewAgent("f1","multiAgent.agent.consultAgent",null);
            consultAgent.start();
            agentHandler.agents.put(consultAgent.getName(),consultAgent);
            AgentController selectAgent = container.createNewAgent("f3","multiAgent.agent.selectAgent",null);
            selectAgent.start();
            agentHandler.agents.put(selectAgent.getName(),selectAgent);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}

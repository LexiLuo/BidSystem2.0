package multiAgent.AIDecision;

import DO.tenant;
import jade.core.Agent;
import multiAgent.AIDecision.tenant.*;
import multiAgent.AIDecision.tenant.calScore.CalPoints;
import multiAgent.AIDecision.tenant.calScore.ComfortablePerson;
import multiAgent.AIDecision.tenant.calScore.EconomicalPerson;
import multiAgent.agent.tenantAgent;
import multiAgent.ontology.Order;
import util.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by H77 on 2017/9/6.
 * 房客用来处理bid的上下文
 */
public class tenant_context {
    private List<channel> channelList;
    private tenant t;
    private tenantAgent agent ;
    private double goodLevel;
    private int num;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;
    private File f = null;
    private tenantData data;
    public tenant_context(tenant t ,Agent g) {
        channelList = new ArrayList<channel>();
        this.t = t;
        this.agent = (tenantAgent)g;
        //初始化数据持久化的地方
        initReaderAndWriter();

        CalPoints c = null;
        if(t.getPreference().equals("economical")){
            c = new EconomicalPerson();
        }else if(t.getPreference().equals("comfortable")){
            c = new ComfortablePerson();
        }
        //特征量化
        scoreHandle s = new scoreHandle(c,g,t);
        //极端数据过滤
        bidFilter f = new bidFilter();
        //bid判别
        //1.随机森林预测
        forestPredict fp = new forestPredict();
        tenant_ScoreAI t_AI = new tenant_ScoreAI();
        fp.setForest(t_AI);
        //2.低数据量自己实现的预测算法
        scoreJudge sj = new scoreJudge();
        bidJudge b = new bidJudge(fp,sj);
        //价格预测
        pricePredict pp = new pricePredict();

        //初始化处理流程
        channelList.add(s);
        channelList.add(f);
        channelList.add(b);
        channelList.add(pp);
    }

    public tenantData handle(jade.util.leap.List bids , Order order){
        //在处理开始前初始化参数
        readGoodLevel();
        data = new tenantData();
        data.setGoodLevel(goodLevel);
        data.setNum(num);
        //处理流程
        for(channel c : channelList){
            data = c.handle(bids,order,data);
        }
        //持久化相关参数
        goodLevel = data.getGoodLevel();
        num = data.getNum();
        writeGoodLevel();
        return data;
    }

    private void initReaderAndWriter(){
        String root = Constants.root+t.getName()+".txt";
        boolean create = false;
        f = new File(root);
        try {
            if(!f.exists()) {
                f.createNewFile();
                create = true;
            }
            reader = new BufferedReader(new FileReader(f));
            writer = new BufferedWriter(new FileWriter(f,true));
            if(create){
                goodLevel = 11.0;
                num = 1;
                String str = goodLevel+"-"+num;
                writer.write(str);
                writer.flush();
                writer.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    private void readGoodLevel(){
        try {
            String str = reader.readLine();
            String[] res = str.split("-");
            goodLevel = Double.parseDouble(res[0]);
            num = Integer.parseInt(res[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void writeGoodLevel(){
        try {
            String str = goodLevel+"-"+num;
            writer = new BufferedWriter(new FileWriter(f,false));
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

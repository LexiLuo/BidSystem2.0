package multiAgent.AIDecision;

import DO.bid;
import dao.daoImpl.bidDao;
import smile.classification.RandomForest;
import smile.data.Attribute;
import smile.data.NumericAttribute;

import java.io.*;
import java.util.HashMap;
import java.util.List;

/**
 * Created by H77 on 2017/6/14.
 */
public class tenant_ScoreAI {
    List<bid> bids;
    private HashMap<String,Integer> roomPoint = new HashMap<String, Integer>();

    private String file = "D:\\jade\\model.obj";

    private RandomForest forest;

    public tenant_ScoreAI(List<bid> bids){
        this();
        this.bids = bids;
    }
    public tenant_ScoreAI(){
        initForest();
    }

    public static void main(String[] args) {

        List<bid> bids = bidDao.selectAllBids();
        tenant_ScoreAI tenant = new tenant_ScoreAI(bids);
        System.out.println("...");
        int num = 0;
        int errors = 0;
        for (int i = bids.size()-5; i < bids.size(); i++) {
            bid bid = bids.get(i);
            double[] a = tenant.parseOneBid(bid);
            if(tenant.predict(bid) != bid.getResult()){
                errors++;
            }
            num++;
        }
        System.out.println("RandomForest errors = " + errors);
        System.out.println("精确度："+ (1-errors/num)*100);
        tenant.persistModel();
        System.out.println("...");
    }
    public void initForest(){
        File targetFile = new File(file);
        //如果有序列化的Model直接反序列化
        //否者根据数据集训练
        if(targetFile.exists()){
            readModel();
        }else{
            trainBid();
        }
    }

    public void trainBid(){
        Attribute[] attrs  = initAttribute();
        double[][] x = new double[bids.size()][4];
        int[] y = new int[bids.size()];
        for (int i = 0; i < bids.size()-5; i++) {
            bid bid = bids.get(i);
            double[] a = parseOneBid(bid);
            x[i] = a;
            y[i] = bid.getResult();
         }
        forest = new RandomForest(attrs,x,y,3,2);
        double[] results = forest.importance();
        for(int i = 0 ; i < results.length ; i++){
            System.out.println(attrs[i].getName()+": "+ results[i]);
        }
    }
    //序列化对象
    public void persistModel(){
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(forest);
        }catch (Exception e){
           e.printStackTrace();
        }
    }
    //反序列化对象
    public void readModel(){
        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
            forest = (RandomForest) input.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public double[] parseOneBid(bid bid){
        double[] a = new double[3];
        a[0] = Double.parseDouble(bid.getFacility())/100;
        a[1] = Double.parseDouble(bid.getArroundsite())/100;
        a[2] = bid.getPrice()*1.0/100;
        return a;
    }


    public int predict(bid bid){
        double[] x = parseOneBid(bid);
        int result = forest.predict(x);
        return result;
    }
    public  Attribute[] initAttribute(){
        Attribute[] attrs = new Attribute[3];
        attrs[0] = new NumericAttribute("facility");
        attrs[1] = new NumericAttribute("arroundSite");
        attrs[2] = new NumericAttribute("price");
        return attrs;
    }
    //目前用这种解析方式表示bid中某种List<facility>的唯一性
//    public double parseFacility(String facility){
//        int num = 8; //默认是8个设施
//        String str = facility.split("-")[0];
//        while(str.length() < 8){
//            str = str+"2";
//        }
////        int result = str.hashCode();
//        double result = Double.parseDouble(str);
//        return result;
//    }
//    public double parseArroundSite(String arroundSite){
//        String[] str = arroundSite.split(":");
//        byte[] results = new byte[str.length];
//
//        for( int i = 0 ; i < str.length ; i++){
//            int num = Integer.parseInt(str[i]);
//            results[i] = (byte)num;
//        }
//        int result = bytesToInt(results,0);
//        return result;
//    }

//    public int bytesToInt(byte[] src, int offset) {
//        int value;
//        value = (int) ((src[offset] & 0xFF)
//                | ((src[offset+1] & 0xFF)<<8)
//                | ((src[offset+2] & 0xFF)<<16)
//                | ((src[offset+3] & 0xFF)<<24));
//        return value;
//    }
//    private void fill_hashmap(){
//        roomPoint.put("Standard",1);
//        roomPoint.put("Superior",3);
//        roomPoint.put("Special",2);
//        roomPoint.put("Business",4);
//        roomPoint.put("Deluxe",5);
//    }

}

package service;

import util.Constants;

import java.io.*;

/**
 * Created by H77 on 2017/9/6.
 */
public class test {
    private double goodLevel;
    private int num;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;
    private File f = null;


     void initReaderAndWriter(){
        String root = "userData/"+"a"+".txt";
        f = new File(root);
        File file = new File(Constants.root);
        try {
            if(!file.exists()){
                file.mkdir();
            }
            if(!f.exists()) {
                f.createNewFile();
            }
            reader = new BufferedReader(new FileReader(f));
            writer = new BufferedWriter(new FileWriter(f,false));
            if(!f.exists()){
                f.mkdir();
                goodLevel = 11.0;
                num = 1;
                String str = goodLevel+"-"+num;
                writer.write(str);
                writer.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        test t = new test();
        t.initReaderAndWriter();
    }
}

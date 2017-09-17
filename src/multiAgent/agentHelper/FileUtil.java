package multiAgent.agentHelper;

import util.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by H77 on 2017/9/11.
 */
public  class FileUtil {
    public static File file;
    public static BufferedWriter writer;

    public static void init(String name){
        try {
            file = new File(Constants.root+name);
            writer = new BufferedWriter(new FileWriter(file,true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void append(String line){
        try {
            writer.write(line+"\n\r");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(){
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;
    }
}

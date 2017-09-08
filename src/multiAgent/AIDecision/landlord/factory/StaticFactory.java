package multiAgent.AIDecision.landlord.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by H77 on 2017/8/9.
 */
public class StaticFactory {
    public static Object getInstance(String className){
        Object instance=null;
        try {
            Class cls=Class.forName(className);
            instance= cls.newInstance();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return instance;

    }
    public static Object getInstance(String className,Object ...agrs) {
        Class cls=null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            return null;
        }
        Constructor[] constructors = cls.getConstructors();
        Object instance=null;
        for(Constructor cons:constructors){
            Class <?>[] clses=cons.getParameterTypes();
            if(clses.length>0){
                boolean isThisConstructor=true;
                for(int i=0;i<clses.length;i++){
                    Class c=clses[i];
                    if(! c.isInstance(agrs[i]) ){
                        isThisConstructor=false;
                    }
                }
                if(isThisConstructor){
                    try {
                        instance=cons.newInstance(agrs);
                        break;
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }else{
                    continue;
                }

            }
        }
        return instance;
    }
}

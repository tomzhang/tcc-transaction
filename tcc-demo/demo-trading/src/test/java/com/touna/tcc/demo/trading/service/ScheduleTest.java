package com.touna.tcc.demo.trading.service;

import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import org.springframework.core.type.MethodMetadata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by chenchaojian on 17/5/27.
 */
public class ScheduleTest {
    static ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

    static ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 5, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(2048),new ThreadFactory(){
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"test thread ....");
        }
    }
    );

    public static void main(String args[]) throws InterruptedException {

        Runnable runnable = new Runnable() {
            public void run() {

                try {
                    // task to run goes here
                    System.out.println("Hello !!");
                    Thread.sleep(1000*100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        };
//
//        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
//        service.scheduleAtFixedRate(runnable, 1, 100, TimeUnit.SECONDS);
//        Hello h = new Hello();
//        Method[] methods = Hello.class.getMethods();
//        for(Method m:methods){
//            if(Modifier.isPublic(m.getModifiers())){
//                try {
//                    if(m.getName().equals("hello")) {
//                        Object[] argums = new Object[0];
//                        List list = new ArrayList(0);
//                        m.invoke(h, list.toArray());
//                    }
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        for(int i=0;i<100;i++) {
            executor.execute(runnable);
        }

//        Thread.sleep(1000*1000);




    }


    public static class Hello{
        public Hello() {
        }

        public void hello(){
            System.out.println("hello world");

        }
    }
}

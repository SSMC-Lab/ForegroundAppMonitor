package com.fruitbasket.foregroundappmonitor.store;

import android.content.*;
import android.util.Log;

import com.fruitbasket.foregroundappmonitor.MyApp;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Author: FruitBasket
 * Time: 2017/6/21
 * Email: FruitBasket@qq.com
 * Source code: github.com/DevelopersAssociation
 */

public class CommenStore implements StoreStrategy {
    private static final String TAG=".store.CommenStore";

    private ArrayBlockingQueue<Data> queue;
    private boolean isStop;
    private ExecutorService pool=Executors.newSingleThreadExecutor();

    private String dataPath;//指定一个文件夹，数据存放在这个文件中
    private boolean dateChange;//指示系统的日期是否发生变化

    public CommenStore(String dataPath){
        queue=new ArrayBlockingQueue<Data>(50);
        this.dataPath=dataPath;
        dateChange=false;
    }

    @Override
    public boolean storeData() {
        Log.i(TAG,"storeData()");
        //dataPath的判断
        {
            File dir = new File(dataPath);
            if (dir.isDirectory() == false) {
                Log.w(TAG, "dataPath: " + dir.getAbsolutePath() + " is not a directory");
                return false;
            }
            if (dir.exists() == false) {
                if (dir.mkdirs() == false) {
                    Log.w(TAG,"create directory '"+dir.getAbsolutePath()+"'failed");
                    return false;
                }
            }
        }

        MyApp.getContext().registerReceiver(
                new DateChangeReceiver(),
                new IntentFilter("android.intent.action.DATE_CHANGED")
        );

        //使用异步的方式存放数据
        new Thread(new Runnable(){

            @Override
            public void run() {
                Log.i(TAG,"storeData(): run()");
                try {
                    File dataFile;
                    DataOutputStream outputStream;

                    dataFile=new File(
                            dataPath + File.separator+
                                    (new SimpleDateFormat("yyyy-MM-dd")).format(System.currentTimeMillis())+
                                    ".xls"
                    );
                    outputStream=new DataOutputStream(
                            new FileOutputStream(dataFile,true)
                    );

                    ///此处应该插入文件头
                    if(dataFile.length()==0){
                    }

                    Data data;
                    isStop=false;
                    while(!isStop){
                        if(dateChange){//如果日期发声变化
                            outputStream.flush();
                            outputStream.close();

                            dataFile=new File(
                                    dataPath + File.separator+
                                            (new SimpleDateFormat("yyyy-MM-dd")).format(System.currentTimeMillis())+
                                            ".xls"
                            );
                            outputStream=new DataOutputStream(
                                    new FileOutputStream(dataFile,true)
                            );
                            dateChange=false;
                        }

                        data=queue.take();
                        Log.d(TAG,"storeData(): run(): queue.take() successfully");
                        outputStream.writeBytes(data.toString()+'\n');///这里会出现乱码问题
                    }
                    outputStream.flush();
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return true;
    }

    @Override
    public void put(Data data){
        Log.i(TAG,"put()");
        //使用异步的方式放入数据
        pool.submit(new Put(data));
    }

    @Override
    public void stop(){
        Log.i(TAG,"stop()");
        pool.shutdown();
        isStop=true;///当isStop=true后，消费者线程就会马上停止，而这时候queue可能还有未存储的数据
    }

    private class Put implements Runnable{
        private static final String TAG=".store.CommenStore.Put";

        private Data data;

        private Put(Data data){
            this.data=data;
        }

        @Override
        public void run() {
            Log.i(TAG,"run()");
            try {
                queue.put(data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class DateChangeReceiver extends BroadcastReceiver{

        private static final String TAG="..$DateChangeReceiver";

        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            Log.i(TAG,"onReceive()");
            dateChange=true;
        }
    }
}

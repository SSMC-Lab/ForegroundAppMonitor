package com.fruitbasket.foregroundappmonitor;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.fruitbasket.foregroundappmonitor.store.CommenStore;
import com.fruitbasket.foregroundappmonitor.store.Context;
import com.fruitbasket.foregroundappmonitor.store.SimpleData;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 本类是一个服务，用于监视手机的前台应用程序，并将监视结果保存起来
 *
 * Author: FruitBasket
 * Time: 2017/6/21
 * Email: FruitBasket@qq.com
 * Source code: github.com/DevelopersAssociation
 */

public class ForegroundAppMonitor extends AccessibilityService {
    private static final String TAG=".ForegroundAppMonitor";

    private static final String APP_FILE_DIR= Environment.getExternalStorageDirectory()+File.separator+"ForegroundAppMonitor";

    private Context context=new Context(
            new CommenStore(
                    APP_FILE_DIR
            )
    );

    @Override
    public void onCreate(){
        super.onCreate();
        Log.i(TAG,"onCreate()");

        //创建程序的根目录
        boolean state=false;
        File appDir=new File(APP_FILE_DIR);
        if(appDir.exists()==false){
            state=appDir.mkdirs();
        }
        if(state==false){
            Log.e(TAG,"false to create the mian directory of the app");
        }

    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        Log.i(TAG,"onStartCommand()");
        return super.onStartCommand(intent,flags,startId);
    }


    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy()");
        context.stop();
        super.onDestroy();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG,"onServiceConnected()");
        context.storeData();
    }

     @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(TAG,"onAccessibilityEvent()");
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            ComponentName componentName = new ComponentName(
                    event.getPackageName().toString(  ),
                    event.getClassName().toString()
            );
            ActivityInfo activityInfo=null;
            try {
                activityInfo= getPackageManager().getActivityInfo(componentName, 0);
            } catch (PackageManager.NameNotFoundException e) {
            }
            boolean isActivity = activityInfo != null;

            if (isActivity) {
                Log.d(TAG,"event.getEventTime(): "+event.getEventTime());
                Log.d(TAG,"event.getPackageName() : "+event.getPackageName());
                Log.d(TAG,"event.getText() : "+event.getText());

                context.put(new SimpleData(
                        new String[]{
                                (new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss", Locale.CHINA)).format(System.currentTimeMillis()),///应该使用event.getEventTime()
                                event.getPackageName().toString()
                }));
            }
            else{
                Log.d(TAG,"isActivity=false; ");
            }
        }
        else{
            Log.i(TAG,"out of types");
        }
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG,"onInterrupt()");
    }
}

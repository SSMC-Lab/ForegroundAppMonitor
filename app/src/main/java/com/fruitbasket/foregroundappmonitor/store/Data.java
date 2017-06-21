package com.fruitbasket.foregroundappmonitor.store;

import android.util.Log;

/**
 * Author: FruitBasket
 * Time: 2017/6/21
 * Email: FruitBasket@qq.com
 * Source code: github.com/DevelopersAssociation
 */

public abstract class Data {
    private static final String TAG=".store.Data";
    public String[] values;

    public Data(String[] values){
        this.values=values;
    }

    public static String getHeader(){
        return "Time\tAppName\tDescription";
    }

    @Override
    public String toString(){
        Log.i(TAG,"toString()");
        StringBuffer stringBuffer=new StringBuffer();
        for(String string:values){
            stringBuffer.append(string+'\t');
        }
        Log.d(TAG,"toString(): "+stringBuffer.toString());
        return stringBuffer.toString();
    }

}

package com.fruitbasket.foregroundappmonitor.store;

/**
 * Author: FruitBasket
 * Time: 2017/6/21
 * Email: FruitBasket@qq.com
 * Source code: github.com/DevelopersAssociation
 */

public class Context {
    private StoreStrategy storeStrategy;

    public Context(StoreStrategy storeStrategy){
        this.storeStrategy=storeStrategy;
    }

    public boolean storeData(){
        return storeStrategy.storeData();
    }

    public void put(Data data){
        storeStrategy.put(data);
    }

    public void stop(){
        storeStrategy.stop();
    }
}

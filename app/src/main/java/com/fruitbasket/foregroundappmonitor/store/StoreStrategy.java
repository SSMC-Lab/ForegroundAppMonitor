package com.fruitbasket.foregroundappmonitor.store;

/**
 * Author: FruitBasket
 * Time: 2017/6/21
 * Email: FruitBasket@qq.com
 * Source code: github.com/DevelopersAssociation
 */

public interface StoreStrategy {

    /**
     * 保存数据
     * @return true 成功保存数据
     */
    boolean storeData();

    /**
     * 放入要保存的数据
     * @param data
     */
    void put(Data data);

    //这个方法，看起来很费，后期完善它
    /**
     * 停止存放数据
     */
    void stop();
}

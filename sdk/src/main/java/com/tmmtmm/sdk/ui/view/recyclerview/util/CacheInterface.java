package com.tmmtmm.sdk.ui.view.recyclerview.util;

/**
 * @description
 * @time 2022/1/17 5:19 下午
 */
public interface CacheInterface<T> {

    /**
     * @param position
     * @param t
     */
    void put(int position, T t);

    /**
     * @param position
     * @return
     */
    T get(int position);

    /**
     * @param position
     */
    void remove(int position);

    /**
     */
    void clean();

}
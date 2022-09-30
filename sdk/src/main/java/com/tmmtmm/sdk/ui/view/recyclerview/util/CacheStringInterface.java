package com.tmmtmm.sdk.ui.view.recyclerview.util;

/**
 * @description
 * @time 2022/1/17 5:19 下午
 */
public interface CacheStringInterface<T> {

    /**
     * @param key
     * @param t
     */
    void put(String key, T t);

    /**
     * @param key
     * @return
     */
    T get(String key);

    /**
     * @param key
     */
    void remove(String key);

    /**
     *
     */
    void clean();

}
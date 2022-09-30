package com.tmmtmm.sdk.ui.view.recyclerview.util;

import android.util.LruCache;
import android.util.SparseArray;

import java.lang.ref.SoftReference;

/**
 * @description
 * @time 2022/1/17 5:18 下午
 */
public class CacheStringUtil<T> implements CacheStringInterface<T> {

    /**
     *
     */
    private boolean mUseCache = true;

    /**
     *
     */
    private LruCache<String, T> mLruCache;

    // TODO: gavin 2018/7/29  mLruCache移除后，使用软引用进行二级缓存

    /**
     *
     */
    private SparseArray<SoftReference<T>> mSoftCache;

    public CacheStringUtil() {
        initLruCache();
    }

    /**
     *
     */
    public void isCacheable(boolean b) {
        mUseCache = b;
    }

    private void initLruCache() {
        mLruCache = new LruCache<String, T>(2 * 1024 * 1024) {
            @Override
            protected void entryRemoved(boolean evicted, String key, T oldValue, T newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };
    }

    @Override
    public void put(String position, T t) {
        if (!mUseCache) {
            return;
        }
        mLruCache.put(position, t);
    }

    @Override
    public T get(String key) {
        if (!mUseCache) {
            return null;
        }
        return mLruCache.get(key);
    }

    @Override
    public void remove(String key) {
        if (!mUseCache) {
            return;
        }
        mLruCache.remove(key);
    }

    @Override
    public void clean() {
        mLruCache.evictAll();
    }
}

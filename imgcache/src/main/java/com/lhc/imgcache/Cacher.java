package com.lhc.imgcache;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 作者：lhc
 * 时间：2018/4/14.
 */

public class Cacher {

    private static volatile Cacher sInstance;
    private MemoryCache memoryCache;
    private DiskCache diskCache;
    private ReuseCache reuseCache;

    public static Cacher getInstance() {
        if (sInstance == null) {
            synchronized (Cacher.class) {
                if (sInstance == null) {
                    sInstance = new Cacher();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        memoryCache = new MemoryCache(context);
        diskCache = new DiskCache(context);
        reuseCache = ReuseCache.getInstance();
    }

    public Bitmap get(String key) {
        Bitmap bitmap = memoryCache.get(key);
        if (bitmap == null) {
            bitmap = diskCache.get(key);
        }
        return bitmap;
    }

    public Bitmap get(String key, int w, int h) {
        return get(key, w, h, 1);
    }

    public Bitmap get(String key, int w, int h, int inSampleSize) {
        Bitmap bitmap = memoryCache.get(key);
        if (bitmap == null) {
            Bitmap reuse = reuseCache.get(w, h, inSampleSize);
            bitmap = diskCache.get(key, reuse);
            if (bitmap != null) {
                memoryCache.put(key, bitmap);
            }
        }
        return bitmap;

    }

    public void put(String key, Bitmap value) {
        memoryCache.put(key, value);
        diskCache.put(key, value);
    }

    public void clear() {
        memoryCache.clear();
        diskCache.clear();
    }
}

package com.lhc.imgcache;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

/**
 * 作者：lhc
 * 时间：2018/4/14.
 */

class MemoryCache implements ICacheStrategy {

    private LruCache<String, Bitmap> lruCache;
    private ReuseCache reuseCache;


    public MemoryCache(Context context) {
        Context ctx = context.getApplicationContext();
        initLruCache(ctx);
        reuseCache = ReuseCache.getInstance();
    }

    private void initLruCache(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        int memory = am.getMemoryClass();//获取程序可用内存，单位为M
        lruCache = new LruCache<String, Bitmap>(memory / 8 * 1024 * 1024) {
            @Override
            protected int sizeOf(String key, Bitmap value) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    return value.getByteCount();
                } else {
                    return value.getAllocationByteCount();
                }

            }

            /**
             * 当Bitmap从LruCache移除时回调
             */
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (oldValue.isMutable()) {
                    reuseCache.put(oldValue);
                } else {
                    oldValue.recycle();
                }
            }
        };
    }


    @Override
    public Bitmap get(String key) {
        return lruCache.get(key);
    }

    @Override
    public void put(String key, Bitmap value) {
        lruCache.put(key, value);
    }

    @Override
    public void clear() {
        lruCache.evictAll();
    }
}

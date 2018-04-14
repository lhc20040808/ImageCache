package com.lhc.imgcache;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 作者：lhc
 * 时间：2018/4/14.
 */

public class ReuseCache {
    private Set<WeakReference<Bitmap>> reusablePoll;
    private ReferenceQueue<Bitmap> referenceQueue;
    private Thread clearThread;
    private boolean isClose;


    private static class SingleHolder {
        static final ReuseCache sInstance = new ReuseCache();
    }

    public static ReuseCache getInstance() {
        return SingleHolder.sInstance;
    }

    private ReuseCache() {
        reusablePoll = new HashSet<>();

    }

    private ReferenceQueue<Bitmap> getReferenceQueue() {
        if (referenceQueue == null) {
            referenceQueue = new ReferenceQueue<Bitmap>();
            clearThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!isClose) {
                            Reference<? extends Bitmap> bitmapReference = referenceQueue.remove();//该方法会阻塞线程
                            Bitmap bitmap = bitmapReference.get();
                            if (bitmap != null && !bitmap.isRecycled()) {
                                bitmap.recycle();//因为Android 3.0以下及8.0以上，bitmap空间申请在native层，gc无法帮助清除bitmap需要手动清除
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
            clearThread.start();
        }
        return referenceQueue;
    }

    public void put(Bitmap bitmap) {
        reusablePoll.add(new WeakReference<Bitmap>(bitmap, getReferenceQueue()));
    }

    @SuppressLint("ObsoleteSdkInt")
    public Bitmap get(int w, int h, int inSampleSize) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return null;
        }

        Bitmap reusableBitmap = null;
        Iterator<WeakReference<Bitmap>> iterator = reusablePoll.iterator();
        while (iterator.hasNext()) {
            Bitmap bitmap = iterator.next().get();
            if (bitmap != null) {
                if (checkBitmap(bitmap, w, h, inSampleSize)) {
                    reusableBitmap = bitmap;
                    iterator.remove();
                    break;
                }
            } else {
                iterator.remove();
            }
        }

        return reusableBitmap;
    }

    private boolean checkBitmap(Bitmap bitmap, int w, int h, int inSampleSize) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return bitmap.getWidth() == w && bitmap.getHeight() == h && inSampleSize == 1;
        }

        if (inSampleSize > 1) {
            w /= inSampleSize;
            h /= inSampleSize;
        }

        int byteCount = w * h * getPixelsCount(bitmap.getConfig());
        return byteCount <= bitmap.getAllocationByteCount();
    }

    private int getPixelsCount(Bitmap.Config config) {
        switch (config) {
            case ARGB_8888:
                return 4;
            case ARGB_4444:
            case RGB_565:
                return 2;
            default:
                return 0;

        }
    }

    public void shutDown() {
        isClose = true;
        clearThread.interrupt();
    }
}

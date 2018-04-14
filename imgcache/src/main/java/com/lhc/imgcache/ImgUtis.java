package com.lhc.imgcache;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 作者：lhc
 * 时间：2018/4/14.
 */

public class ImgUtis {

    public static Bitmap resizeBitmap(Resources resources, int id, int toW, int toH) {
        return resizeBitmap(resources, id, toW, toH, null);
    }

    public static Bitmap resizeBitmap(Resources resources, int id, int toW, int toH, Bitmap reusable) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, id, options);
        int fromW = options.outWidth;
        int fromH = options.outHeight;
        options.inSampleSize = calculateInSampleSize(fromW, fromH, toW, toH);
        options.inJustDecodeBounds = false;
        if (reusable != null) {
            options.inMutable = true;//是否复用内存
            options.inBitmap = reusable;//复用内存
        }
        return BitmapFactory.decodeResource(resources, id, options);
    }

    public static int calculateInSampleSize(int fromW, int fromH, int toW, int toH) {
        int inSampleSize = 1;
        while (fromW > toW && fromH > toH) {
            inSampleSize *= 2;
            fromW /= inSampleSize;
            fromH /= inSampleSize;
        }
        return inSampleSize;
    }
}

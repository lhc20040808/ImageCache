package com.lhc.imgcache;

import android.graphics.Bitmap;

/**
 * 作者：lhc
 * 时间：2018/4/14.
 */

public interface ICacheStrategy {


    Bitmap get(String key);

    void put(String key, Bitmap value);

    void clear();
}

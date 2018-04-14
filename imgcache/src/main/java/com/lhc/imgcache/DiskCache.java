package com.lhc.imgcache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.lhc.imgcache.disk.DiskLruCache;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 作者：lhc
 * 时间：2018/4/14.
 */

class DiskCache implements ICacheStrategy {

    private final static int MAX_SIZE = 10 * 1024 * 1024;
    private DiskLruCache diskLruCache;
    BitmapFactory.Options options = new BitmapFactory.Options();

    public DiskCache(Context context) {
        try {
            String path = Environment.getExternalStorageDirectory() + File.separator + context.getPackageName();
            diskLruCache = DiskLruCache.open(new File(path), BuildConfig.VERSION_CODE, 1, MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Bitmap get(String key) {
        return get(key, null);
    }

    public Bitmap get(String key, Bitmap reuse) {
        DiskLruCache.Snapshot snapshot = null;
        Bitmap bitmap = null;
        try {
            snapshot = diskLruCache.get(key);
            if (snapshot != null) {
                InputStream is = snapshot.getInputStream(0);
                options.inMutable = true;
                options.inBitmap = reuse;
                bitmap = BitmapFactory.decodeStream(is, null, options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(snapshot);
        }
        return bitmap;
    }

    @Override
    public void put(String key, Bitmap value) {
        DiskLruCache.Snapshot snapshot = null;
        OutputStream os = null;
        try {
            snapshot = diskLruCache.get(key);
            DiskLruCache.Editor edit = diskLruCache.edit(key);
            if (edit != null) {
                os = edit.newOutputStream(0);
                value.compress(Bitmap.CompressFormat.JPEG, 75, os);
                edit.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(snapshot);
            close(os);
        }
    }

    @Override
    public void clear() {
        try {
            diskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.lhc.imagecache;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lhc.imgcache.ImgUtis;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "bitmap";
    private ImageView img;
    private ImageView img_2;
    private ImageView img_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView) findViewById(R.id.img);
        printImgInfo(img);
        img_2 = (ImageView) findViewById(R.id.img_2);
        printImgInfo(img_2);
        img_3 = (ImageView) findViewById(R.id.img_3);
        img_3.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = ImgUtis.resizeBitmap(getResources(), R.mipmap.test, img_3.getWidth(), img_3.getHeight());
                Log.d(TAG, "宽:" + bitmap.getWidth() + " 高:" + bitmap.getHeight() + " 尺寸:" + bitmap.getByteCount());
                img_3.setImageBitmap(bitmap);
            }
        });

    }

    private void printImgInfo(ImageView img) {
        img.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        img.layout(0, 0, img.getMeasuredWidth(), img.getMeasuredHeight());
        img.buildDrawingCache();
        img.setDrawingCacheEnabled(true);
        Bitmap bitmap1 = Bitmap.createBitmap(img.getDrawingCache());
        img.setDrawingCacheEnabled(false);
        Log.d(TAG, "宽:" + bitmap1.getWidth() + " 高:" + bitmap1.getHeight() + " 尺寸:" + bitmap1.getByteCount());
    }
}

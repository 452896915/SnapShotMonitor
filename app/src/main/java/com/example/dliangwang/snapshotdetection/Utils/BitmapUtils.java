package com.example.dliangwang.snapshotdetection.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by dliang.wang on 2017/4/13.
 */

public class BitmapUtils {
    private static final int SAMPLE_SIZE = 2;
    public static Bitmap getCompressedBitmap(String filePath, int needHeight) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            // 第一次只解码原始长宽的值
            o.inJustDecodeBounds = true;
            try {
                BitmapFactory.decodeStream(new FileInputStream(new File(filePath)), null, o);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            // 根据原始图片长宽和需要的长宽计算采样比例，必须是2的倍数，
            //  IMAGE_WIDTH_DEFAULT=768， IMAGE_HEIGHT_DEFAULT=1024
            int needWidth = (int) (needHeight * 1.0 / o.outHeight * o.outWidth);
            o2.inSampleSize = SAMPLE_SIZE;
            // 每像素采用RGB_565的格式保存
            o2.inPreferredConfig = Bitmap.Config.RGB_565;
            // 根据压缩参数的设置进行第二次解码
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(new File(filePath)), null, o2);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, needWidth, needHeight, true);

//          b.recycle();  // b.recycle will cause prev Bitmap.createScaledBitmap null pointer exception on b occasionally
            System.gc();

            return scaledBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取采样比例
    public static int getImageScale(int outWidth, int outHeight, int needWidth, int needHeight) {
        int scale = 1;
        if (outHeight > needHeight || outWidth > needWidth) {
            int maxSize = needHeight > needWidth ? needHeight : needWidth;
            scale = (int) Math.pow(2, (int) Math.round(Math.log(maxSize /(double) Math.max(outHeight, outWidth)) / Math.log(0.5)));
        }

        return scale;
    }
}

package com.crews.newsreader.utils;

import android.graphics.Bitmap;

/**
 * Created by zia on 2017/4/9.
 */

public class ImgAdapter {
    /**
     * 传入需要的宽度，获取按原比例缩放的高度
     * @param width 所需宽度
     * @param bitmap 原始图片
     * @return
     */
    public static float getHight(float width,Bitmap bitmap){
        float h = bitmap.getHeight();
        float w = bitmap.getWidth();
        return h * width / w;
    }
}

package com.barswipe.draweePhotoView.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.request.BasePostprocessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * ========================================================== <br>
 * <b>版权</b>：　　　别志华 版权所有(c) 2015 <br>
 * <b>作者</b>：　　　别志华 biezhihua@163.com<br>
 * <b>创建日期</b>：　15-9-23 <br>
 * <b>描述</b>：　　　用于处理图片的类，在图片尚未传给process之前处理<br>
 * <b>版本</b>：　    V1.0 <br>
 * <b>修订历史</b>：　<br>
 * ========================================================== <br>
 */
public class MyBasePostProcessor extends BasePostprocessor {

    Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    MySimpleDraweeView draweeView;

    public MyBasePostProcessor(MySimpleDraweeView draweeView) {
        this.draweeView = draweeView;
    }

    @Override
    public String getName() {
        return "MyBasePostProcessor";
    }

//    @Override
//    public void process(Bitmap destBitmap, Bitmap sourceBitmap) {
//        for (int x = 0; x < destBitmap.getWidth(); x++) {
//            for (int y = 0; y < destBitmap.getHeight(); y++) {
//                destBitmap.setPixel(destBitmap.getWidth() - x, y, sourceBitmap.getPixel(x, y));
//            }
//        }
//    }
//    @Override
//    public void process(Bitmap bitmap) {
//        for (int x = 0; x < bitmap.getWidth(); x+=2) {
//            for (int y = 0; y < bitmap.getHeight(); y+=2) {
//                bitmap.setPixel(x, y, Color.RED);
//            }
//        }
//    }

    @Override
    public CloseableReference<Bitmap> process(Bitmap sourceBitmap, PlatformBitmapFactory bitmapFactory) {
        CloseableReference<Bitmap> bitmapRef = bitmapFactory.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight());
        try {
            Bitmap destBitmap = bitmapRef.get();
            int width = sourceBitmap.getWidth();         //获取位图的宽
            int height = sourceBitmap.getHeight();       //获取位图的高

            int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组
            sourceBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            int alpha = 0xFF << 24;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int grey = pixels[width * i + j];

                    int red = ((grey & 0x00FF0000) >> 16);
                    int green = ((grey & 0x0000FF00) >> 8);
                    int blue = (grey & 0x000000FF);

                    grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                    grey = alpha | (grey << 16) | (grey << 8) | grey;
                    pixels[width * i + j] = grey;
                }
            }
            destBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return CloseableReference.cloneOrNull(bitmapRef);
        } finally {
            CloseableReference.closeSafely(bitmapRef);
        }
//
//        // 默认宽高比例显示 W:H = 1:2
//        // 按照宽高比例截取图片区域
//        if (sourceBitmap.getHeight() > (int) (sourceBitmap.getWidth() * MySimpleDraweeView.DEF_RATIO)) {
//            Bitmap bitmap = decodeRegion(sourceBitmap, sourceBitmap.getWidth(), (int) (sourceBitmap.getWidth() * MySimpleDraweeView.DEF_RATIO));
//            return super.process(bitmap, bitmapFactory);
//        }
//
//        // 将PNG图片转换成JPG，并将背景色设置为指定颜色
//        else if (DefaultImageFormats.PNG.equals(draweeView.getImageFormat()) && draweeView.isReplacePNGBackground() != -1) {
//            replaceTransparent2TargetColor(sourceBitmap, draweeView.isReplacePNGBackground());
//        }
//
//        // PNG图片，并且设置了图片最大宽高，如果加载的PNG图片宽高超过指定宽高，并截取指定大小
//        else if (DefaultImageFormats.PNG.equals(draweeView.getImageFormat())
//                && draweeView.getTargetImageSize() != -1
//                && (sourceBitmap.getWidth() > draweeView.getTargetImageSize() || sourceBitmap.getHeight() > draweeView.getTargetImageSize())) {
//
//            // 压缩图片
//            Bitmap bitmap = Utils.decodeSampledBitmapFromByteArray(
//                    bitmap2Bytes(sourceBitmap, 100),
//                    draweeView.getTargetImageSize(),
//                    draweeView.getTargetImageSize());
//
//            // 截取图片
//            Bitmap region = decodeRegion(bitmap, draweeView.getTargetImageSize(), draweeView.getTargetImageSize());
//            bitmap.recycle();
//
//            return super.process(region, bitmapFactory);
//        }
//        return super.process(sourceBitmap, bitmapFactory);
    }

    /**
     * 将彩色图转换为黑白图
     *
     * @return 返回转换好的位图
     */
    public static Bitmap convertToBlackWhite(Bitmap img) {
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();       //获取位图的高

        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }

    private void replaceTransparent2TargetColor(Bitmap sourceBitmap, int color) {
        Canvas canvas = new Canvas(sourceBitmap);
        canvas.drawColor(color, PorterDuff.Mode.DST_OVER);
        canvas.drawBitmap(sourceBitmap, 0, 0, mPaint);
    }

    /**
     * 将bitmap转化为数组
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, int quality) {
        if (bitmap == null) {
            throw new IllegalArgumentException("bitmap is not null");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

    /**
     * 截取bitmap指定的宽高
     */
    public static Bitmap decodeRegion(Bitmap bitmap, int width, int height) {
        return decodeRegion(bitmap2Bytes(bitmap, 100), width, height);
    }

    public static Bitmap decodeRegion(byte[] bytes, int width, int height) {
        BitmapRegionDecoder bitmapRegionDecoder = null;
        try {
            bitmapRegionDecoder = BitmapRegionDecoder.newInstance(bytes, 0, bytes.length, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Rect rect = new Rect(0, 0, width, height);
        assert bitmapRegionDecoder != null;
        return bitmapRegionDecoder.decodeRegion(rect, null);
    }
}

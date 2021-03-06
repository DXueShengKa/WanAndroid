package com.hc.wanandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class QrCodeUtils {

    private static final int DEFAULT_SIZE = 800;
    private static final int MAX_QR_CODE_SIZE = 1500;

    public QrCodeUtils() {

    }

    /**
     * {@link QrCodeUtils#createQrCode(String, int)}.
     */
    public Bitmap createQrCode(String contents) throws WriterException {
        return createQrCode(contents, DEFAULT_SIZE);

    }

    /**
     * 根据字符生成二维码 {@link QrCodeUtils#createQrCode(String, int, int)}.
     *
     * @param size 图片大小
     */
    public Bitmap createQrCode(String contents, int size) throws WriterException {
        return createQrCode(contents, size, size);
    }

    /**
     * 根据字符生成二维码
     *
     * @param contents 文字内容
     * @param width    图片宽度
     * @param height   图片高度
     * @return 二维码图像
     * @throws WriterException
     */
    public Bitmap createQrCode(String contents, int width, int height) throws WriterException {

        final Map<EncodeHintType, Object> hint = new EnumMap<>(EncodeHintType.class);
        //指定纠错等级,纠错级别
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        //编码
        hint.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //设置二维码边的空度，非负数
        hint.put(EncodeHintType.MARGIN, 1);

        final BitMatrix bitMatrix = new MultiFormatWriter().encode(
                contents, BarcodeFormat.QR_CODE,
                width, height, hint
        );

        final int w = bitMatrix.getWidth();
        final int h = bitMatrix.getHeight();

        final Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int color = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                bitmap.setPixel(x, y, color);
            }
        }

        return bitmap;
    }

    public String readQrCodeRGB(InputStream inputStream) throws NotFoundException {

        final Bitmap bitmap = zipBitmap(BitmapFactory.decodeStream(inputStream));

        final int height = bitmap.getHeight();
        final int width = bitmap.getWidth();

//        Log.d("QR", "h "+height +" w"+width);

        final int[] pixels = new int[height * width];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        return readQrCode(new RGBLuminanceSource(width, height, pixels));
    }


    public String readQrCode(LuminanceSource source) throws NotFoundException {
        try {
            return readQrCode(new GlobalHistogramBinarizer(source));
        }catch (NotFoundException e){
            return readQrCode(new HybridBinarizer(source));
        }
    }

    private String readQrCode(Binarizer binarizer) throws NotFoundException {
        final MultiFormatReader qrCodeReader = new MultiFormatReader();
        final Result result = qrCodeReader.decode(new BinaryBitmap(binarizer));
        return result.getText();
    }

    public String readYuvCode(byte[] yuvData, int width, int height) throws Exception {
        return readQrCode(new PlanarYUVLuminanceSource(yuvData, width, height, 0, 0, width, height, false));
    }

    public Bitmap zipBitmap(Bitmap bitmap){
        final int height = bitmap.getHeight();
        final int width = bitmap.getWidth();

//        Log.d("QR", "h "+height +" w"+width);

        if (Math.max(height,width) > MAX_QR_CODE_SIZE){
            Matrix matrix = new Matrix();
            matrix.setScale(0.5f,0.5f);
            return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
        return bitmap;
    }


    /**
     * 图像二值化（黑白）
     */
    private Bitmap zeroAndOne(Bitmap bm) {
        int width = bm.getWidth();//原图像宽度
        int height = bm.getHeight();//原图像高度
        int color;//用来存储某个像素点的颜色值
        int r, g, b, a;//红，绿，蓝，透明度
        //创建空白图像，宽度等于原图宽度，高度等于原图高度，用ARGB_8888渲染，这个不用了解，这样写就行了
        Bitmap bmp = Bitmap.createBitmap(width, height
                , Bitmap.Config.ARGB_8888);

        int[] oldPx = new int[width * height];//用来存储原图每个像素点的颜色信息
        int[] newPx = new int[width * height];//用来处理处理之后的每个像素点的颜色信息
        /*
         * 第一个参数oldPix[]:用来接收（存储）bm这个图像中像素点颜色信息的数组
         * 第二个参数offset:oldPix[]数组中第一个接收颜色信息的下标值
         * 第三个参数width:在行之间跳过像素的条目数，必须大于等于图像每行的像素数
         * 第四个参数x:从图像bm中读取的第一个像素的横坐标
         * 第五个参数y:从图像bm中读取的第一个像素的纵坐标
         * 第六个参数width:每行需要读取的像素个数
         * 第七个参数height:需要读取的行总数
         */
        bm.getPixels(oldPx, 0, width, 0, 0, width, height);//获取原图中的像素信息

        for (int i = 0; i < width * height; i++) {//循环处理图像中每个像素点的颜色值
            color = oldPx[i];//取得某个点的像素值
            r = Color.red(color);//取得此像素点的r(红色)分量
            g = Color.green(color);//取得此像素点的g(绿色)分量
            b = Color.blue(color);//取得此像素点的b(蓝色分量)
            a = Color.alpha(color);//取得此像素点的a通道值

            //此公式将r,g,b运算获得灰度值，经验公式不需要理解
            int gray = (int)((float)r*0.3+(float)g*0.59+(float)b*0.11);
            //下面前两个if用来做溢出处理，防止灰度公式得到到灰度超出范围（0-255）
            if(gray > 255) {
                gray = 255;
            }

            if(gray < 0) {
                gray = 0;
            }

            if (gray != 0) {//如果某像素的灰度值不是0(黑色)就将其置为255（白色）
                gray = 255;
            }

            newPx[i] = Color.argb(a,gray,gray,gray);//将处理后的透明度（没变），r,g,b分量重新合成颜色值并将其存储在数组中
        }
        /*
         * 第一个参数newPix[]:需要赋给新图像的颜色数组//The colors to write the bitmap
         * 第二个参数offset:newPix[]数组中第一个需要设置给图像颜色的下标值//The index of the first color to read from pixels[]
         * 第三个参数width:在行之间跳过像素的条目数//The number of colors in pixels[] to skip between rows.
         * Normally this value will be the same as the width of the bitmap,but it can be larger(or negative).
         * 第四个参数x:从图像bm中读取的第一个像素的横坐标//The x coordinate of the first pixels to write to in the bitmap.
         * 第五个参数y:从图像bm中读取的第一个像素的纵坐标//The y coordinate of the first pixels to write to in the bitmap.
         * 第六个参数width:每行需要读取的像素个数The number of colors to copy from pixels[] per row.
         * 第七个参数height:需要读取的行总数//The number of rows to write to the bitmap.
         */
        bmp.setPixels(newPx, 0, width, 0, 0, width, height);//将处理后的像素信息赋给新图
        return bmp;//返回处理后的图像
    }

    /**
     * yuv转bitmap
     */
    public Bitmap saveYUV2Bitmap(byte[] yuv, int mWidth, int mHeight) {
        final YuvImage image = new YuvImage(yuv, ImageFormat.NV21, mWidth, mHeight, null);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compressToJpeg(new Rect(0, 0, mWidth, mHeight), 100, stream);
        final Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
        try {
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bmp;
    }

}

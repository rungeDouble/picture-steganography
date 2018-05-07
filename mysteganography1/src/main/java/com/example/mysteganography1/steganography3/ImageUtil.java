package com.example.mysteganography1.steganography3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import org.ujmp.core.Matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;
import static java.lang.Math.sqrt;


public class ImageUtil {

    final static double pi = 3.1415926;

    public static int[] getImagePixels(String filepath) {
        Bitmap image = BitmapFactory.decodeFile(filepath);
        // 得到图像的宽度
        int width = image.getWidth();
        // 得到图像的高度
        int height = image.getHeight();
        // RGB格式图像文件每一个点的颜色由红、绿、蓝三种颜色构成，即实际图像可为3层，
        // 分别为R，G，B层，因此分解后的文件象素是实际坐标高度和宽度的三倍。
        int[] pixels = new int[width * height];
        // 读取坐标的范围是从(0,0)坐标开始宽width,高height，默认是Config.ARGB8888,4字节存储一位像素
        image.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    public static Matrix getNDCTMatrix(int N) {
        double[][] NDCT = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double a;
                if (i == 0) {
                    a = sqrt(1 / (double) N);
                } else {
                    a = sqrt(2 / (double) N);
                }
                NDCT[i][j] = a * (Math.cos((pi * (j + 0.5) * i / (double) N)));
            }

        }
        //System.out.println("完成：\n"+NDCT);
        Matrix NDCTMatrix = Matrix.Factory.importFromArray(NDCT);
        return NDCTMatrix;
    }

    public static double[][] makeImage2DArray(Matrix resoure) {
        double[][] ImageArray = resoure.toDoubleArray();
        return ImageArray;
    }

    public static Matrix makeImageMatrix(double[][] resoure) {
        return Matrix.Factory.importFromArray(resoure);
    }

    public Bitmap doPixelsToBitmap(int[] result, int width, int height) {
        Bitmap newPictureBitmap = Bitmap.createBitmap(result, 0, width, width, height, Bitmap.Config.ARGB_8888);
        return newPictureBitmap;
    }

    public static Boolean saveBitmap(Bitmap bitmap, String filepath, String fileName, String type) {
        Log.e(TAG, "保存图片");
        File file = new File("/sdcard/picture/" + "image");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
            }
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return true;
    }
    public static int[][] arrayTo2DArray(int[] m, int width, int height) {
        int[][] result = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int p = i * width + j;
                result[i][j] = m[p];
            }
        }
        return result;
    }

    public static int[] Array2DToArray(int[][] m) {
        int p = m.length * m[0].length;
        int[] result = new int[p];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                int q = j + i * m[0].length;
                result[q] = m[i][j];
            }
        }
        return result;
    }
    public static double[][] arrayTo2DArray(double[] m, int width, int height) {
        double[][] result = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int p = i * width + j;
                result[i][j] = m[p];
            }
        }
        return result;
    }

    public static double[] Array2DToArray(double[][] m) {
        int p = m.length * m[0].length;
        double[] result = new double[p];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                int q = j + i * m[0].length;
                result[q] = m[i][j];
            }
        }
        return result;
    }

    /**
     * @param pixels
     * @param width
     * @param height
     * @return
     */
    public static int[][][] getRGBArrayTo2DArray(int[] pixels, int width, int height) {
        // TODO: 17-4-19  修改成获取Bitmp的RGB 分量
        // 已知有3个二维数组组成分别代表RGB
        int[][][] result = new int[3][height][width];
        int[][] temp2DArray = arrayTo2DArray(pixels, width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int p = i * width + j;
                result[0][i][j] = Color.red(temp2DArray[i][j]);
                result[1][i][j] = Color.green(temp2DArray[i][j]);
                result[2][i][j] = Color.blue(temp2DArray[i][j]);
            }
        }
        return result;
    }


    public static int[] getRGB2DArrayToArray(int[][][] RGB2DArray) {
        int height = RGB2DArray[0].length;
        int width = RGB2DArray[0][0].length;
        int temp[][] = new int[height][width];

        for (int j = 0; j < height; j++){
            for (int k = 0; k < width; k++){
                temp[j][k] = Color.rgb(RGB2DArray[0][j][k],RGB2DArray[1][j][k], RGB2DArray[2][j][k]);
            }
        }
        int[] result= Array2DToArray(temp);
        return result;
    }
    public static double getPSNR(int nrows, int ncols, int[][] img1, int[][] img2) {
        double signal,noise,peak,mse;
        signal = noise = peak = 0;
        for (int i=0; i<nrows; i++) {
            for (int j=0; j<ncols; j++) {
                signal += img1[i][j] * img1[i][j];
                noise += (img1[i][j] - img2[i][j]) * (img1[i][j] - img2[i][j]);
                if (peak < img1[i][j])
                    peak = img1[i][j];
            }
        }

        mse = noise/(nrows*ncols); // Mean square error
        System.out.println("MSE: " + mse);
        System.out.println("SNR: " + 10*log10(signal/noise));
        System.out.println("PSNR(max=255): " + (10*log10(255*255/mse)));
        System.out.println("PSNR(max=" + peak + "): " + 10*log10((peak*peak)/mse));
        return 10*log10(255*255/mse);
    }

    public static double log10(double x) {
        return Math.log(x)/Math.log(10);
    }

    public static double[] intToDouble(int[] ints) {
        double [] result = new double[ints.length];
        for (int i =0; i < ints.length; i++){
            result[i] = ints[i];
        }
        return result;
    }
}

class ColorArray {
    private int[] baseArray, alphaArray, redArray, greenArray, blueArray;
    private double[] YArray, IArray, QArray;

    ColorArray(int[] inputArray) {
        baseArray = inputArray;
        alphaArray = getAArray(baseArray);
        redArray = getRArray(baseArray);
        greenArray = getGArray(baseArray);
        blueArray = getBArray(baseArray);
        YArray = new double[baseArray.length];
        IArray = new double[baseArray.length];
        QArray = new double[baseArray.length];
        creatYArray();
        creatIArray();
        creatQArray();
    }

    private void creatYArray() {
        for (int i = 0; i < baseArray.length; i++) {
            YArray[i] = 0.229 * redArray[i] + 0.587 * greenArray[i] + 0.114 * blueArray[i];
        }
    }

    private void creatIArray() {
        for (int i = 0; i < baseArray.length; i++) {
            IArray[i] = 0.596 * redArray[i] - 0.274 * greenArray[i] - 0.322 * blueArray[i];
        }
    }

    private void creatQArray() {
        for (int i = 0; i < baseArray.length; i++) {
            QArray[i] = 0.211 * redArray[i] - 0.523 * greenArray[i] + 0.312 * blueArray[i];
        }

    }

    public boolean isAlphaPic() {
        for (int i = 0; i < baseArray.length; i++) {
            if (alphaArray[i] == 0) {
                return true;
            }
        }
        return false;
    }

    public int[] getbaseArray() {
        return baseArray;
    }

    public int[] getalphaArray() {
        return alphaArray;
    }

    public int[] getredArray() {
        return redArray;
    }

    public int[] getgreenArray() {
        return greenArray;
    }

    public int[] getblueArray() {
        return blueArray;
    }

    public double[] getYArray() {
        return YArray;
    }
    public double[] getIArray() {
        return IArray;
    }
    public double[] getQArray() {
        return QArray;
    }

    public void setalphaArray(int[] inputArray) {
        // TODO: 17-5-18  超过255的处理、异常长度处理
        alphaArray = inputArray;
    }

    public void setredArray(int[] inputArray) {
        // TODO: 17-5-18  超过255的处理、异常长度处理
        for (int i = 0; i < inputArray.length; i++){
            if(inputArray[i] > 255){
                redArray[i] = 255;
            }
            else {
                redArray[i] = inputArray[i];
            }
        }
    }

    public void setgreenArray(int[] inputArray) {
        // TODO: 17-5-18  超过255的处理、异常长度处理
        for (int i = 0; i < inputArray.length; i++){
            if(inputArray[i] > 255){
                greenArray[i] = 255;
            }
            else {
                greenArray[i] = inputArray[i];
            }
        }
    }

    public void setblueArray(int[] inputArray) {
        // TODO: 17-5-18  超过255的处理、异常长度处理
        for (int i = 0; i < inputArray.length; i++){
            if(inputArray[i] > 255){
                blueArray[i] = 255;
            }
            else {
                blueArray[i] = inputArray[i];
            }
        }
    }

    public void setblueArray(double[] inputArray) {
        // TODO: 17-5-18  超过255的处理、异常长度处理
        for (int i = 0; i < inputArray.length; i++){
            if(inputArray[i] > 255){
                blueArray[i] = 255;
            }
            else {
                blueArray[i] = (int) inputArray[i];
            }
        }
    }

    public void setYArray(double[] inputArray) {
        // TODO: 17-5-18  超过255的处理、异常长度处理
        YArray = inputArray;
    }

    public void setIArray(double[] inputArray) {
        // TODO: 17-5-18  超过255的处理、异常长度处理
        IArray = inputArray;
    }

    public void setQArray(double[] inputArray) {
        // TODO: 17-5-18  超过255的处理、异常长度处理
        QArray = inputArray;
    }

    public void updatebaseArray() {
        // TODO: 17-5-18  超过255的处理、异常长度处理
        for (int i = 0; i < baseArray.length; i++) {
            baseArray[i] = Color.argb(alphaArray[i], redArray[i], greenArray[i], blueArray[i]);
        }
    }

    public void updatebaseArraybyYIQ() {
        // TODO: 17-5-18  超过255的处理、异常长度处理
        for (int i = 0; i < baseArray.length; i++) {
            int red = (int) (1.0 * YArray[i] + 0.956 * IArray[i] + 0.621 * QArray[i]);
            int green = (int) (1.0 * YArray[i] - 0.272 * IArray[i] - 0.647 * QArray[i]);
            int blue = (int) (1.0 * YArray[i] - 1.106 * IArray[i] - 1.703 * QArray[i]);
            redArray[i] = intToColor(red);
            greenArray[i] = intToColor(green);
            blueArray[i] = intToColor(blue);
            baseArray[i] = Color.argb(255, redArray[i], greenArray[i], blueArray[i]);
        }
    }

    private int intToColor(int intColor) {
        if (intColor > 255)
            return 255;
        else if (intColor < 0)
            return 0;
        else
            return intColor;

    }

    public static int[] getAArray(int[] pixels) {
        // TODO: 17-4-19  修改成获取Bitmp的A 分量
        int[] result = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            result[i] = Color.alpha(pixels[i]);

        }
        return result;
    }

    public static int[] getRArray(int[] pixels) {
        // TODO: 17-4-19  修改成获取Bitmp的R 分量
        int[] result = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            result[i] = Color.red(pixels[i]);

        }
        return result;
    }

    public static int[] getGArray(int[] pixels) {
        // TODO: 17-4-19  修改成获取Bitmp的G 分量
        int[] result = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            result[i] = Color.green(pixels[i]);

        }
        return result;
    }

    public static int[] getBArray(int[] pixels) {
        // TODO: 17-4-19  修改成获取Bitmp的B 分量
        int[] result = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            result[i] = Color.blue(pixels[i]);

        }
        return result;
    }
}


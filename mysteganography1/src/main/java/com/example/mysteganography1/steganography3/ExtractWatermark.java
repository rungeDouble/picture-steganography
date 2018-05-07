package com.example.mysteganography1.steganography3;

import android.graphics.Bitmap;
import android.util.Log;

import org.ujmp.core.Matrix;


public class ExtractWatermark {
    private Bitmap mWaterBitmap;
    int mHeight,mWidth;
    Matrix mImageMatrix,mDCTMatrix,mFDCTMatrix;
    double[][] DecodeValue;

    public ExtractWatermark(Bitmap inputBItmap){
        mWaterBitmap = inputBItmap;
        mHeight = inputBItmap.getHeight();
        mWidth = inputBItmap.getWidth();
        /*mHeight = 64;
        mWidth = 64;*/
        mImageMatrix = Matrix.Factory.zeros(mHeight, mWidth);
        mDCTMatrix = ImageUtil.getNDCTMatrix(8);
        mFDCTMatrix = mDCTMatrix.transpose();
        DecodeValue = new double[mHeight][mWidth];
    }


    public String ExtractDctwaterMark(int sortBit){
        /*if (mWidth != mHeight) {
            return null;
        }*/
        System.out.println("长度是"+ mWidth);
        //获取嵌入的信息bit
        int[] messageBit = new int[(mWidth / 8)*(mHeight / 8) * 8];
        byte[] messageByte = new byte[(mWidth / 8)*(mHeight / 8)];

        //获取相关的像素序列
        int[] pix = new int[mHeight * mWidth];
        mWaterBitmap.getPixels(pix, 0, mWidth, 0, 0, mWidth, mHeight);

        //拆分像素RGB和YIQ分量信息
        ColorArray picArray = new ColorArray(pix);

        //选择插入到Y分量，以特定种子进行随机置乱
        //double[] pixs = MathTool.random_sort(picArray.getYArray(), sortBit);
        //DecodeValue = arrayTo2DArray(pixs, mWidth, mHeight);
        DecodeValue = ImageUtil.arrayTo2DArray(picArray.getYArray(), mWidth, mHeight);

        //开始提取
        int i, j;
        System.out.print("sort:");
        decodeloop: for (i = 0, j = 0; i < mHeight / 8; i++) {
            for (j = 0; j < mWidth / 8; j++) {
                if (i*(mWidth/8)+j >= (mWidth / 8)*(mHeight / 8) * 8) {
                    break decodeloop;
                }
                System.out.print(" ("+i+", "+j+")");
                messageBit[i*(mWidth/8)+j] = decodeBitInto8Blk(i*8, j*8);
            }
        }
        System.out.println("i= "+i+"j= "+j);

        System.out.print("decodebit:");
        for (int k = 0; k < messageBit.length; k++){
            System.out.print(" "+ messageBit[k]);
        }
        System.out.println();

        for (i = 0; i < (mWidth / 8)*(mHeight / 8) * 8; i++) {
            int bitNo = i % 8;
            messageByte[(i) / 8] = (messageBit[i] == 1 ? (byte) (messageByte[i / 8] | (1 << bitNo))
                    : (byte) (messageByte[i / 8] & ~(1 << bitNo)));
        }

        try{
            String out= new String(messageByte,"UTF-8");

            int start= out.indexOf("!n17");
            if(start==-1) return null;
            int stop= out.indexOf("$n%");
            if(stop==-1) return null;
            System.out.println("DECODE RESULT:"+out.substring(start+4, stop));
            return out.substring(start+4, stop);
        } catch (Exception e){
            return null;
        }
    }

    private int decodeBitInto8Blk(int x, int y) {
        //分块
        double[][] pix = new double[8][8];
        pix = get8Blk(x, y);
        //DCT变换
        mImageMatrix = ImageUtil.makeImageMatrix(pix);
        mImageMatrix = mDCTMatrix.mtimes(mImageMatrix).mtimes(mFDCTMatrix);
        pix = mImageMatrix.toDoubleArray();
        double d = pix[4][1] - pix[3][2];
        if (d < 0){
            return 0;
        }
        else{
            return 1;
        }

    }

    private double[][] get8Blk (int x, int y) {
        if(x > mHeight || y > mWidth){
            Log.e("get8Blk error!:", "error! overflow!");
        }
        double[][] new8Blk = new double[8][8];
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                new8Blk[i][j] = DecodeValue[x+i][y+j];
            }
        }
        return new8Blk;
    }
}

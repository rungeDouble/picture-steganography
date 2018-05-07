package com.example.mysteganography1.steganography3;

import android.graphics.Bitmap;
import android.util.Log;

import org.ujmp.core.Matrix;

import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.lang.Math.signum;


public class AddWatermark {
    private Bitmap mPicBitmap;
    private String addEncoderMessage;
    int mHeight,mWidth;
    Matrix mImageMatrix,mDCTMatrix,mFDCTMatrix;
    double [][] EncodeValue;

    public AddWatermark(Bitmap inputBitmap, String inputStr){
        mHeight = inputBitmap.getHeight();
        mWidth = inputBitmap.getWidth();
       /* mHeight = 64;
        mWidth = 64;*/
        mPicBitmap = inputBitmap.copy(Bitmap.Config.ARGB_8888, true);
        addEncoderMessage = "!n17"+inputStr+"$n%";
        mImageMatrix = Matrix.Factory.zeros(mHeight, mWidth);
        mDCTMatrix = ImageUtil.getNDCTMatrix(8);
        mFDCTMatrix = mDCTMatrix.transpose();
        EncodeValue = new double[mHeight][mWidth];
    }


    public Bitmap addDctwaterMark(int sortBit, double size) throws Exception {
        /*if (mWidth != mHeight){
            return null;
        }*/
        System.out.println("长度是"+ mWidth);
        //获取嵌入的信息Byte
        byte [] messageByte = addEncoderMessage.getBytes("UTF-8");

        //获取相关的像素序列
        int[] pix = new int[mHeight*mWidth];
        mPicBitmap.getPixels(pix, 0, mWidth, 0, 0, mWidth, mHeight);

        //拆分像素RGB和YIQ分量信息
        ColorArray picArray = new ColorArray(pix);

        //选择插入到Y分量，以特定种子进行随机置乱
        //double [] pixs = MathTool.random_sort(picArray.getYArray(), sortBit);
        //EncodeValue = arrayTo2DArray(pixs, mWidth , mHeight);
        EncodeValue = ImageUtil.arrayTo2DArray(picArray.getYArray(), mWidth , mHeight);

        //开始嵌入
        int[] flag = new int[messageByte.length*8];
        for (int i = 0; i < messageByte.length; i++) {
            for (int j = 0; j < 8; j++) {
                flag[i*8+j] = (messageByte[i] >> j) & 1;
            }
        }

        System.out.print("encodebit:");
        for (int k = 0; k < flag.length; k++){
            System.out.print(" "+flag[k]);
        }
        System.out.println();

        System.out.print("sort:");
        int i,j;
        encodeloop: for (i = 0,j = 0; i < mHeight/8; i++) {
            /*if(i*(mWidth/8)+j >= messageByte.length*8){
                break;
            }*/
            for (j = 0; j < mWidth/8; j++) {
                if(i*(mWidth/8)+j >= messageByte.length*8){
                    break encodeloop;
                }
                System.out.print(" ("+i+", "+j+")");
                encodeBitInto8Blk(i*8, j*8, flag[i*(mWidth/8)+j], size);
            }
        }
        System.out.println("i= "+i+"j= "+j);


        //逆置乱
        //double [] pixw = MathTool.random_Fsort(ImageUtil.Array2DToArray(EncodeValue),sortBit);
        //picArray.setYArray(pixw);
        picArray.setYArray(ImageUtil.Array2DToArray(EncodeValue));
        picArray.updatebaseArraybyYIQ();

        if (picArray.isAlphaPic()){
            //生成Bitmap
            //用此方法嵌入进去的数据将会修改所有像素的值的alpha分量全变成255
            int[][] ter = ImageUtil.arrayTo2DArray(picArray.getbaseArray(), mWidth , mHeight);
            //因为arrayTo2DArray函数与Bitmap的行列优先级刚好相反，反向取数
            for (int ii = 0; ii < mHeight;ii++) {
                for (int jj = 0; jj < mWidth; jj++){
                    mPicBitmap.setPixel(ii, jj, ter[jj][ii]);
                }
            }
            return mPicBitmap;
        } else {

            //用此种方法在A分量为0的像素嵌入的数据会丢失;
            Bitmap newBitmap = Bitmap.createBitmap(picArray.getbaseArray(), 0, mWidth, mWidth, mHeight, Bitmap.Config.ARGB_8888);
            return newBitmap;
        }
    }

    private void encodeBitInto8Blk(int x, int y, final int hidemessagebit, double size) {
        //System.out.println("add lenght"+messageByte.length);
        if (hidemessagebit != 1 && hidemessagebit != 0) {
            Log.e("encodeBitInto8Blk:", "hidemessagebit is not a bit!");
            return;
        }
        //分块
        double[][] pix = new double[8][8];
        pix = get8Blk(x, y);
        //DCT变换
        mImageMatrix = ImageUtil.makeImageMatrix(pix);
        mImageMatrix = mDCTMatrix.mtimes(mImageMatrix).mtimes(mFDCTMatrix);
        pix = mImageMatrix.toDoubleArray();
        double d = pix[4][1] - pix[3][2];

        //将signum(-1,(0,1))扩展到（0，1）
        double flag = round((signum(d) + 1) / 2);
        //将(0,1)扩展到（-1，1）
        double sign = (flag - 0.5) * 2;
        if (hidemessagebit == flag) {
            if (abs(d) < size) {
                pix[4][1] = pix[4][1] + sign * size * 0.5;
                pix[3][2] = pix[3][2] - sign * size * 0.5;
            }
        } else {
            if (size < abs(d) && abs(d) < 2 * size) {
                pix[4][1] = pix[4][1] - d;
                pix[3][2] = pix[3][2] + d;

            } else if (d > 2 * size || (-size < d && d < 0)) {
                pix[4][1] = pix[4][1] - (d - size * 0.5);
                pix[3][2] = pix[3][2] + (d - size * 0.5);
            } else {
                pix[4][1] = pix[4][1] - (d + size * 0.5);
                pix[3][2] = pix[3][2] + (d + size * 0.5);
            }
        }
        //DCT反变换
        mImageMatrix = ImageUtil.makeImageMatrix(pix);
        mImageMatrix = mDCTMatrix.mtimes(mImageMatrix).mtimes(mDCTMatrix);
        pix = mImageMatrix.toDoubleArray();
        set8Blk(pix, x, y);
    }

    private void set8Blk(double[][] pix,  int x, int y) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                EncodeValue[x + i][y + j] = pix[i][j];
            }
        }
    }

    private double[][] get8Blk (int x, int y) {
        if(x > mHeight || y > mWidth){
            Log.e("get8Blk error!:", "error! overflow!");
        }
        double[][] new8Blk = new double[8][8];
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                new8Blk[i][j] = EncodeValue[x+i][y+j];
            }
        }
        return new8Blk;
    }
}

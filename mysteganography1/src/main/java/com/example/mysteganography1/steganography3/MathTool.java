package com.example.mysteganography1.steganography3;

import java.util.Random;

public class MathTool {
    public static double[][][] intToDoubleMatrix(int[][][] input) {
        int i = input.length;
        int width = input[0].length;
        int height = input[0][0].length;
        double[][][] output = new double[i][height][width];
        for (int m = 0; m < i; m++) {
            output[m] = intToDoubleMatrix(input[m]);
            //System.out.println();
        }
        return output;
    }
    public static double[][] intToDoubleMatrix(int[][] input) {
        int height = input.length;
        int width = input[0].length;
        double[][] output = new double[height][width];
        for (int i = 0; i < height; i++) {
            // 列
            for (int j = 0; j < width; j++) {
                // 行
                output[i][j] = Double.valueOf(String.valueOf(input[i][j]));
                //System.out.print(output[i][j]);
            }
            //System.out.println();
        }
        return output;
    }

    public static double[] intToDoubleArray(int[] input) {
        int length = input.length;
        double[] output = new double[length];
        for (int i = 0; i < length; i++)
            output[i] = Double.valueOf(String.valueOf(input[i]));
        return output;
    }

    public static int[] intFromDoubleArray(double[] input) {
        int length = input.length;
        int[] output = new int[length];
        for (int i = 0; i < length; i++) {
            output[i] = (int) input[i];
        }
        return output;

    }public static int[][] intFromDouble2DArray(double[][] input) {
        int height = input.length;
        int width = input[0].length;
        int[][] output = new int[height][width];
        for (int i = 0; i < height; i++) {
            // 列
            for (int j = 0; j < width; j++) {
                // 行
                output[i][j] = (int) input[i][j];
                //System.out.print(output[i][j]);
            }
            //System.out.println();
        }
        return output;
    }

    public static double[] zigZagArrayFrom2DArray(double[][] input){
        int width,height;
        height = input.length;
        width = input[0].length;
        int s,i,j,dir;
        int squa;

        squa=height*width;
        double [] result = new double[squa];
        i=0;     //行
        j=0;     //列
        s=0;     //计数
        dir=0;   //四个行进方向0（right）,1（left_down）,2（down）,3（right_up）
        while(s<squa)
        {
            result[s] = input[i][j];
            //设置下一点（位置和方向）
            switch(dir)
            {
                case 0:
                    //位置
                    j++;
                    //行进方向
                    if(0==i)
                        dir=1;
                    if(height-1==i)
                        dir=3;
                    break;
                case 1:
                    i++;
                    j--;
                    if(height-1==i)
                        dir=0;
                    else if(0==j)
                        dir=2;
                    break;
                case 2:
                    i++;
                    if(0==j)
                        dir=3;
                    if(width-1==j)
                        dir=1;
                    break;
                case 3:
                    i--;
                    j++;
                    if(width-1==j)
                        dir=2;
                    else if(0==i)
                        dir=0;
                    break;
                default:
                    break;
            }
            s++;
        }
        return result;
    }

    public static double[][] zigZagArrayTo2DArray(double[] input, int width, int height){
        int s,i,j,dir;
        double [][] result = new double[height][width];
        i=0;     //行
        j=0;     //列
        s=0;     //计数
        dir=0;   //四个行进方向0（right）,1（left_down）,2（down）,3（right_up）
        while(s < height * width)
        {
            result[i][j] = input[s];
            //设置下一点（位置和方向）
            switch(dir)
            {
                case 0:
                    //位置
                    j++;
                    //行进方向
                    if(0==i)
                        dir=1;
                    if(height-1==i)
                        dir=3;
                    break;
                case 1:
                    i++;
                    j--;
                    if(height-1==i)
                        dir=0;
                    else if(0==j)
                        dir=2;
                    break;
                case 2:
                    i++;
                    if(0==j)
                        dir=3;
                    if(width-1==j)
                        dir=1;
                    break;
                case 3:
                    i--;
                    j++;
                    if(width-1==j)
                        dir=2;
                    else if(0==i)
                        dir=0;
                    break;
                default:
                    break;
            }
            s++;
        }
        return result;
    }

    public static double[] random_sort(double[] args, int sortBit) {
        int i;
        double [] result = args;
        //初始的有序数组
        /*System.out.println("初始有序数组1:");
        for (i = 0; i < result.length; i++) {
            System.out.print(" " + result[i]);
        }*/
        //费雪耶兹置乱算法
        Random rand = new Random(sortBit);
        //System.out.println("\n" + "每次生成的随机交换位置2:");
        for (i = result.length - 1; i > 0; i--) {
            //随机数生成器，范围[0, i]
            int randtmp = rand.nextInt(i+1);
            //System.out.print(" " + randtmp);
            double temp = result[i];
            result[i] = result[randtmp];
            result[randtmp] = temp;
        }
        //置换之后的数组
        /*System.out.println("\n" + "置换后的数组3:");
        for (int k = 0; k < result.length; k++) {
            System.out.print(" " + result[k]);
        }*/
        return result;
    }

    public static double[] random_Fsort(double[] args, int sortBit) {
        int i;
        int arr[] = new int [args.length];
        double [] result = args;
        //初始的有序数组
        /*System.out.println("\n" + "置换后的数组4:");
        for (i = 0; i < args.length; i++) {
            System.out.print(" " + args[i]);
        }*/
        //费雪耶兹置乱算法
        Random rand = new Random(sortBit);
        //System.out.println("\n" + "每次生成的随机交换位置5:");
        for (i = result.length - 1; i > 0; i--) {
            //随机数生成器，范围[0, i]
            int randtmp = rand.nextInt(i+1);
            //System.out.print(" " + randtmp);
            arr[i] = randtmp;
        }
        for (i = 1; i < result.length; i++) {
            //随机数生成器，范围[0, i]
            int randtmp = arr[i];
            double temp = result[i];
            result[i] = result[randtmp];
            result[randtmp] = temp;
        }
        //置换之后的数组
        /*System.out.println("\n" + "置换后的数组6:");
        for (int k = 0; k < result.length; k++) {
            System.out.print(" " + result[k]);
        }*/
        return result;
    }
}

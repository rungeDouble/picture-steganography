package com.example.runge.PictureSteganography.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveFiles {

    public static File createImageFileFromCamera() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Pst_"+timeStamp;

        String pathdir = Environment.getExternalStorageDirectory().getPath() + "/runge/Camera";
        File storageDir = new File(pathdir);
        if(!storageDir.exists()){
            storageDir.mkdir();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public static File createImageFileAfterEncoding() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Pst_"+timeStamp;

        String pathdir = Environment.getExternalStorageDirectory().getPath() + "/runge/Encoded";
        File storageDir = new File(pathdir);
        //File storageroot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Steganography");
        /*if(!storageroot.exists()){
            storageroot.mkdir();
        }

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Steganography/Encoded");*/
        if(!storageDir.exists()){
            storageDir.mkdir();
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public static File createTxtFile(String imagename) throws IOException {

        /*File storageroot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Steganography");
        if(!storageroot.exists()){
            storageroot.mkdir();
        }*/

        String pathdir = Environment.getExternalStorageDirectory().getPath() + "/runge/TXTs";
        File storageDir = new File(pathdir);
        //File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Steganography/TXTs");
        if(!storageDir.exists()){
            storageDir.mkdir();
        }

        File txt= File.createTempFile(
                imagename,  /* prefix */
                ".txt",         /* suffix */
                storageDir      /* directory */
        );

        return txt;
    }
}
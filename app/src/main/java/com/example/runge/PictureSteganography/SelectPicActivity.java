package com.example.runge.PictureSteganography;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.runge.PictureSteganography.utils.SaveFiles;
import com.example.runge.PictureSteganography.utils.UriRealPath;

import java.io.File;
import java.io.IOException;

import offlinefiles.Offline_Files_Choose_Activity;

/**
 *
 */
public class SelectPicActivity extends Activity{

    private static final int REQUEST_TAKE_PHOTO = 1; //intent code for opening the camera app
    private static final int REQUEST_OPEN_GALLERY_BY_ENCODE= 2; //intent code for opening the gallery for the Encoding
    private static final int REQUEST_OPEN_GALLERY_BY_DECODE= 3; //intent code for opening the gallery for the Decoding
    private static final int REQUEST_CAMERA= 4;
    private static final int REQUEST_STORAGE_BY_ENCODE= 5;
    private static final int REQUEST_STORAGE_BY_DECODE= 6;
    private static final int REQUEST_STORAGE_CAMERA= 7;
    private static final int REQUEST_OPEN_File_BY_ENCODE= 8;
    private static final int REQUEST_OPEN_File_BY_DECODE= 9;

    public static final String SELECT_PIC_take_photo = "take_photo";
    public static final String SELECT_PIC_phone_pic = "photo_pic";
    public static final String SELECT_PIC_phone_file = "photo_file";
    public static final String PATH = "way_to_do";

    String title,type;

    private String photo_path;

    TextView tvchooseText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_pic);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 初始化加载View
     */
    private void initView() {
        LinearLayout tvTakePhoto = (LinearLayout) findViewById(R.id.tv_take_photo);
        LinearLayout tvPhonePic = (LinearLayout) findViewById(R.id.tv_phone_pic);
        LinearLayout tvPhonefile = (LinearLayout) findViewById(R.id.tv_phone_file);
        Intent intent = getIntent();
        type = intent.getStringExtra("Type");
        title = intent.getStringExtra("title");
        if(type.equals("decode")) {
            tvTakePhoto.setVisibility(View.INVISIBLE);
        }

        AlphaAnimation fade = new AlphaAnimation(0.0f, 1.0f);
        fade.setDuration(500);
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                AlphaAnimation fadeIn= new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setDuration(1000);
                tvchooseText = (TextView) findViewById(R.id.chooseText);
                tvchooseText.startAnimation(fadeIn);
                tvchooseText.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    public void fadeOut(View view){
        view.setEnabled(false);

    }

    public void chooseFile(View view) {
        Intent chooseFileIntent = new Intent(SelectPicActivity.this, Offline_Files_Choose_Activity.class);
        // Ensure that there's a camera activity to handle the intent
        chooseFileIntent.putExtra("title",title);
        chooseFileIntent.putExtra("Type",type);
        if (type.equals("encode")){
            startActivityForResult(chooseFileIntent, REQUEST_OPEN_File_BY_ENCODE);
        }
        else {
            startActivityForResult(chooseFileIntent, REQUEST_OPEN_File_BY_DECODE);
        }


    }

    public void takePhoto(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                checkStoragePermissionForCamera();
            } else {
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){
                    Toast.makeText(this,"Camera permission is needed to take a photo.", Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},REQUEST_CAMERA);
            }
        }
        else takePhoto();
    }

    private void checkStoragePermissionForCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                takePhoto();
            } else {
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(this,"External storage permission is needed to create a photo.", Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_STORAGE_CAMERA);
            }
        }
        else takePhoto();
    }

    private void takePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = SaveFiles.createImageFileFromCamera();
            } catch (IOException ex) {
                Toast alert = Toast.makeText(getApplicationContext(), "Cannot create image file.", Toast.LENGTH_SHORT);
                alert.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photo_path = photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void chooseFromGalleryForEncoding(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                browseGallery(REQUEST_OPEN_GALLERY_BY_ENCODE);
            } else {
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(this,"External storage permission is needed to explore gallery.", Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_STORAGE_BY_ENCODE);
            }
        }
        else {
            if (type.equals("encode")){
                browseGallery(REQUEST_OPEN_GALLERY_BY_ENCODE);
            }
            else {
                browseGallery(REQUEST_OPEN_GALLERY_BY_DECODE);
            }
        }
    }

    /**************************************************************
     * 系统图库模块
     * @param requestCode
     */
    private void browseGallery(int requestCode){

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) { //if the user clicked ok, we can pass the path to the Activity
            MediaScannerConnection.scanFile(getApplicationContext(),new String[]{photo_path},null,null);

            Intent selfiSrc = new Intent(this, EncodingActivity.class);
            selfiSrc.putExtra("path", photo_path);
            startActivity(selfiSrc);
        }

        if(requestCode == REQUEST_OPEN_GALLERY_BY_ENCODE && resultCode== RESULT_OK){
            Intent startEncoding= new Intent(this, EncodingActivity.class);
            Uri selectedImageUri = data.getData();

            photo_path= null;

            if(Build.VERSION.SDK_INT>=19){
                photo_path= UriRealPath.getRealPathFromURI_API19(this,selectedImageUri);
            }
            else if(Build.VERSION.SDK_INT>=11 && Build.VERSION.SDK_INT<=18){
                photo_path= UriRealPath.getRealPathFromURI_API11to18(this,selectedImageUri);
            }

            if(photo_path==null){
                Toast alert = Toast.makeText(getApplicationContext(), "Invalid path. Change folder or select another image.", Toast.LENGTH_LONG);
                alert.show();
            }
            else {
                startEncoding.putExtra("path",photo_path);
                startActivity(startEncoding);
            }
        }

        if(requestCode == REQUEST_OPEN_GALLERY_BY_DECODE && resultCode== RESULT_OK){
            Intent startDecoding= new Intent(this, DecodingActivity.class);
            Uri selectedImageUri = data.getData();

            photo_path= null;

            if(Build.VERSION.SDK_INT>=19){
                photo_path= UriRealPath.getRealPathFromURI_API19(this,selectedImageUri);
            }
            else if(Build.VERSION.SDK_INT>=11 && Build.VERSION.SDK_INT<=18){
                photo_path= UriRealPath.getRealPathFromURI_API11to18(this,selectedImageUri);
            }

            if(photo_path==null){
                Toast alert = Toast.makeText(getApplicationContext(), "Invalid path. Change folder or select another image.", Toast.LENGTH_LONG);
                alert.show();
            }
            else {
                startDecoding.putExtra("path",photo_path);
                startActivity(startDecoding);
            }
        }
        if(requestCode == REQUEST_OPEN_File_BY_ENCODE && resultCode== RESULT_OK){
            Intent startEncoding= new Intent(this, EncodingActivity.class);
            photo_path= data.getStringExtra("path");

            if(photo_path==null){
                Toast alert = Toast.makeText(getApplicationContext(), "Invalid path. Change folder or select another image.", Toast.LENGTH_LONG);
                alert.show();
            }
            else {
                startEncoding.putExtra("path",photo_path);
                startActivity(startEncoding);
            }
        }
        if(requestCode == REQUEST_OPEN_File_BY_DECODE && resultCode== RESULT_OK){
            Intent startDecoding= new Intent(this, DecodingActivity.class);
            photo_path= data.getStringExtra("path");

            if(photo_path==null){
                Toast alert = Toast.makeText(getApplicationContext(), "Invalid path. Change folder or select another image.", Toast.LENGTH_LONG);
                alert.show();
            }
            else {
                startDecoding.putExtra("path",photo_path);
                startActivity(startDecoding);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CAMERA){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                checkStoragePermissionForCamera();
            } else {
                Toast.makeText(this,"Camera permission was not granted.", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode==REQUEST_STORAGE_BY_ENCODE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                browseGallery(REQUEST_OPEN_GALLERY_BY_ENCODE);
            } else {
                Toast.makeText(this,"External storage permission was not granted.", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode==REQUEST_STORAGE_BY_DECODE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                browseGallery(REQUEST_OPEN_GALLERY_BY_DECODE);
            } else {
                Toast.makeText(this,"External storage permission was not granted.", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode==REQUEST_STORAGE_CAMERA){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                takePhoto();
            } else {
                Toast.makeText(this,"External storage permission was not granted.", Toast.LENGTH_SHORT).show();
            }
        }
        else super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


}

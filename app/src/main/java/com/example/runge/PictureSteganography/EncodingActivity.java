package com.example.runge.PictureSteganography;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mysteganography1.steganography2.Steg;
import com.example.mysteganography1.steganography3.AddWatermark;
import com.example.runge.PictureSteganography.utils.OutputStats;
import com.example.runge.PictureSteganography.utils.SaveFiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import steganography.EncodeRS;

import static com.example.mysteganography1.steganography3.ImageUtil.arrayTo2DArray;
import static com.example.runge.PictureSteganography.MainActivity.getCurrentTime2;


public class EncodingActivity extends AppCompatActivity {

    private static final int ENCODING_DONE = 0;
    private static final int SAVED_IMG = 1;
    private static final int ENCODING_FAILURE = -1;

    public static String Image_Name = "";
    public static String Image_Path = "";
    public static String Image_time = "";

    static double SNR;
    static double PSNR1;
    static double PSNR2;

    private Bitmap bmp;
    private ImageView photoImageView;
    private TextView status, errorText;
    private EditText inputText;
    private File imgFile;
    private Button encodeButton, clearButton, goBackButton;
    RadioButton mRadioButton,mRadioButton2,mRadioButton3;
    private long time;
    File file = null;

    private String toEncrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encoding);

        SNR = 0;
        PSNR1 = 0;
        PSNR2 = 0;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Image_Path = (String) extras.get("path");
            Image_Name = (new File(Image_Path)).getName();
            Image_time = getCurrentTime2();
            MainActivity.insertfromEncode();
        }

        imgFile = new File(Image_Path);
        if (imgFile == null || !(imgFile.exists())) {
            encodeButton = (Button) findViewById(R.id.encodeButton);
            clearButton = (Button) findViewById(R.id.clearButton);
            errorText = (TextView) findViewById(R.id.errorEncodingtextView);
            status = (TextView) findViewById(R.id.statusEncText);
            inputText = (EditText) findViewById(R.id.inputText);

            encodeButton.setEnabled(false);
            clearButton.setEnabled(false);

            errorText.setVisibility(View.VISIBLE);
            encodeButton.setVisibility(View.INVISIBLE);
            clearButton.setVisibility(View.INVISIBLE);
            inputText.setVisibility(View.INVISIBLE);

            status.setText("Image not found. Please try again.");
            status.setTextColor(Color.RED);
        } else if (imgFile.exists()) {
            photoImageView = (ImageView) findViewById(R.id.photoImageView);
            //photoImageView.setImageURI(Uri.fromFile(new File(path)));

            Glide.with(this).load(imgFile).into(photoImageView);
        }
    }

    public void encodeClicked(View view) {
        inputText = (EditText) findViewById(R.id.inputText);
        encodeButton = (Button) findViewById(R.id.encodeButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        goBackButton = (Button) findViewById(R.id.backEncodingButton);

        mRadioButton = (RadioButton) findViewById(R.id.radioButton);
        mRadioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        mRadioButton3 = (RadioButton) findViewById(R.id.radioButton3);

        encodeButton.setEnabled(false);
        clearButton.setEnabled(false);
        goBackButton.setEnabled(false);
        toEncrypt = inputText.getText().toString();
        if (toEncrypt.equals("")) {
            Toast.makeText(getApplicationContext(), "Type some text to encode!", Toast.LENGTH_SHORT).show();
            encodeButton.setEnabled(true);
            clearButton.setEnabled(true);
            goBackButton.setEnabled(true);
        }
        else {
            time = System.currentTimeMillis();

            status = (TextView) findViewById(R.id.statusEncText);
            status.setText("Please wait... 0%");
            status.setTextColor(Color.YELLOW);
            //new AsyncEncode().execute();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        if(mRadioButton.isChecked()) {
                            bmp = EncodeRS.encode(toEncrypt, getApplicationContext(), imgFile);
                        }
                        else if (mRadioButton2.isChecked()) {
                            bmp = Steg.withInput(BitmapFactory.decodeFile(imgFile.getPath())).encode(toEncrypt).intoBitmap();
                        }else {
                            bmp = new AddWatermark(BitmapFactory.decodeFile(imgFile.getPath()), toEncrypt).addDctwaterMark(100,1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bmp != null){
                        handler.sendEmptyMessage(ENCODING_DONE);
                        PSNR(BitmapFactory.decodeFile(imgFile.getPath()), bmp, bmp.getWidth(), bmp.getHeight());
                    }
                    else handler.sendEmptyMessage(ENCODING_FAILURE);
                }
            };

            Thread thread = new Thread(r);
            thread.start();
        }

    }

    private void saveImageThread() {
        Runnable r2 = new Runnable() {
            @Override
            public void run() {

                try { //TODO: asynctask
                    file = SaveFiles.createImageFileAfterEncoding();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (IOException e) {
                }
                //asynctask
                handler.sendEmptyMessage(SAVED_IMG);
            }
        };

        Thread thread = new Thread(r2);
        thread.start();
    }

    public void clearText(View view) {
        inputText = (EditText) findViewById(R.id.inputText);
        inputText.setText("");
    }

    public void goBackFromEncoding(View view) {
        Intent gotoMain = new Intent(EncodingActivity.this, MainActivity.class);
        gotoMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(gotoMain);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int code = msg.what;

            if (code == ENCODING_DONE) {
                status.setText("Saving image... 75%\n(lossless - can be slow)");
                time = System.currentTimeMillis() - time;
                saveImageThread();
            } else if (code == SAVED_IMG) {
                status.setText("Done! 100%");
                status.setTextColor(Color.GREEN);

                bmp.recycle(); //cleanup
                OutputStats out = new OutputStats(file.getPath(), toEncrypt, time, SNR, PSNR1, PSNR2);
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getPath()}, null, null);

                Intent startEncodedActivity = new Intent(EncodingActivity.this, EncodedActivity.class);
                startEncodedActivity.putExtra("resultfromencoding", out);
                startActivity(startEncodedActivity);
            } else if (code == ENCODING_FAILURE) {
                Toast.makeText(getApplicationContext(), "Text cannot be encoded. Try with a different text or choose another pic.", Toast.LENGTH_SHORT).show();
                encodeButton.setEnabled(true);
                clearButton.setEnabled(true);
                goBackButton.setEnabled(true);
            }
        }
    };

    @Override
    public void onBackPressed() {
        Intent gotoMain = new Intent(EncodingActivity.this, MainActivity.class);
        gotoMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(gotoMain);
    }

    public void PSNR(Bitmap img1, Bitmap img2, int width, int height) {
        double peak, signal, noise, mse;

        int [] imgArray1 = new int[height*width];
        int [] imgArray2 = new int[height*width];

        img1.getPixels(imgArray1, 0, width, 0, 0, width, height);
        img2.getPixels(imgArray2, 0, width, 0, 0, width, height);

        int [][]img2DArray1 = arrayTo2DArray(imgArray1, width , height);
        int [][]img2DArray2 = arrayTo2DArray(imgArray2, width , height);


        signal = noise = peak = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                signal += img2DArray1[i][j] * img2DArray1[i][j];
                noise += (img2DArray1[i][j] - img2DArray2[i][j]) * (img2DArray1[i][j] - img2DArray2[i][j]);
                if (peak < img2DArray1[i][j])
                    peak = img2DArray1[i][j];
            }
        }

        mse = noise / (height * width);
        SNR = 10 * log10(signal / noise);
        PSNR1 = 10 * log10(255 * 255 / mse);
        PSNR2 =  10 * log10((peak * peak) / mse);
        System.out.println("MSE: " + mse);
        System.out.println("SNR: " + 10 * log10(signal / noise));
        System.out.println("PSNR(max=255): " + (10 * log10(255 * 255 / mse)));
        System.out.println("PSNR(max=" + peak + "): " + 10 * log10((peak * peak) / mse));
    }

    public static double log10(double x) {
        return Math.log(x)/Math.log(10);
    }
}

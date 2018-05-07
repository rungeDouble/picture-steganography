package com.example.runge.PictureSteganography;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mysteganography1.steganography2.Steg;
import com.example.mysteganography1.steganography3.ExtractWatermark;
import com.example.runge.PictureSteganography.utils.OutputStats;

import java.io.File;

import steganography.DecodeRS;

import static com.example.runge.PictureSteganography.MainActivity.getCurrentTime2;

public class DecodingActivity extends AppCompatActivity {

    private static final int DECODING1_SUCCESFULLY= 0;
    private static final int DECODING2_SUCCESFULLY= 1;
    private static final int DECODING3_SUCCESFULLY= 2;
    private static final int DECODING_FAILURE= -1;

    public static String Image_Name = "";
    public static String Image_Path = "";
    public static String Image_Time = "";

    private File imgFile;
    private Button decodeButton, chooseAnotherPicButton;
    private ImageView decodingImageView;
    private TextView statusDecoding,errorDecodingText;
    private String output, output2, output3;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoding);

        Bundle extras= getIntent().getExtras();
        if(extras!=null)
        {
            Image_Path = (String) extras.get("path");
            Image_Name = (new File(Image_Path)).getName();
            Image_Time = getCurrentTime2();
            MainActivity.insertfromDecode();
        }

        imgFile = new File(Image_Path);
        if (imgFile==null || !(imgFile.exists())){
            decodeButton= (Button) findViewById(R.id.decodeButton);
            errorDecodingText= (TextView) findViewById(R.id.errorDecodingText);
            statusDecoding= (TextView) findViewById(R.id.statusDecodingText);

            statusDecoding.setText("Image not found. Please try again");
            statusDecoding.setTextColor(Color.RED);
            errorDecodingText.setVisibility(View.VISIBLE);

            decodeButton.setEnabled(false);
        } else if(imgFile.exists()){
            decodingImageView= (ImageView) findViewById(R.id.decodingImageView);
            //decodingImageView.setImageURI(Uri.fromFile(sourceFile));
            Glide.with(this).load(imgFile).into(decodingImageView);
        }
    }

    public void decodeClicked(View view) {
        decodeButton = (Button) findViewById(R.id.decodeButton);
        chooseAnotherPicButton = (Button) findViewById(R.id.chooseAnotherPicButton);
        statusDecoding = (TextView) findViewById(R.id.statusDecodingText);

        statusDecoding.setText("Decoding in progress...");
        statusDecoding.setTextColor(Color.YELLOW);

        decodeButton.setEnabled(false);
        chooseAnotherPicButton.setEnabled(false);

        output= null;
        output2= null;
        output3= null;

        time= System.currentTimeMillis();

        Runnable r= new Runnable() {
            @Override
            public void run() {
                output = DecodeRS.decode(imgFile, getApplicationContext());
                Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getPath());
                try {
                    output2 = Steg.withInput(imgBitmap).decode().intoString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ExtractWatermark decode = new ExtractWatermark(BitmapFactory.decodeFile(imgFile.getPath()));
                output3 = decode.ExtractDctwaterMark(100);
                time = System.currentTimeMillis() - time;
                if (output != null) handler.sendEmptyMessage(DECODING1_SUCCESFULLY);
                else if (output2 != null) handler.sendEmptyMessage(DECODING2_SUCCESFULLY);
                else if (output3 != null) handler.sendEmptyMessage(DECODING3_SUCCESFULLY);
                else handler.sendEmptyMessage(DECODING_FAILURE);
            }

        };

        Thread thread= new Thread(r);
        thread.start();
    }

    public void goBackFromDecoding(View view){
        Intent gotoMain= new Intent(DecodingActivity.this, MainActivity.class);
        gotoMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(gotoMain);
    }

    private Handler handler= new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int code= msg.what;

            if(code==DECODING1_SUCCESFULLY) {
                statusDecoding.setText("Done!");
                statusDecoding.setTextColor(Color.GREEN);

                Intent gotoDecoded= new Intent(DecodingActivity.this, DecodedActivity.class);
                OutputStats out= new OutputStats(imgFile.getName().split(".png")[0],output,time,0,0,0);
                gotoDecoded.putExtra("outputstas",out);
                startActivity(gotoDecoded);
            }
            else if(code==DECODING2_SUCCESFULLY) {
                statusDecoding.setText("Done!");
                statusDecoding.setTextColor(Color.GREEN);

                Intent gotoDecoded= new Intent(DecodingActivity.this, DecodedActivity.class);
                OutputStats out= new OutputStats(imgFile.getName().split(".png")[0],output2,time,0,0,0);
                gotoDecoded.putExtra("outputstas",out);
                startActivity(gotoDecoded);
            }
            else if(code==DECODING3_SUCCESFULLY) {
                statusDecoding.setText("Done!");
                statusDecoding.setTextColor(Color.GREEN);

                Intent gotoDecoded= new Intent(DecodingActivity.this, DecodedActivity.class);
                OutputStats out= new OutputStats(imgFile.getName().split(".png")[0],output3,time,0,0,0);
                gotoDecoded.putExtra("outputstas",out);
                startActivity(gotoDecoded);
            }
            else if(code==DECODING_FAILURE){
                Toast alert = Toast.makeText(getApplicationContext(), "String not found! Try with another pic.", Toast.LENGTH_SHORT);
                alert.show();

                statusDecoding.setText("Decoding unsuccessful.");
                statusDecoding.setTextColor(Color.RED);

                decodeButton.setEnabled(true);
                chooseAnotherPicButton.setEnabled(true);
            }
        }

    };
}

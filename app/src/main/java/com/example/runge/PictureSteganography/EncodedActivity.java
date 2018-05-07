package com.example.runge.PictureSteganography;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.example.runge.PictureSteganography.utils.OutputStats;

import java.io.File;

public class EncodedActivity extends AppCompatActivity {

    private OutputStats out;
    private String path;
    private String msg;
    private long time;

    private TextView outputStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encoded);

        Bundle bundle= getIntent().getExtras();
        out= (OutputStats) bundle.get("resultfromencoding");
        displayStats();
    }

    private void displayStats(){
        path= out.getPath();
        msg= out.getMsg();
        time= out.getTime();
        double SNR,PSNR1,PSNR2;
        SNR = out.getSNR();
        PSNR1 = out.getPSNR1();
        PSNR2 = out.getPSNR2();

        outputStats = (TextView) findViewById(R.id.logstatusTextView);
        outputStats.setText
                ("Status: ENCODED SUCCESSFULLY" +
                        "\\n\\nImage saved in: "+path+"" +
                        "\n\nOperation done in: "+time+ "ms"+
                        "\n\nSNR = "+SNR+
                        "\n\nPSNR(Max255) = "+PSNR1 +
                        "\n\nText encoded: \n\n"+msg);
        outputStats.setMovementMethod(new ScrollingMovementMethod());
    }

    public void backToMain(View view){
        Intent gotoMain= new Intent(EncodedActivity.this, MainActivity.class);
        gotoMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(gotoMain);
    }
    public void sendImg(View view){
        path= out.getPath();
        Intent sendImg = new Intent(EncodedActivity.this, SelectWayActivity.class);
        sendImg.putExtra("path",path);
        sendImg.putExtra("filename",(new File(path)).getName());
        startActivity(sendImg);
    }

    public void shareImg(View view){
        showAlert();
    }

    private void showAlert(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(EncodedActivity.this);
        a_builder.setMessage("大多数的应用会对图片进行压缩，使用未知的压缩方式压缩图片可能丢失隐藏其中的信息，请谨慎选择分享方式！")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendImgToOthers();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("警告");
        alert.show();
    }

    private void sendImgToOthers(){
        path= out.getPath();
        Uri uri= Uri.fromFile(new File(path));
        Intent intent= new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,"Shared using Picture Steganography app.");
        Intent chooser= Intent.createChooser(intent,"Send Image");
        startActivity(chooser);
    }

    @Override
    public void onBackPressed() {
        Intent gotoMain= new Intent(EncodedActivity.this, MainActivity.class);
        gotoMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(gotoMain);
    }
}
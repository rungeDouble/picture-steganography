package com.example.runge.PictureSteganography;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

import filestrans.Files_Trans_Activity;

public class SelectWayActivity extends Activity implements View.OnClickListener{

    public String PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_way);
        PATH = getIntent().getStringExtra("path");
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
        LinearLayout byBlueteen = (LinearLayout) findViewById(R.id.by_blueteen);
        LinearLayout byIntnet = (LinearLayout) findViewById(R.id.by_intnet);
        LinearLayout byOther = (LinearLayout) findViewById(R.id.by_other);
        byBlueteen.setOnClickListener(this);
        byIntnet.setOnClickListener(this);
        byOther.setOnClickListener(this);
        ImageView mImageView = (ImageView) findViewById(R.id.tv_ImageResult);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(PATH));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.by_blueteen:{
                File file = new File(PATH);

                Uri uri = Uri.fromFile(file);
                //打开系统蓝牙模块并发送文件
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("*/*");
                sharingIntent.setPackage("com.android.bluetooth");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(sharingIntent, "Share"));
                Log.d("SelectWayActivity", uri.getPath());//log打印返回的文件路径
                break;
            }
            case R.id.by_intnet:{
                File file = new File(PATH);

                Intent lastIntent = new Intent(SelectWayActivity.this, Files_Trans_Activity.class);
                lastIntent.putExtra("path", PATH);
                lastIntent.putExtra("filename", file.getName());
                startActivity(lastIntent);
                break;
            }
            case R.id.by_other:{
                showAlert();
                break;
            }
            default:{
                finish();
                break;
            }
        }
    }

    private void showAlert(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(SelectWayActivity.this);
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
        Uri uri= Uri.fromFile(new File(PATH));
        Intent intent= new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,"Shared using Picture Steganography app.");
        Intent chooser= Intent.createChooser(intent,"Send Image");
        startActivity(chooser);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }
}

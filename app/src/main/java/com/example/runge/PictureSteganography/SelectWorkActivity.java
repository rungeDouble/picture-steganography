package com.example.runge.PictureSteganography;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 */
public class SelectWorkActivity extends Activity implements View.OnClickListener{


    //encode
    public static final String SELECT_WORK_Toencode = "toencode";
    //decode
    public static final String SELECT_WORK_Todecode = "todecode";
    public static final String WORK = "what_to_do";

    TextView tvchooseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_work);
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
        LinearLayout toEncoding = (LinearLayout) findViewById(R.id.to_encoding);
        LinearLayout toDecoding = (LinearLayout) findViewById(R.id.to_decoding);
        toEncoding.setOnClickListener(this);
        toDecoding.setOnClickListener(this);

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
                tvchooseText = (TextView) findViewById(R.id.instructionTextView);
                tvchooseText.startAnimation(fadeIn);
                tvchooseText.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        String path = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            path = (String) extras.get("FilePath");
            switch (v.getId()) {
                case R.id.to_encoding:{
                    Intent lastIntent = new Intent(SelectWorkActivity.this, EncodingActivity.class);
                    lastIntent.putExtra(WORK, SELECT_WORK_Toencode);
                    lastIntent.putExtra("path", path);
                    startActivity(lastIntent);
                    break;
                }
                case R.id.to_decoding:{
                    Intent lastIntent = new Intent(SelectWorkActivity.this, DecodingActivity.class);
                    lastIntent.putExtra(WORK, SELECT_WORK_Todecode);
                    lastIntent.putExtra("path", path);
                    startActivity(lastIntent);
                    break;
                }
                default:{
                    finish();
                    break;
                }
            }
        }else {
            Toast.makeText(this, "can't get file message", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }
}

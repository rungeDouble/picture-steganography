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

public class SelectFunctionActivity extends Activity implements View.OnClickListener{


    public static final String WORK = "Function";

    TextView tvchooseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_function);
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
        LinearLayout byFunction1 = (LinearLayout) findViewById(R.id.by_Function1);
        LinearLayout byFunction2 = (LinearLayout) findViewById(R.id.by_Function2);
        LinearLayout byFunction3 = (LinearLayout) findViewById(R.id.by_Function3);
        byFunction1.setOnClickListener(this);
        byFunction2.setOnClickListener(this);
        byFunction3.setOnClickListener(this);

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
                tvchooseText = (TextView) findViewById(R.id.instruction2TextView);
                tvchooseText.startAnimation(fadeIn);
                tvchooseText.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.by_Function1:{
                Intent lastIntent = new Intent();
                lastIntent.putExtra("function", "1");
                setResult(RESULT_OK,lastIntent);
                SelectFunctionActivity.this.setResult(RESULT_OK,lastIntent);
                SelectFunctionActivity.this.finish();
                break;
            }
            case R.id.by_Function2:{
                Intent lastIntent = new Intent();
                lastIntent.putExtra("function", "2");
                setResult(RESULT_OK,lastIntent);
                SelectFunctionActivity.this.setResult(RESULT_OK,lastIntent);
                SelectFunctionActivity.this.finish();
                break;
            }case R.id.by_Function3:{
                Intent lastIntent = new Intent();
                lastIntent.putExtra("function", "3");
                SelectFunctionActivity.this.setResult(RESULT_OK,lastIntent);
                SelectFunctionActivity.this.finish();
                break;
            }
            default:{
                finish();
                break;
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }
}


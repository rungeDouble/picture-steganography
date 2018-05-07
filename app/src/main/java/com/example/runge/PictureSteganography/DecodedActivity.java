package com.example.runge.PictureSteganography;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.runge.PictureSteganography.utils.SaveFiles;
import com.example.runge.PictureSteganography.utils.OutputStats;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DecodedActivity extends AppCompatActivity {

        private String decodemsg;
        private long time;
        private String filename;

        private TextView outputText;

        private String toTextView="";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_decoded);

            Bundle bundle= getIntent().getExtras();
            OutputStats out= (OutputStats) bundle.get("outputstas");
            decodemsg= out.getMsg();
            filename= out.getPath();
            time= out.getTime();

            displaystats();
        }

    public void displaystats(){
        outputText= (TextView) findViewById(R.id.outputText);
        outputText.setMovementMethod(new ScrollingMovementMethod());

        toTextView= "Status: DECODED SUCCESSFULLY\nOperation done in: "+time+"ms\n\nText decoded:\n\n\""+decodemsg+"\"";

        outputText.setText(toTextView);
    }

    public void copyToClip(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("provola",decodemsg);
        clipboard.setPrimaryClip(clip);
        Toast alert = Toast.makeText(getApplicationContext(), "Message copied to clipboard.", Toast.LENGTH_SHORT);
        alert.show();
    }

    public void saveTxt(View view){
        File txt;

        try {
            txt= SaveFiles.createTxtFile(filename);
            FileOutputStream stream = new FileOutputStream(txt);
            stream.write(decodemsg.getBytes());
            stream.close();

            outputText.setText(toTextView+"\n\nTXT file saved in:\n\n"+txt.getPath());

            Toast alert = Toast.makeText(getApplicationContext(), "Txt Saved", Toast.LENGTH_SHORT);
            alert.show();
        } catch (IOException e) {
            Toast alert = Toast.makeText(getApplicationContext(), "Error creating txt", Toast.LENGTH_SHORT);
            alert.show();
        }

    }

    public void tryAnotherImage(View view){
        Intent gotoMain= new Intent(DecodedActivity.this, MainActivity.class);
        gotoMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(gotoMain);
    }

    @Override
    public void onBackPressed() {
        Intent gotoMain= new Intent(DecodedActivity.this, MainActivity.class);
        gotoMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(gotoMain);
    }
}

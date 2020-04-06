package com.example.twoactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUESTCODE_SUBACTIVITYNAME = 0;
    Button btn_newactivity;
    String subactivityName;
    TextView tv_text;

    // AsyncTask
    Button btn_Start, btn_Stop;
    ProgressBar progressBar; // 进度条



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_text = (TextView)findViewById(R.id.main_tv);


        btn_newactivity = (Button)findViewById(R.id.main_btn_newactivity);
        btn_newactivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent sub_intent = new Intent(getApplicationContext(), Sub1Activity.class);
                startActivityForResult(sub_intent, REQUESTCODE_SUBACTIVITYNAME);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_SUBACTIVITYNAME) {
            tv_text.setText(data.getStringExtra(Sub1Activity.RESULTEXTRA_ACITITYNAME));
            }
        super.onActivityResult(requestCode, resultCode, data);
    }


}





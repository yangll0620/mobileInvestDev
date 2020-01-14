package com.example.twoactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUESTCODE_SUBACTIVITYNAME = 0;
    Button btn;
    String subactivityName;
    TextView tvString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvString = (TextView)findViewById(R.id.main_tvString);


        btn = (Button)findViewById(R.id.main_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent sub_intent = new Intent(getApplicationContext(), Sub1Activity.class);
                startActivityForResult(sub_intent, REQUESTCODE_SUBACTIVITYNAME);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_SUBACTIVITYNAME) {
            tvString.setText(data.getStringExtra(Sub1Activity.RESULTEXTRA_ACITITYNAME));
            }
        super.onActivityResult(requestCode, resultCode, data);
    }


}

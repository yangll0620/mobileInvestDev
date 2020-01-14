package com.example.twoactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Sub1Activity extends AppCompatActivity {

    TextView tvString;
    Button btn;
    public static final String RESULTEXTRA_ACITITYNAME = "activityName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub1);

        tvString = (TextView)findViewById(R.id.sub1_tvString);

        btn = findViewById(R.id.sub1_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent();
                intent.putExtra(RESULTEXTRA_ACITITYNAME, tvString.getText());
                setResult(RESULT_OK, intent);
                finish();
            }
        });





    }
}

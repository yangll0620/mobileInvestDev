package com.example.sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddCountryActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAddrecord;
    private EditText etSubject, etDesc;

    private DBManager dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_country);


        setTitle("Add Record");

        btnAddrecord = (Button) findViewById(R.id.addcountry_btn_addrecord);

        etSubject = (EditText) findViewById(R.id.addcountry_et_subject);
        etDesc = (EditText) findViewById(R.id.addcountry_et_description);


        dbManager = new DBManager(this);
        dbManager.open();


        btnAddrecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.addcountry_btn_addrecord:

                final String name = etSubject.getText().toString();
                final String desc = etDesc.getText().toString();

                dbManager.insert(name, desc);

                Intent intent_main = new Intent(AddCountryActivity.this, CountryListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent_main);

                break;

        }
    }
}

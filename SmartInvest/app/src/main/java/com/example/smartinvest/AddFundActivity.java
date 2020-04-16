package com.example.smartinvest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddFundActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAddrecord;
    private EditText etFundName, etFundSymbol;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fund);

        setTitle("Add Record manually");

        btnAddrecord = (Button) findViewById(R.id.addfund_btn_addrecord);

        etFundSymbol = (EditText) findViewById(R.id.addfund_et_fundsymbol);
        etFundName = (EditText) findViewById(R.id.addfund_et_fundname);


        dbManager = new DBManager(this);
        dbManager.open();


        btnAddrecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.addfund_btn_addrecord:

                String fundSymbol = etFundSymbol.getText().toString();
                String fundName = etFundName.getText().toString();
                Fund fund = new Fund(fundSymbol, fundName);


                dbManager.insert(fund);
                dbManager.close();

                Intent intent_main = new Intent(AddFundActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent_main);

                break;

        }
    }
}

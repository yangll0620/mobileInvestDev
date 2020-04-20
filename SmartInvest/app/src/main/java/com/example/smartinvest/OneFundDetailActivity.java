package com.example.smartinvest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class OneFundDetailActivity extends AppCompatActivity {


    public static final String EXTRA_FUNDSYMBOL = "fundSymbol";
    public static final String EXTRA_FUNDNAME = "fundName";
    public static final String EXTRA_DBMANAGER = "dbManager";

    private Button btnAddTrans;

    String fundSymbol, fundName;
    DBManager dbManager;


    /** Onefund Transaction History List Related Variables **/
    ListView onefundTransList_lv;
    ArrayList<Transaction> onefundTransList_arrayList;
    OnefundTransListAdapter onefundTransList_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_fund_detail);

        Intent intent_main = getIntent();
        fundSymbol = intent_main.getStringExtra(EXTRA_FUNDSYMBOL);
        fundName = intent_main.getStringExtra(EXTRA_FUNDNAME);

        setTitle(fundName);


        /** SQLite databse**/
        dbManager = new DBManager(this);
        dbManager.open();


        /** Add Transcation Button **/
        btnAddTrans = (Button) findViewById(R.id.onefund_btn_addtran);
        btnAddTrans.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent_addTrans = new Intent(getApplicationContext(), AddTransActivity.class);

                intent_addTrans.putExtra(EXTRA_FUNDSYMBOL, fundSymbol);
                intent_addTrans.putExtra(EXTRA_FUNDNAME,fundName);


                startActivity(intent_addTrans);
            }
        });


        /**  Onefund Transaction History Section **/
        // Saved Fund Listveiw Adapter
        onefundTransList_arrayList = dbManager.fetchAllTrans();
        onefundTransList_adapter = new OnefundTransListAdapter(this, onefundTransList_arrayList);

        onefundTransList_lv = (ListView) findViewById(R.id.onefund_lv_translist);
        onefundTransList_lv.setEmptyView(findViewById(R.id.main_tv_empty));
        onefundTransList_lv.setAdapter(onefundTransList_adapter);
    }




}

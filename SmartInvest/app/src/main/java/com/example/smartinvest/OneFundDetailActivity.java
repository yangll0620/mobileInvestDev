package com.example.smartinvest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OneFundDetailActivity extends AppCompatActivity {


    public static final String EXTRA_FUNDSYMBOL = "fundSymbol";
    public static final String EXTRA_FUNDNAME = "fundName";


    String fundSymbol, fundName;


    /** SQLiteDatabase Variables **/
    DBManager dbManager;
    int shares;
    float cost;
    float currPrice;


    /**Onefund  Basic Information Variables **/
    TextView shares_tv, cost_tv;
    TextView totalReturn_tv, annualReturn_tv, peakAR_tv;


    /** Onefund Transaction History List Related Variables **/
    ListView onefundTransList_lv;
    ArrayList<Transaction> onefundTransList_arrayList;
    OnefundTransListAdapter onefundTransList_adapter;
    ViewGroup onefundTransListheader_vg;
    private static final int ADDTRANs_REQUEST_CODE = 0;
    public static final String DATEFORMAT = "MM/dd/YYYY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_fund_detail);

        Intent intent_main = getIntent();
        fundSymbol = intent_main.getStringExtra(EXTRA_FUNDSYMBOL);
        fundName = intent_main.getStringExtra(EXTRA_FUNDNAME);

        setTitle(fundName);


        currPrice = (float)204.36;

        /** SQLite databse**/
        dbManager = new DBManager(this);
        dbManager.open();
        shares = dbManager.getShares(fundSymbol);
        cost = dbManager.getAvgCost(fundSymbol);



        /**Onefund  Basic Information **/
        shares_tv = findViewById(R.id.onefund_tv_shares);
        cost_tv = findViewById(R.id.onefund_tv_avgcost);
        annualReturn_tv = findViewById(R.id.onefund_tv_annualreturn);

        shares_tv.setText(String.valueOf(shares));
        cost_tv.setText(String.valueOf(cost));



        /**  Onefund Transaction History Section **/
        // Saved Fund Listveiw Adapter
        onefundTransList_arrayList = dbManager.fetchTrans(fundSymbol);
        onefundTransList_adapter = new OnefundTransListAdapter(this, onefundTransList_arrayList);

        onefundTransList_lv = (ListView) findViewById(R.id.onefund_lv_translist);
        onefundTransList_lv.setEmptyView(findViewById(R.id.main_tv_empty));
        onefundTransList_lv.setAdapter(onefundTransList_adapter);


        /* Test */
        List <DateAmount> dateValueList = new ArrayList<DateAmount>();
        for(Transaction trans: onefundTransList_arrayList){
            dateValueList.add(new DateAmount(trans));
        }

        // add Today's amount
        dateValueList.add(new DateAmount(new Date(), -currPrice * shares));
        double XIRR = DateAmount.calcXIRR(dateValueList,0.01,0.0001,0.001,100000);

        annualReturn_tv.setText(String.valueOf(XIRR * 100) + "%");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.onefund, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_addtrans) {
            // add Transaction menu
            Intent intent_addTrans = new Intent(getApplicationContext(), AddTransActivity.class);

            intent_addTrans.putExtra(EXTRA_FUNDSYMBOL, fundSymbol);
            intent_addTrans.putExtra(EXTRA_FUNDNAME,fundName);

            startActivityForResult(intent_addTrans, ADDTRANs_REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {/* for returning the result */

        if(requestCode == ADDTRANs_REQUEST_CODE)
        {
            boolean addedTrans = data.getBooleanExtra(AddTransActivity.RESULT_ADDEDTRANS,false);
            if(addedTrans)
            {
                // update shares, avgcost
                shares = dbManager.getShares(fundSymbol);
                shares_tv.setText(String.valueOf(shares));
                cost = dbManager.getAvgCost(fundSymbol);
                cost_tv.setText(String.valueOf(cost));



                /*update trans list*/
                onefundTransList_arrayList.clear();
                onefundTransList_arrayList.addAll(dbManager.fetchTrans(fundSymbol));
                onefundTransList_adapter.notifyDataSetChanged();
                onefundTransList_lv.invalidateViews();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

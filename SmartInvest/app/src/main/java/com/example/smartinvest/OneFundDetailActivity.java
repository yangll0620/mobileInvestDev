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

public class OneFundDetailActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String EXTRA_FUNDSYMBOL = "fundSymbol";
    public static final String EXTRA_FUNDNAME = "fundName";

    public static final String EXTRA_TRANSFUNDNAME  = "transFundName";
    public static final String EXTRA_TRANSFUNDSYMBOL  = "transFundSymbol";
    public static final String EXTRA_TRANSPRICE  = "transPrice";
    public static final String EXTRA_TRANSSHARES  = "transShares";
    public static final String EXTRA_TRANSAMOUNT  = "transAmount";


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
    private static final int UPDATETRANs_REQUEST_CODE = 1;
    public static final String DATEFORMAT = "MM/dd/YYYY";
    public int selectedPos;
    public Button btnDelrecord;
    public Button btnUpdated;

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
        onefundTransList_lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPos = position;
            }
        });


        /* Test */
        List <DateAmount> dateValueList = new ArrayList<DateAmount>();
        for(Transaction trans: onefundTransList_arrayList){
            dateValueList.add(new DateAmount(trans));
        }

        // add Today's amount
        dateValueList.add(new DateAmount(new Date(), -currPrice * shares));
        double XIRR = DateAmount.calcXIRR(dateValueList,0.01,0.0001,0.001,100000);

        annualReturn_tv.setText(String.valueOf(XIRR * 100) + "%");

        // delete a selected transaction
        btnDelrecord = (Button) findViewById(R.id.onefund_btn_deltrans);
        btnDelrecord.setOnClickListener(this);

        // update trans button
        btnUpdated = (Button) findViewById(R.id.onefund_btn_updatetrans);
        btnUpdated.setOnClickListener(this);

    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.onefund_btn_deltrans:

                Transaction selectedTrans = (Transaction) onefundTransList_lv.getItemAtPosition(selectedPos);
                long transId = selectedTrans.getTransId();

                // delete the selected Trans through its transId
                dbManager.deleteTrans(transId);

                // update the transaction list view
                onefundTransList_arrayList.remove(selectedPos);
                onefundTransList_adapter.notifyDataSetChanged();

                onefundTransList_lv.invalidateViews();

                break;

            case R.id.onefund_btn_updatetrans:

                Transaction selectedTrans1 = (Transaction) onefundTransList_lv.getItemAtPosition(selectedPos);

                //start the update trans activity
                Intent intent_updateTrans = new Intent(getApplicationContext(), UpdateTransActivity.class);
                intent_updateTrans.putExtra(EXTRA_TRANSPRICE, selectedTrans1.getTransPrice() );
                intent_updateTrans.putExtra(EXTRA_TRANSFUNDNAME, selectedTrans1.getTransFundName() );
                intent_updateTrans.putExtra(EXTRA_TRANSFUNDSYMBOL, selectedTrans1.getTransFundSymbol() );
                intent_updateTrans.putExtra(EXTRA_TRANSSHARES, selectedTrans1.getTransShares() );
                intent_updateTrans.putExtra(EXTRA_TRANSAMOUNT, selectedTrans1.getTransAmount() );
                startActivityForResult(intent_updateTrans, UPDATETRANs_REQUEST_CODE);
                break;

        }

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

        switch (requestCode){
            case ADDTRANs_REQUEST_CODE:
                boolean addedTrans = data.getBooleanExtra(AddTransActivity.RESULT_ADDEDTRANS,false);
                if(addedTrans) {
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
                break;


            case UPDATETRANs_REQUEST_CODE:
                boolean updatedTrans = data.getBooleanExtra(UpdateTransActivity.RESULT_UPDATEDTRANS, false);
                if (updatedTrans){
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
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}

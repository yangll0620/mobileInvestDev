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

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
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
    public static final String EXTRA_TRANSID  = "transId";
    public static final String EXTRA_TRANSDATE = "transdate";


    public static final String FLOATFORMAT = "%.2f";

    public static final String[] tranTypes = new String[]{"Buy", "Sell"};


    String fundSymbol, fundName;



    /** SQLiteDatabase Variables **/
    DBManager dbManager;


    /**Onefund  Basic Information Variables **/
    TextView shares_tv, cost_tv, currprice_tv;
    TextView totalReturn_tv, annualReturn_tv, peakAR_tv;


    /** Onefund Transaction History List Related Variables **/
    ListView onefundTransList_lv;
    ArrayList<Transaction> onefundTransList_arrayList;
    OnefundTransListAdapter onefundTransList_adapter;
    private static final int ADDTRANs_REQUEST_CODE = 0;
    private static final int UPDATETRANs_REQUEST_CODE = 1;
    public static final String DATEFORMAT = "MM/dd/YYYY";
    public int selectedPos;
    public Button btnDelrecord;
    public Button btnUpdated;



    /** Variables for Update Prices Runnable **/
    int updateSec = 60;
    boolean updateTag;

    String serialInterval = AlphaVantageUse.inter1min;
    Fund fundChecked;
    FetchPriceRunnable fetchPriceRunnable;
    public Button btnTest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_fund_detail);

        Intent intent_main = getIntent();
        fundSymbol = intent_main.getStringExtra(EXTRA_FUNDSYMBOL);
        fundName = intent_main.getStringExtra(EXTRA_FUNDNAME);

        setTitle(fundName);


        /** Find View by Id**/
        shares_tv = findViewById(R.id.onefund_tv_shares);
        cost_tv = findViewById(R.id.onefund_tv_avgcost);
        currprice_tv = findViewById(R.id.onefund_tv_currprice);
        annualReturn_tv = findViewById(R.id.onefund_tv_annualreturn);
        totalReturn_tv = findViewById(R.id.onefund_tv_totalreturn);
        onefundTransList_lv = (ListView) findViewById(R.id.onefund_lv_translist);



        /** SQLite databse**/
        dbManager = new DBManager(this);
        dbManager.open();



        /**  Onefund Transaction History Section **/
        // Saved Fund Listveiw Adapter
        onefundTransList_arrayList = new ArrayList<Transaction>();
        onefundTransList_adapter = new OnefundTransListAdapter(this, onefundTransList_arrayList);
        onefundTransList_lv.setEmptyView(findViewById(R.id.main_tv_empty));
        onefundTransList_lv.setAdapter(onefundTransList_adapter);
        onefundTransList_lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPos = position;
            }
        });


        /** Delete and Update trans Buttons**/
        // delete a selected transaction
        btnDelrecord = (Button) findViewById(R.id.onefund_btn_deltrans);
        btnDelrecord.setOnClickListener(this);

        // update trans button
        btnUpdated = (Button) findViewById(R.id.onefund_btn_updatetrans);
        btnUpdated.setOnClickListener(this);



        /** Update Price Continually **/
        fundChecked = new Fund(fundSymbol);
        fetchPriceRunnable = new FetchPriceRunnable();
        new Thread(fetchPriceRunnable).start();


        /** Update Fund Details those are not related to price**/
        updateDetailsNotRelatedPrice();



        /** btnTest */
        btnTest   = (Button) findViewById(R.id.onefund_btn_test);
        btnTest.setOnClickListener(this);

    }


    public void updateDetailsNotRelatedPrice(){
        onefundTransList_arrayList.clear();
        onefundTransList_arrayList.addAll(dbManager.fetchTrans(fundSymbol));



        //Set Onefund  Basic Information
        if(!onefundTransList_arrayList.isEmpty())
        {// at least one transaction exists


            // get cost, shares
            float cost = dbManager.getAvgCost(fundSymbol);
            int shares = dbManager.getShares(fundSymbol);



            // set all basic details
            cost_tv.setText(String.format(FLOATFORMAT, cost));
            shares_tv.setText(String.valueOf(shares));
        }




        //update trans list
        onefundTransList_adapter.notifyDataSetChanged();
        onefundTransList_lv.invalidateViews();


    }

    public float calcTotalReturn( ArrayList<Transaction>  transList, float currPrice){

        float cost= 0, value = 0;
        int sharesHeld = 0;

        for(Transaction trans : transList){
            int shares = trans.getTransShares();
            float amount = trans.getTransAmount();

            if (amount > 0)
            {// Buy cost
                cost += amount;
            }
            else
            {// Sell vaule
                value += -amount;
            }

            sharesHeld += shares;
        }

        // add the value of held shares
        value += currPrice * sharesHeld;
        float totalReturn = (value - cost) / cost;

        return totalReturn;
    }


    @Override
    public void onClick(View view){

        // Get the selected trans and its id in database
        Transaction selectedTrans = (Transaction) onefundTransList_lv.getItemAtPosition(selectedPos);
        long transId = selectedTrans.getTransId();

        switch (view.getId()){
            case R.id.onefund_btn_test:
                break;


            case R.id.onefund_btn_deltrans:


                // delete the selected Trans through its transId
                dbManager.deleteTrans(transId);

                // update the transaction list view
                onefundTransList_arrayList.remove(selectedPos);
                onefundTransList_adapter.notifyDataSetChanged();

                onefundTransList_lv.invalidateViews();

                break;

            case R.id.onefund_btn_updatetrans:

                // Get trans details
                String transFundSymbol = selectedTrans.getTransFundSymbol();
                String transFundName = selectedTrans.getTransFundName();
                Date transDate = selectedTrans.getTransDate();
                float transPrice = selectedTrans.getTransPrice();
                int transShares = selectedTrans.getTransShares();
                float transAmount = selectedTrans.getTransAmount();


                // Convert transDate into String for putExtra
                DateFormat df = new SimpleDateFormat(DBManager.DATEFORMATINDB);
                String transDateStr = df.format(transDate);


                // New an update trans intent
                Intent intent_updateTrans = new Intent(getApplicationContext(), UpdateTransActivity.class);

                // Put trans details using putExtra
                intent_updateTrans.putExtra(EXTRA_TRANSID, transId);
                intent_updateTrans.putExtra(EXTRA_TRANSFUNDSYMBOL, transFundSymbol);
                intent_updateTrans.putExtra(EXTRA_TRANSFUNDNAME, transFundName);
                intent_updateTrans.putExtra(EXTRA_TRANSDATE, transDateStr);
                intent_updateTrans.putExtra(EXTRA_TRANSPRICE,  transPrice);
                intent_updateTrans.putExtra(EXTRA_TRANSSHARES, transShares);
                intent_updateTrans.putExtra(EXTRA_TRANSAMOUNT, transAmount);


                //start the update trans activity
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
                    updateDetailsNotRelatedPrice();
                }
                break;


            case UPDATETRANs_REQUEST_CODE:
                boolean updatedTrans = data.getBooleanExtra(UpdateTransActivity.RESULT_UPDATEDTRANS, false);
                if (updatedTrans){
                    updateDetailsNotRelatedPrice();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    /** Methods for updating prices and corresponding returns */

    public void updateDetailsAlongPrice(final float currPrice){
        // ... update the annual return rate, total return along time


        onefundTransList_arrayList.clear();
        onefundTransList_arrayList.addAll(dbManager.fetchTrans(fundSymbol));

        if(!onefundTransList_arrayList.isEmpty()) {
            // at least one transaction exists

            /** Total Return */
            final float totalReturn = calcTotalReturn( onefundTransList_arrayList,  currPrice);


            /** Get annual return Rate */
            // Generate dataAmount List of transaction, data amount is a pair of date and amount
            List <DateAmount> dateAmountList = new ArrayList<DateAmount>();
            for(Transaction trans: onefundTransList_arrayList){
                dateAmountList.add(new DateAmount(trans));
            }
            // get current shares
            int shares = dbManager.getShares(fundSymbol);
            // add Today's amount
            dateAmountList.add(new DateAmount(new Date(), -currPrice * shares));
            final double XIRR = DateAmount.calcXIRR(dateAmountList,0.01,0.01,0.001,100000);



            // update the values of annualReturn_tv and totalReturn_tv
            annualReturn_tv.post(new Runnable() {
                @Override
                public void run() {
                    annualReturn_tv.setText(String.format(FLOATFORMAT, XIRR * 100) + "%");
                }
            });
            totalReturn_tv.post(new Runnable() {
                @Override
                public void run() {
                    totalReturn_tv.setText(String.format(FLOATFORMAT, totalReturn * 100) +"%");
                }
            });

        }


        // update current price to currprice_tv
        currprice_tv.post(new Runnable() {
            @Override
            public void run() {
                currprice_tv.setText(String.format(FLOATFORMAT, currPrice));
            }
        });

    }



    class FetchPriceRunnable implements Runnable{
        @Override
        public void run(){

            AlphaVantageUse alphaVUse = new AlphaVantageUse();;
            String timeSeriesAlphaVQuery = AlphaVantageUse.alphaVURL + "query?function=TIME_SERIES_INTRADAY&symbol=" + fundChecked.getFundSymbol()
                    + "&interval=" + serialInterval + "&apikey=" + AlphaVantageUse.apiKey;


            updateTag = true;
            float currPrice;
            while(updateTag)
            {/* update Stock price every updateIntervalSec */

                try{

                    // get current price
                    JSONObject retrievedJSON = alphaVUse.RetrieveOnlineJSON(timeSeriesAlphaVQuery);
                    currPrice = alphaVUse.ParsePriceTimeSeriesJSON(retrievedJSON, serialInterval);

                }catch(Exception e)
                {
                    e.printStackTrace();
                    currPrice = AlphaVantageUse.faultPrice;
                }


                updateDetailsAlongPrice(currPrice);


                // Sleep updateSec seconds
                try{
                    Thread.sleep(updateSec * 1000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }

            }
        }
    }



}

package com.example.smartinvest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {


    Button btn_test;
    TextView tv_text;

    /** SQL Database Related Variables **/
    DBManager dbManager;


    /** Save Fund List Related Variables **/
    ListView savedFundList_lv;
    ArrayList<Fund> savedFundList_arrayList;
    SavedFundListAdapter savedFundList_adapter;



    /** Search Fund Related Variables **/
    private static final int SEARCHFUND_REQUEST_CODE = 0;

    // Customized searchfund adapter usage, arraylist and listView
    SearchedFundListAdapter searchedfunds_adapter;
    ArrayList<Fund> searchedfunds_arrayList;
    ListView searchedfunds_listview;

    // Search View
    SearchView searchedfunds_searchview;

    // selected Fund in Searched Fund Listview
    Fund searchedfunds_selectedFund;



    /** Update price **/
    private static final String interval = "1min";


    /******************** Start Methods ********************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /** SQLite databse**/
        dbManager = new DBManager(this);
        dbManager.open();


        /** Saved Fund Listview**/
        // Saved Fund Listveiw Adapter
        savedFundList_arrayList = dbManager.fetchAllSavedFunds();
        savedFundList_adapter = new SavedFundListAdapter(this, savedFundList_arrayList);

        savedFundList_lv = (ListView) findViewById(R.id.main_lv_savedfundlist);
        savedFundList_lv.setEmptyView(findViewById(R.id.main_tv_empty));
        savedFundList_lv.setAdapter(savedFundList_adapter);
        savedFundList_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Intent intent_funddetail = new Intent(getApplicationContext(), OneFundDetailActivity.class);

                Fund fund = (Fund) parent.getItemAtPosition(position);
                intent_funddetail.putExtra(OneFundDetailActivity.EXTRA_FUNDSYMBOL, fund.getFundSymbol());
                intent_funddetail.putExtra(OneFundDetailActivity.EXTRA_FUNDNAME, fund.getFundName());

                startActivity(intent_funddetail);
            }
        });



        /** Search Fund Section **/
        searchedfunds_searchview = (SearchView) findViewById(R.id.main_sv_searchfund);
        searchedfunds_searchview.setOnQueryTextListener(this);

        // Arraylist, Listview and Adapter Setup
        searchedfunds_arrayList = new ArrayList<Fund>();
        searchedfunds_adapter = new SearchedFundListAdapter(this, searchedfunds_arrayList);
        searchedfunds_listview = (ListView) findViewById(R.id.main_lv_searchedfundlist);
        searchedfunds_listview.setAdapter(searchedfunds_adapter);
        searchedfunds_listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchedfunds_selectedFund = (Fund) parent.getItemAtPosition(position);
            }
        });



        /** Test Button **/
        btn_test = (Button) findViewById(R.id.main_btn_test);
        tv_text = (TextView) findViewById(R.id.main_text);
        btn_test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                ArrayList<Transaction> transList_arrayList = dbManager.fetchAllTrans();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_addfund) {
            if(!dbManager.checkRecordExist(searchedfunds_selectedFund))
            {
                // Insert searchedfunds_selectedFund into table SavedFund
                dbManager.insert(searchedfunds_selectedFund);
                ArrayList <Fund> fundlist = dbManager.fetchAllSavedFunds();

                // Update the content of savedFundList_arrayList
                savedFundList_arrayList.clear();
                savedFundList_arrayList.addAll(fundlist);

                // Update savedFundList_adapter and savedFundList_lv
                savedFundList_adapter.notifyDataSetChanged();
                savedFundList_lv.invalidateViews();
                savedFundList_lv.refreshDrawableState();

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }


    @Override
    public boolean onQueryTextChange(String newText)
    {
        String text = newText;


        Intent intent = new Intent(getApplicationContext(), SearchFundIntentService.class);

        String searchString = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + text + "&apikey=ICPJTYNNQ4EO66TT";
        intent.putExtra(SearchFundIntentService.EXTRA_SEARCHSTRING, searchString);

        // required
        PendingIntent pendingResult = createPendingResult(SEARCHFUND_REQUEST_CODE, new Intent(), 0);
        intent.putExtra(SearchFundIntentService.RESULT_EXTRA, pendingResult);

        startService(intent);
        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {/*  IntentService for returning the result */

        if(requestCode == SEARCHFUND_REQUEST_CODE)
        {
            switch(resultCode)
            {
                case SearchFundIntentService.RESULT_CODE:
                    showSearchedFunds(data);
                    break;
                case SearchFundIntentService.INVALID_URL_CODE:
                    handleInvalidURL();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showSearchedFunds(Intent intent)
    {
        // Extract the searched_funds
        FundListParcelable searchedFundListParce = intent.getParcelableExtra(SearchFundIntentService.RESULT_EXTRA_SEACHEDFUNDs);


        searchedfunds_arrayList.clear();
        searchedfunds_arrayList.addAll(searchedFundListParce.fundList);

        searchedfunds_adapter.notifyDataSetChanged();
        searchedfunds_listview.invalidateViews();

        searchedfunds_selectedFund = null;
    }


    private void handleInvalidURL(){
    }



}

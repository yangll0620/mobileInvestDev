package com.example.smartinvest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{


    Button btn_test;
    TextView tv_text;

    /** SQL Database Related Variables **/
    DBManager dbManager;
    private SimpleCursorAdapter savedfundCursorAdapter;
    String[] from_savedFundDB = new String[]{DatabaseHelper.FUNDSYMBOL, DatabaseHelper.FUNDNAME};
    int[] to_savedFundItem = new int[] {R.id.savedfunditem_symbol, R.id.savedfunditem_name};


    /** Save Fund List Related Variables **/
    ListView savedFundList_lv;




    /** Search Fund Related Variables **/
    private static final int SEARCHFUND_REQUEST_CODE = 0;
    private static final String ulr_alphavantage = "https://www.alphavantage.co/documentation/";

    // Customized searchfund adapter usage, arraylist and listView
    SearchedFundListAdapter searchedfunds_adapter;
    ArrayList<Fund> searchedfunds_arrayList;
    ListView searchedfunds_listview;

    // Search View
    SearchView searchedfund_searchview;




    /******************** Start Methods ********************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /** SQLite databse**/
        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();
        savedfundCursorAdapter = new SimpleCursorAdapter(this, R.layout.activity_savedfund_listview_item, cursor, from_savedFundDB, to_savedFundItem, 0);
        savedfundCursorAdapter.notifyDataSetChanged();


        /** Saved Fund Listview**/
        savedFundList_lv = (ListView) findViewById(R.id.main_lv_savedfundlist);
        savedFundList_lv.setEmptyView(findViewById(R.id.main_tv_empty));
        savedFundList_lv.setAdapter(savedfundCursorAdapter);



        /** Search Fund Section **/
        searchedfund_searchview = (SearchView) findViewById(R.id.main_sv_searchfund);
        searchedfund_searchview.setOnQueryTextListener(this);

        // Arraylist, Listview and Adapter Setup
        searchedfunds_arrayList = new ArrayList<Fund>();
        searchedfunds_adapter = new SearchedFundListAdapter(this, searchedfunds_arrayList);
        searchedfunds_listview = (ListView) findViewById(R.id.main_lv_searchedfundlist);
        searchedfunds_listview.setAdapter(searchedfunds_adapter);
        searchedfunds_listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });



        /** Test Button **/
        btn_test = (Button) findViewById(R.id.main_btn_test);
        tv_text = (TextView) findViewById(R.id.main_text);
        btn_test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                boolean existFund = dbManager.checkRecordExist("BA");
                tv_text.setText(String.valueOf(existFund));
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
            Intent intent_addrecord = new Intent(this, AddFundActivity.class);
            startActivity(intent_addrecord);
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
        // Extract the searched_funds symbol and Name
        String[] searched_fundsSymbol = intent.getStringArrayExtra(SearchFundIntentService.RESULT_EXTRA_FUNDSSYMBOL);
        String[] searched_fundsName = intent.getStringArrayExtra(SearchFundIntentService.RESULT_EXTRA_FUNDSNAME);


        searchedfunds_arrayList.clear();
        for (int i = 0; i< searched_fundsSymbol.length; i++)
        {
            Fund fund = new Fund(searched_fundsSymbol[i], searched_fundsName[i]);
            searchedfunds_arrayList.add(fund);
        }

        searchedfunds_adapter.notifyDataSetChanged();
        searchedfunds_listview.invalidateViews();

    }


    private void handleInvalidURL(){
    }
}

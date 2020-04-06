package com.example.smartinvest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
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
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{


    Button btn_test;
    TextView tv_text;

    /** SQL Database Related Variables **/
    DbHandler dbHandler;



    /** Save Fund Related Variables **/
    // Customized savedfund adapter usage, arraylist and listView
    SavedFundListAdapter savedfunds_adapter;
    ArrayList<Fund> savedfunds_arrayList;
    ListView savedfunds_listview;
    Fund tobeSavedFund = null;




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


        btn_test = (Button) findViewById(R.id.main_btn_test);
        tv_text = (TextView) findViewById(R.id.main_text);
        btn_test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                SQLiteDatabase db = dbHandler.getReadableDatabase();
                tv_text.setText("read Database");
            }
        });

        /** SQLite databse**/
        dbHandler = new DbHandler(this);
        dbHandler.insertFund(new Fund("MSFT", "Microsoft"));


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
                tobeSavedFund = (Fund) parent.getItemAtPosition(position);
            }
        });


        /** Saved Fund Section **/
        // Arraylist, Listview and Adapter Setup
        savedfunds_arrayList = new ArrayList<Fund>();
        savedfunds_adapter = new SavedFundListAdapter(this, savedfunds_arrayList);
        savedfunds_listview = (ListView) findViewById(R.id.main_lv_savedfundlist);
        savedfunds_listview.setAdapter(savedfunds_adapter);
        savedfunds_listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

            if(tobeSavedFund != null)
            {
                savedfunds_arrayList.add(tobeSavedFund);
                savedfunds_adapter.notifyDataSetChanged();
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

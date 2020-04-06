package com.example.intentservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    TextView tv_text;

    private static final int SEARCHFUND_REQUEST_CODE = 0;
    private static final String ulr_alphavantage = "https://www.alphavantage.co/documentation/";


    // Search View
    SearchView sv_searchfund;


    // searched fund listview
    ListView searchedfunds_listview;


    // Customized searchfund adapter usage
    SearchedFundListAdapter searchedfunds_adapter;
    ArrayList<Fund> searchedfunds_arrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // find the views
        tv_text = (TextView) findViewById(R.id.main_tv_text);
        searchedfunds_listview = (ListView) findViewById(R.id.main_lv_searchedfundlist);


        //Customized searchfund adapter setup
        searchedfunds_arrayList = new ArrayList<Fund>();
        searchedfunds_adapter = new SearchedFundListAdapter(this, searchedfunds_arrayList);
        searchedfunds_listview.setAdapter(searchedfunds_adapter);



        /* Buttons */
        Button btn_startIntent = (Button) findViewById(R.id.main_btn_startIntent);
        btn_startIntent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PendingIntent pendingResult = createPendingResult(SEARCHFUND_REQUEST_CODE, new Intent(), 0);
                Intent intent = new Intent(getApplicationContext(), SearchFundIntentService.class);
                intent.putExtra(SearchFundIntentService.EXTRA_SEARCHSTRING, ulr_alphavantage);
                intent.putExtra(SearchFundIntentService.PENDING_RESULT_EXTRA, pendingResult);
                startService(intent);
            }
        });

        Button btn_Test = (Button) findViewById(R.id.main_btn_Test);
        btn_Test.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            }
        });


        /*   Search View     */
        // Locate the EditText in listview_main.xml
        sv_searchfund = (SearchView) findViewById(R.id.main_search);
        sv_searchfund.setOnQueryTextListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == SEARCHFUND_REQUEST_CODE)
        {
            switch(resultCode)
            {
                case SearchFundIntentService.RESULT_CODE:
                    handleSearchedFund(data);
                    break;
                case SearchFundIntentService.INVALID_URL_CODE:
                    handleInvalidURL();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSearchedFund(Intent intent)
    {
        String  searchString = intent.getStringExtra(SearchFundIntentService.RESULT_EXTRA_SEARCHSTRING);
        int urlcode = intent.getIntExtra(SearchFundIntentService.RESULT_EXTRA_URLRESPONSECODE,-1);

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
        tv_text.setText("Invalid URL");
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
        intent.putExtra(SearchFundIntentService.PENDING_RESULT_EXTRA, pendingResult);

        startService(intent);

        return false;
    }
}

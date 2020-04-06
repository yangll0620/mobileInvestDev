package com.example.intentservice;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class SearchFundIntentService extends IntentService {

    private static final String TAG = SearchFundIntentService.class.getSimpleName();


    public static final String PENDING_RESULT_EXTRA = "pending_result";
    public static final String RESULT_EXTRA_SEARCHSTRING = "result_search_string";
    public static final String RESULT_EXTRA_FUNDSSYMBOL= "result_searched_funds_symbol";
    public static final String RESULT_EXTRA_FUNDSNAME= "result_searched_funds_name";
    public static final String RESULT_EXTRA_URLRESPONSECODE = "result_urlresponsecode";
    public static final String EXTRA_SEARCHSTRING = "search_string";


    public static final int RESULT_CODE = 0;
    public static final int INVALID_URL_CODE = 1;
    public static final int ERROR_CODE = 2;


    public SearchFundIntentService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        PendingIntent reply = intent.getParcelableExtra(PENDING_RESULT_EXTRA);
        try{
            try{
                AlphaVantageUse alphaVUse = new AlphaVantageUse();

                String searchString = intent.getStringExtra(EXTRA_SEARCHSTRING);
                JSONObject RetrievedJSON = alphaVUse.RetrieveOnlineJSON(searchString);
                ArrayList<Fund> searchedFundList = alphaVUse.ParseSearchedFundsJSON(RetrievedJSON);


                int n_fund = searchedFundList.size();
                String[] seachedFunds_name = new String[n_fund];
                String[] searchedFunds_symbol = new String[n_fund];
                for (int i =0; i< n_fund; i++)
                {
                    Fund seachedFund = searchedFundList.get(i);
                    seachedFunds_name[i] = seachedFund.getFundName();
                    searchedFunds_symbol[i] = seachedFund.getFundSymbol();
                }


                // pack result_intent
                Intent result_intent = new Intent();
                result_intent.putExtra(RESULT_EXTRA_SEARCHSTRING, searchString);
                result_intent.putExtra(RESULT_EXTRA_FUNDSSYMBOL, searchedFunds_symbol);
                result_intent.putExtra(RESULT_EXTRA_FUNDSNAME, seachedFunds_name);


                // send result_intent
                reply.send(this,RESULT_CODE ,result_intent);
            } catch(Exception e){
                reply.send(ERROR_CODE);
            }
        } catch (PendingIntent.CanceledException e) {
            Log.i(TAG, "reply cancelled", e);
        }

    }
}

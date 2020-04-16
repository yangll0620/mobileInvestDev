package com.example.smartinvest;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFundIntentService extends IntentService {
    private static final String TAG = SearchFundIntentService.class.getSimpleName();


    public static final String RESULT_EXTRA = "pending_result";
    public static final String RESULT_EXTRA_SEACHEDFUNDs= "result_searched_funds";
    public static final String EXTRA_SEARCHSTRING = "search_string";


    public static final int RESULT_CODE = 0;
    public static final int INVALID_URL_CODE = 1;
    public static final int ERROR_CODE = 2;


    public SearchFundIntentService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        PendingIntent reply = intent.getParcelableExtra(RESULT_EXTRA);
        try{
            try{
                AlphaVantageUse alphaVUse = new AlphaVantageUse();

                String searchString = intent.getStringExtra(EXTRA_SEARCHSTRING);
                JSONObject RetrievedJSON = alphaVUse.RetrieveOnlineJSON(searchString);
                ArrayList<Fund> searchedFundList = alphaVUse.ParseSearchedFundsJSON(RetrievedJSON);


                FundListParcelable searchedFundListParce = new FundListParcelable(searchedFundList);

                // pack result_intent
                Intent result_intent = new Intent();
                result_intent.putExtra(RESULT_EXTRA_SEACHEDFUNDs, searchedFundListParce);


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

package com.example.webfetch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String apiKey = "ICPJTYNNQ4EO66TT";
    private static final String prefixURL = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY";
    private static final String symbol = "BABA";
    private static final String interval = "1min";
    private static final int fetchFreq = 60;


    private static final int RSS_FETCHPRICE_REQUEST_CODE = 0;


    private TextView tv_time, tv_price;
    java.util.Date noteTS;

    private volatile boolean stopThread = false;

    float closeprice;
    String string_PriceTime;
    ArrayList<String> searchedFundNameList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tv_time =(TextView) findViewById(R.id.tv_time);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_price.setSingleLine(false);
    }




    public void onClick_BtnStartThread(View view){

        stopThread = false;
        tv_price.setText("Fetching the price of Fund " + symbol);
        FetchPriceRunnable runnable = new FetchPriceRunnable(300);
        new Thread(runnable).start();

    }

    public void onClick_BtnStopThread(View view){
        stopThread = true;
        tv_price.append("\n" + "Stopped Fetching");
    }

    public void onClick_BtnTest(View view) throws InterruptedException {
    }


    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        //searchedfunds_adapter.filter(text);

        return true;
    }



    class FetchPriceRunnable implements Runnable{
        int seconds;

        FetchPriceRunnable(int seconds){
            this.seconds = seconds;
        }


        @Override
        public void run(){

            final String timeFormat = "hh:mm:ss";

            long tStart = System.currentTimeMillis();
            double elapsedSeconds;


            for(int i = 0; i< seconds; i++)
            {
                if (stopThread)
                    return;

                Log.d(TAG, "Start Thread: " + i);


                elapsedSeconds = (System.currentTimeMillis() - tStart) / 1000.0;

                // update TextView tvTime
                noteTS = Calendar.getInstance().getTime();
                tv_time.post(new Runnable() {
                    @Override
                    public void run() {

                        tv_time.setText(DateFormat.format(timeFormat, noteTS));
                    }
                });

                if (i == 1 || elapsedSeconds >= fetchFreq)
                {/* update Stock price every minute */

                    String protocol = prefixURL + "&symbol=" + symbol + "&interval=" + interval + "&apikey=" + apiKey;
                    FetchCurrentPrice fetchCurrentPrice =  new FetchCurrentPrice(protocol, interval);

                    try{
                        fetchCurrentPrice.GetJSON();
                        fetchCurrentPrice.parseJSON();

                        closeprice = fetchCurrentPrice.closeprice;
                        string_PriceTime = fetchCurrentPrice.string_PriceTime;

                        tv_price.post(new Runnable() {
                            @Override
                            public void run() {
                                    tv_price.append("\n" + DateFormat.format(timeFormat, noteTS)  + ", close price : " + String.valueOf(closeprice) + " at " + string_PriceTime);
                            }
                        });
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    tStart = System.currentTimeMillis();
                }


                // Sleep 1 seconds
                try{
                    Thread.sleep(1000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            tv_price.post(new Runnable() {
                @Override
                public void run() {
                    tv_price.append("\n" + "Fininished Fetching Prices");
                }
            });

        }
    }
}
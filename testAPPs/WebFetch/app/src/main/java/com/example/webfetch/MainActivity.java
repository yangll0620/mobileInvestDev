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

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String apiKey = "ICPJTYNNQ4EO66TT";
    private static final String prefixURL = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY";
    private static final String symbol = "MSFT";
    private static final String interval = "1min";
    private static final int fetchFreq = 60;


    private static final int RSS_FETCHPRICE_REQUEST_CODE = 0;


    private TextView tvTime;
    private TextView tvPrice;
    java.util.Date noteTS;

    private volatile boolean stopThread = false;

    float closeprice;
    String string_PriceTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvTime =(TextView) findViewById(R.id.tvTime);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvPrice.setSingleLine(false);
    }




    public void startThread(View view){

        stopThread = false;
        tvPrice.setText("");
        ExampleRunnable runnable = new ExampleRunnable(300);
        new Thread(runnable).start();

    }

    public void stopThread(View view){
        stopThread = true;
        tvTime.setText("Stopped");
    }

    class ExampleRunnable implements Runnable{
        int seconds;

        ExampleRunnable(int seconds){
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
                tvTime.post(new Runnable() {
                    @Override
                    public void run() {

                        tvTime.setText(DateFormat.format(timeFormat, noteTS));
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

                        tvPrice.post(new Runnable() {
                            @Override
                            public void run() {
                                    tvPrice.append("\n" + DateFormat.format(timeFormat, noteTS)  + ", close price : " + String.valueOf(closeprice) + " at " + string_PriceTime);
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
        }
    }
}
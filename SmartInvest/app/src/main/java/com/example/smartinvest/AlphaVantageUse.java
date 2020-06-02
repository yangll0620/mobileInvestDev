package com.example.smartinvest;


import androidx.annotation.StringDef;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class AlphaVantageUse implements Runnable
{

    public static final String apiKey = "ICPJTYNNQ4EO66TT";
    public static final String alphaVURL = "https://www.alphavantage.co/";


    public static final String inter1min = "1min";
    public static final String inter5min = "5min";
    public static final String inter15min = "15min";
    public static final String inter30min = "30min";
    public static final String inter60min = "60min";

    public static final float faultPrice = -1;



    /** Variables for Update Prices Runnable **/
    int updateSec;
    boolean updateTag;

    String serialInterval;
    Fund fundChecked;
    float price;



    public AlphaVantageUse()
    {

    }


    // Annotation replace enums, the the value of the annotated String element SerialInterval should be one of the explicitly named constants.
    @StringDef({inter1min, inter5min, inter15min, inter30min, inter60min})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SerialInterval {}



    public JSONObject RetrieveOnlineJSON(String alphaVRequestString)
    {// Retrieve JSONObject from online using alphaVRequestString

        JSONObject RetrievedJSON;
        try {
                URL url = new URL(alphaVRequestString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                int code = urlConnection.getResponseCode();
                if (code != 200) {
                    urlConnection = null;
                    throw new IOException("Invalid response from server: " + code);
                }


                InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream(), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder responseBuilder = new StringBuilder();


                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                bufferedReader.close();

                String RetrievedString = responseBuilder.toString();
                RetrievedJSON = new JSONObject(RetrievedString);

        }
        catch (JSONException e){
            e.printStackTrace();
            RetrievedJSON = new JSONObject();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            RetrievedJSON = new JSONObject();
        }
        catch (IOException e) {
            e.printStackTrace();
            RetrievedJSON = new JSONObject();
        }
        return RetrievedJSON;
    }


    public float ParsePriceTimeSeriesJSON(JSONObject RetrievedJSON, @SerialInterval String interval)
    {// Parse JSON of retrieved time series using TIME_SERIES_DAILY, TIME_SERIES_INTRADAY et.al

        float price;

        try{
            // get the JSONObject of Time Series, JSON has two names: "Meta Data" and "Time Series (5min)"
            JSONObject jObj_Timeseries = RetrievedJSON.getJSONObject("Time Series" +  " (" + interval + ")");


            // Get the latest time (String)
            String string_PriceTime = jObj_Timeseries.names().getString(0);
            // Get the JSONObject for the latest time point, as "Time Series": {"2019-12-27 16:00:00": {...}}, {"2019-12-27 15:55:00": {...}}
            JSONObject jObj_latestTime  = jObj_Timeseries.getJSONObject(string_PriceTime);


            // Get the close price of the latest time point
            price = Float.valueOf(jObj_latestTime.getString("4. close"));

        } catch(JSONException e)
        {
            e.printStackTrace();
            price = faultPrice;
        }

        return price;
    }


    public  ArrayList<Fund>  ParseSearchedFundsJSON(JSONObject RetrievedJSON)
    {// Parse JSON of retrieved SYMNOL_SEARCH

        ArrayList<Fund> searchedFundList = new ArrayList<Fund>();
        try{
            // get the JSONArray of bestMatches, JSON has one field: "bestMatches"
            JSONArray jObj_Matches = RetrievedJSON.getJSONArray("bestMatches");


            // Parse each Fund
            searchedFundList.clear();
            for(int i =0; i< jObj_Matches.length(); i++ )
            {
                JSONObject fund_jsonobj = new JSONObject(jObj_Matches.getString(i));

                String fund_symbol = fund_jsonobj.getString("1. symbol");
                String fund_name = fund_jsonobj.getString("2. name");

                Fund fund = new Fund(fund_symbol, fund_name);

                searchedFundList.add(fund);
            }


        } catch(JSONException e)
        {
            searchedFundList.clear();
            e.printStackTrace();
        }

        return searchedFundList;
    }



    /** methods for runnable **/
    public void prepareUpdatePrice(Fund fundChecked, int updateSec)
    {
        this.fundChecked = fundChecked;
        this.updateSec = updateSec;
    }

    @Override
    public void run(){

        updateTag = true;
        while(updateTag)
        {/* update Stock price every updateIntervalSec */

            String timeSeriesAlphaVQuery = alphaVURL + "query?function=TIME_SERIES_INTRADAY&symbol=" + fundChecked.getFundSymbol() + "&interval=" + serialInterval + "&apikey=" + apiKey;

            try{
                JSONObject retrievedJSON = RetrieveOnlineJSON(timeSeriesAlphaVQuery);
                price = ParsePriceTimeSeriesJSON(retrievedJSON, serialInterval);
            }catch(Exception e)
            {
                e.printStackTrace();
                price = faultPrice;
            }

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

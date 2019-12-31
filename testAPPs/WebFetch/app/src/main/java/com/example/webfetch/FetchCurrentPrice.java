package com.example.webfetch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FetchCurrentPrice {
    String URL;
    HttpURLConnection urlConnection;
    String JSON;
    String interval;
    float closeprice, openprice;
    String string_PriceTime;

    public FetchCurrentPrice(String URL, String interval)
    {
        this.URL = URL;
        this.interval = interval;
    }

    public String GetJSON() {

        try {
            URL url = new URL(URL);
            urlConnection = (HttpURLConnection) url.openConnection();

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
            JSON = responseBuilder.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            JSON = "";
        } catch (IOException e) {
            e.printStackTrace();
            JSON = "";
        }


        return JSON;
    }



    public void parseJSON()
    {/* parse JSON, change the values of closeprice and openprice */

        try{
            // get the JSONObject of Time Series, JSON has two names: "Meta Data" and "Time Series (5min)"
            JSONObject jObj_Timeseries = new JSONObject(JSON).getJSONObject("Time Series" +  " (" + interval + ")");


            // Get the latest time (String)
            string_PriceTime = jObj_Timeseries.names().getString(0);
            // Get the JSONObject for the latest time point, as "Time Series": {"2019-12-27 16:00:00": {...}}, {"2019-12-27 15:55:00": {...}}
            JSONObject jObj_latestTime  = jObj_Timeseries.getJSONObject(string_PriceTime);


            // Get the close price of the latest time point
            closeprice = Float.valueOf(jObj_latestTime.getString("4. close"));

            // Get the open price of the latest time point
            openprice = Float.valueOf(jObj_latestTime.getString("1. open"));
        } catch(JSONException e)
        {
            e.printStackTrace();
            closeprice = -1;
            openprice = -1;

        }
    }



}

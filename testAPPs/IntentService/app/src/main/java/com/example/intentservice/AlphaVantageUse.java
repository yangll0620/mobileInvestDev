package com.example.intentservice;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class AlphaVantageUse
{

    private static final String apiKey = "ICPJTYNNQ4EO66TT";
    private static final String alphaVURL = "https://www.alphavantage.co/";


    public AlphaVantageUse()
    {

    }

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
}


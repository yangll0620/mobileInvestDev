package com.example.searchinterface;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Toolbar searchToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        //setSupportActionBar(searchToolbar);
    }
}

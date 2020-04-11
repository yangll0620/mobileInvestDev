package com.example.sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CountryListActivity extends AppCompatActivity {

    private DBManager dbManager;

    private ListView lv_countries;


    // SimpleCursorAdapter
    private SimpleCursorAdapter cursorAdapter;
    final String[] from = new  String[]{DatabaseHelper._ID, DatabaseHelper.SUBJECT, DatabaseHelper.DESC};
    final int[] to = new int[]{R.id.countryitem_id, R.id.countryitem_countryname, R.id.countryitem_desc};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();


        cursorAdapter = new SimpleCursorAdapter(this, R.layout.activity_countrylist_item, cursor, from, to, 0);
        cursorAdapter.notifyDataSetChanged();

        lv_countries = (ListView)findViewById(R.id.countrylist_lv_countries);
        lv_countries.setEmptyView(findViewById(R.id.countrylist_tv_empty));
        lv_countries.setAdapter(cursorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id == R.id.main_menu_addrecord)
        {
            Intent intent_addrecord = new Intent(this, AddCountryActivity.class);
            startActivity(intent_addrecord);
        }
        return super.onOptionsItemSelected(item);
    }
}


package com.example.smartinvest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "investdb";

    public static final String TABLE_FUNDs = "fundsList";
    public static final String FUNDs_COL_FUNDNAME = "fundName";
    public static final String FUNDs_COL_ID = "fundID";


    public DbHandler(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_FUNDs + "("
                + FUNDs_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FUNDs_COL_FUNDNAME + " TEXT"  + ")";

        db.execSQL(CREATE_TABLE);


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int  newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FUNDs);
        onCreate(db);
    }


    // *** CRUD (Create, Read, Update, Delete) Operations ***//
    public void insertFund(String fundname){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DbHandler.FUNDs_COL_FUNDNAME, fundname);

        // Get the Data Repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_FUNDs, null, contentValue);
        db.close();

    }
}
package com.example.smartinvest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "investdb";

    public static final String TABLE_SAVEDFUNDs = "savedfundsTable";
    public static final String SAVEDFUND_NAME = "fundName";
    public static final String SAVEDFUND_SYMBOL = "fundSymbol";


    public DbHandler(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_SAVEDFUNDs + "("
                + SAVEDFUND_SYMBOL + " TEXT PRIMARY KEY,"
                + SAVEDFUND_NAME + " TEXT"  + ")";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int  newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVEDFUNDs);
        onCreate(db);
    }


    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }

    // *** CRUD (Create, Read, Update, Delete) Operations ***//
    public void insertFund(Fund newFund){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DbHandler.SAVEDFUND_SYMBOL, newFund.getFundSymbol());
        contentValue.put(DbHandler.SAVEDFUND_NAME, newFund.getFundName());

        // Get the Data Repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_SAVEDFUNDs, null, contentValue);
        db.close();
    }
}
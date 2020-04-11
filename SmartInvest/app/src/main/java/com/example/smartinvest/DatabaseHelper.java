package com.example.smartinvest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // database version
    static final int DB_VERSION = 1;

    // database name
    static final String DB_NAME = "investdb.DB";



    // Table Name
    static final String TABLE_NAME = "savedFunds";

    // Table columns
    public static final String _ID = "_id";
    public static final String FUNDSYMBOL = "fundSymbol";
    public static final String FUNDNAME = "fundName";


    // table create query string
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FUNDSYMBOL + " TEXT NOT NULL, " +
            FUNDNAME + " TEXT)";



    /** Methods **/

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

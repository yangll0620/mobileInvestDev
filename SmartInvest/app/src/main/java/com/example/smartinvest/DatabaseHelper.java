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
    static final String TABLENAME_SAVEDFUNDS = "savedFunds";
    static final String TABLENAME_TRANS = "transcations";

    // columns for savedFunds Table
    public static final String SAVEDFUNDS_ID = "_id";
    public static final String SAVEDFUNDS_FUNDSYMBOL = "fundSymbol";
    public static final String SAVEDFUNDS_FUNDNAME = "fundName";


    // columns for transcations Table
    public static final String TRANS_ID = "_id";
    public static final String TRANS_FUNDSYMBOL = "fundSymbol";
    public static final String TRANS_FUNDNAME = "fundName";
    public static final String TRANS_DATE = "date";
    public static final String TRANS_PRICE = "price";
    public static final String TRANS_SHARES = "shares";
    public static final String TRANS_AMOUNT = "amount";



    // table create query string
    private static final String CREATE_TABLE_SAVEDFUNDS = "create table " + TABLENAME_SAVEDFUNDS + "(" +
            SAVEDFUNDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SAVEDFUNDS_FUNDSYMBOL + " TEXT NOT NULL, " +
            SAVEDFUNDS_FUNDNAME + " TEXT)";

    private static final String CREATE_TABLE_TRANS = "create table " + TABLENAME_TRANS + "(" +
            TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TRANS_FUNDSYMBOL + " TEXT NOT NULL, " +  TRANS_FUNDNAME + " TEXT, " + TRANS_DATE + " TEXT NOT NULL, " +
            TRANS_PRICE + " REAL NOT NULL, " + TRANS_SHARES + " INTEGER NOT NULL, " + TRANS_AMOUNT + " REAL NOT NULL)";


    /** Methods **/

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL(CREATE_TABLE_SAVEDFUNDS);
        db.execSQL(CREATE_TABLE_TRANS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_SAVEDFUNDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_TRANS);
        onCreate(db);
    }
}

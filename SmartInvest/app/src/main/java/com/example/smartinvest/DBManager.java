package com.example.smartinvest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private Context context;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase database;




    /** Methods **/
    public DBManager(Context c){
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }




    public void insert(String fundSymbol, String fundName){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.FUNDSYMBOL, fundSymbol);
        contentValues.put(DatabaseHelper.FUNDNAME, fundName);

        database.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch(){
        String[] columns  = new String[] {DatabaseHelper._ID, DatabaseHelper.FUNDSYMBOL, DatabaseHelper.FUNDNAME};

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);

        return cursor;
    }

    public int update(long _id, String fundSymbol, String fundName){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.FUNDSYMBOL, fundSymbol);
        contentValues.put(DatabaseHelper.FUNDNAME, fundName);

        int n_affectedrows= database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + "=" +_id, null);

        return n_affectedrows;
    }


    public boolean checkRecordExist(String fundSymbol)
    {
        String[] columns = { DatabaseHelper.FUNDSYMBOL };
        String selection = DatabaseHelper.FUNDSYMBOL + " =?";
        String[] selectionArgs = { fundSymbol };
        String limit = "1";

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);


        cursor.close();
        return exists;
    }


    public void delete(long _id){
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }


}

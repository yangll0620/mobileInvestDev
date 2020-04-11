package com.example.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private Context context;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase database;

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



    
    public void insert(String name, String desc){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SUBJECT, name);
        contentValues.put(DatabaseHelper.DESC, desc);

        database.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch(){
        String[] columns  = new String[] {DatabaseHelper._ID, DatabaseHelper.SUBJECT, DatabaseHelper.DESC};

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);

        return cursor;
    }

    public int update(long _id, String name, String desc){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SUBJECT, name);
        contentValues.put(DatabaseHelper.DESC, desc);

        int n_affectedrows= database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + "=" +_id, null);

        return n_affectedrows;
    }



    public void delete(long _id){
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }



}

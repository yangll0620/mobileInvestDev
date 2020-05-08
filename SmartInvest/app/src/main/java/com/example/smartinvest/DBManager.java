package com.example.smartinvest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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




    public void insert(Fund fund){
        /* insert a new fund into the table of savedfunds*/

        String fundSymbol = fund.getFundSymbol();
        String fundName = fund.getFundName();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SAVEDFUNDS_FUNDSYMBOL, fundSymbol);
        contentValues.put(DatabaseHelper.SAVEDFUNDS_FUNDNAME, fundName);

        database.insert(DatabaseHelper.TABLENAME_SAVEDFUNDS, null, contentValues);
    }

    public long insert(Transaction trans){
        /* insert a new fund into the table of transcation*/

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TRANS_FUNDSYMBOL, trans.getTransFundSymbol());
        contentValues.put(DatabaseHelper.TRANS_FUNDNAME, trans.getTransFundName());

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        contentValues.put(DatabaseHelper.TRANS_DATE, df.format(trans.getTransDate()));
        contentValues.put(DatabaseHelper.TRANS_PRICE, (float)trans.getTransPrice());
        contentValues.put(DatabaseHelper.TRANS_SHARES, trans.getTransShares());
        contentValues.put(DatabaseHelper.TRANS_AMOUNT, (float)trans.getTransAmount());

        long rowId = database.insert(DatabaseHelper.TABLENAME_TRANS, null, contentValues);
        return rowId;
    }

    public Cursor fetch(){
        String[] columns  = new String[] {DatabaseHelper.SAVEDFUNDS_ID, DatabaseHelper.SAVEDFUNDS_FUNDSYMBOL, DatabaseHelper.SAVEDFUNDS_FUNDNAME};

        Cursor cursor = database.query(DatabaseHelper.TABLENAME_SAVEDFUNDS, columns, null, null, null, null, null);

        return cursor;
    }


    public ArrayList<Fund> fetchAllSavedFunds()
    {
        ArrayList<Fund> array_list = new ArrayList<Fund>();

        Cursor cursor = database.rawQuery("select * from " + DatabaseHelper.TABLENAME_SAVEDFUNDS, null);
        cursor.moveToFirst();
        String[] colNames = cursor.getColumnNames();
        while(cursor.isAfterLast() == false)
        {
            // extract each record to fund
            Fund fund = new Fund();
            for(String colName: colNames)
            {
                int coli = cursor.getColumnIndex(colName);
                switch (colName){
                    case "fundSymbol":
                        fund.setFundSymbol(cursor.getString(coli));
                        break;
                    case "fundName":
                        fund.setFundName(cursor.getString(coli));
                        break;
                }
            }
            if(!fund.getFundSymbol().isEmpty())
            {
                array_list.add(fund);
            }

            cursor.moveToNext();
        }

        cursor.close();
        return array_list;
    }

    public ArrayList<Transaction> fetchAllTrans()
    {
        ArrayList<Transaction> array_list = new ArrayList<Transaction>();

        Cursor cursor = database.rawQuery("select * from " + DatabaseHelper.TABLENAME_TRANS, null);
        cursor.moveToFirst();
        String[] colNames = cursor.getColumnNames();
        while(cursor.isAfterLast() == false)
        {
            // extract each transaction
            Transaction trans = new Transaction();
            for(String colName: colNames)
            {
                int coli = cursor.getColumnIndex(colName);
                switch (colName){
                    case "fundSymbol":
                        trans.setTransFundSymbol(cursor.getString(coli));
                        break;
                    case "fundName":
                        trans.setTransFundName(cursor.getString(coli));
                        break;
                    case "date":
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");
                            trans.setTransDate(format.parse(cursor.getString(coli)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "price":
                        trans.setTransPrice(cursor.getFloat(coli));
                        break;
                    case "shares":
                        trans.setTransShares(cursor.getInt(coli));
                        break;
                    case "amount":
                        trans.setTransAmount(cursor.getFloat(coli));
                        break;
                }
            }
            if(trans.completeTrans())
            {
                array_list.add(trans);
            }

            cursor.moveToNext();
        }

        cursor.close();
        return array_list;
    }

    public ArrayList<Transaction> fetchTrans(String fundSymbol)
    {/* fetch all transactions of fundSymbol */

        ArrayList<Transaction> array_list = new ArrayList<Transaction>();


        String table = DatabaseHelper.TABLENAME_TRANS;
        String whereClause = DatabaseHelper.TRANS_FUNDSYMBOL + " = ?";
        String[] whereArgs = new String[] {fundSymbol};
        Cursor cursor = database.query(table, null, whereClause, whereArgs,null, null, null);


        cursor.moveToFirst();
        String[] colNames = cursor.getColumnNames();
        while(cursor.isAfterLast() == false)
        {
            // extract each transaction
            Transaction trans = new Transaction();
            for(String colName: colNames)
            {
                int coli = cursor.getColumnIndex(colName);
                switch (colName){
                    case DatabaseHelper.TRANS_ID:
                        trans.setTransId(cursor.getLong(coli));
                    case DatabaseHelper.TRANS_FUNDSYMBOL:
                        trans.setTransFundSymbol(cursor.getString(coli));
                        break;
                    case DatabaseHelper.TRANS_FUNDNAME:
                        trans.setTransFundName(cursor.getString(coli));
                        break;
                    case DatabaseHelper.TRANS_DATE:
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            Date transDate = format.parse(cursor.getString(coli));
                            trans.setTransDate(transDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case DatabaseHelper.TRANS_PRICE:
                        trans.setTransPrice(cursor.getFloat(coli));
                        break;
                    case DatabaseHelper.TRANS_SHARES:
                        trans.setTransShares(cursor.getInt(coli));
                        break;
                    case DatabaseHelper.TRANS_AMOUNT:
                        trans.setTransAmount(cursor.getFloat(coli));
                        break;
                }
            }
            if(trans.completeTrans())
            {
                array_list.add(trans);
            }

            cursor.moveToNext();
        }

        cursor.close();

        Collections.sort(array_list);
        return array_list;
    }

    public int getShares(String fundSymbol)
    {

        String sqlStr = "SELECT SUM(" + DatabaseHelper.TRANS_SHARES + ") FROM " +  DatabaseHelper.TABLENAME_TRANS +
                " WHERE " + DatabaseHelper.SAVEDFUNDS_FUNDSYMBOL + " =  '" + fundSymbol + "'";
        Cursor cursor = database.rawQuery(sqlStr, null);

        int shares;
        if(cursor.moveToFirst())
            shares = cursor.getInt(0);
        else
            shares = -1;
        cursor.close();

        return shares;
    }


    public float getAvgCost(String fundSymbol)
    {

        String sqlStr = "SELECT SUM(" + DatabaseHelper.TRANS_SHARES + "), SUM(" + DatabaseHelper.TRANS_AMOUNT +
                ") FROM " +  DatabaseHelper.TABLENAME_TRANS +
                " WHERE " + DatabaseHelper.SAVEDFUNDS_FUNDSYMBOL + " =  '" + fundSymbol + "'";
        Cursor cursor = database.rawQuery(sqlStr, null);


        float cost;
        if(cursor.moveToFirst()) {
            int shares = cursor.getInt(0);
            float amount = cursor.getFloat(1);
            cost = amount / shares;
        }
        else{
            cost = -1;
        }
        cursor.close();

        return cost;
    }



    public int update(long _id, String fundSymbol, String fundName){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SAVEDFUNDS_FUNDSYMBOL, fundSymbol);
        contentValues.put(DatabaseHelper.SAVEDFUNDS_FUNDNAME, fundName);

        int n_affectedrows= database.update(DatabaseHelper.TABLENAME_SAVEDFUNDS, contentValues, DatabaseHelper.SAVEDFUNDS_ID + "=" +_id, null);

        return n_affectedrows;
    }


    public boolean checkRecordExist(Fund fund)
    {
        String fundSymbol = fund.getFundSymbol();

        String[] columns = { DatabaseHelper.SAVEDFUNDS_FUNDSYMBOL };
        String selection = DatabaseHelper.SAVEDFUNDS_FUNDSYMBOL + " =?";
        String[] selectionArgs = { fundSymbol };
        String limit = "1";

        Cursor cursor = database.query(DatabaseHelper.TABLENAME_SAVEDFUNDS, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);


        cursor.close();
        return exists;
    }


    public void delete(long _id){
        database.delete(DatabaseHelper.TABLENAME_SAVEDFUNDS, DatabaseHelper.SAVEDFUNDS_ID + "=" + _id, null);
    }

    public void deleteTrans(long _id){
        database.delete(DatabaseHelper.TABLENAME_TRANS, DatabaseHelper.TRANS_ID + "=" + _id, null);

    }

    public void updateTrans(Transaction trans){

        // update the value the transaction from the DB

        long transFundId = trans.getTransId();
        // initialize a contentValues, and put the contentValues

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.TRANS_FUNDSYMBOL, trans.getTransFundSymbol());
        contentValues.put(DatabaseHelper.TRANS_FUNDNAME, trans.getTransFundName());

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        contentValues.put(DatabaseHelper.TRANS_DATE, df.format(trans.getTransDate()));
        contentValues.put(DatabaseHelper.TRANS_PRICE, (float)trans.getTransPrice());
        contentValues.put(DatabaseHelper.TRANS_SHARES, trans.getTransShares());
        contentValues.put(DatabaseHelper.TRANS_AMOUNT, (float)trans.getTransAmount());


        database.update(DatabaseHelper.TABLENAME_TRANS, contentValues ,DatabaseHelper.TRANS_ID + "=" + transFundId, null);
    }

    public void deleteALL()
    {
        database.execSQL("delete from " + DatabaseHelper.TABLENAME_SAVEDFUNDS);
    }

}

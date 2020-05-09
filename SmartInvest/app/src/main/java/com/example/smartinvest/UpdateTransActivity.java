package com.example.smartinvest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class UpdateTransActivity extends AppCompatActivity implements View.OnClickListener {

    public String transFundName, transFundSymbol;
    public int transShares;
    public float transAmount, transPrice;
    public long transId;
    public Date transDate;

    DatePicker dp_transdate;

    EditText et_price, et_shares, et_amount;
    Button btnUpdateTrans;
    Spinner sp_transstate;

    String fundSymbol, fundName;

    DBManager dbManager;

    public static final String RESULT_UPDATEDTRANS = "updatedTrans";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_trans);

        // get the original transaction
        Intent intent_parent = getIntent();
        transFundName = intent_parent.getStringExtra(OneFundDetailActivity.EXTRA_TRANSFUNDNAME);
        transFundSymbol = intent_parent.getStringExtra(OneFundDetailActivity.EXTRA_TRANSFUNDSYMBOL);
        transPrice = intent_parent.getFloatExtra(OneFundDetailActivity.EXTRA_TRANSPRICE, -1);
        transShares = intent_parent.getIntExtra(OneFundDetailActivity.EXTRA_TRANSSHARES, 0);
        transAmount = intent_parent.getFloatExtra(OneFundDetailActivity.EXTRA_TRANSAMOUNT,-1);
        transId = intent_parent.getLongExtra(OneFundDetailActivity.EXTRA_TRANSID, -1);
        String transDateString = intent_parent.getStringExtra(OneFundDetailActivity.EXTRA_TRANSDATE);
        String DateArray[]= transDateString.split("/");


        try{
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            transDate = format.parse(transDateString);
        }catch (ParseException e){
            Calendar cal = Calendar.getInstance();
            cal.set(dp_transdate.getYear(), dp_transdate.getMonth(), dp_transdate.getDayOfMonth());
            transDate = cal.getTime();
        }

        /** Set the transaction state drop-down list**/
        //get the spinner from the xml.
        sp_transstate= findViewById(R.id.savetrans_spinner_transstate);
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, OneFundDetailActivity.tranTypes);
        //set the spinners adapter to the previously created one.
        sp_transstate.setAdapter(adapter);

        if(transShares>0)
        {// buy
            sp_transstate.setSelection(0);
        }
        else
        {// sell
            sp_transstate.setSelection(1);
        }


        // EditText Views of price, shares, and amount
        et_price = (EditText) findViewById(R.id.savetrans_et_transprice);
        et_shares = (EditText) findViewById(R.id.savetrans_et_transshares);
        et_amount = (EditText) findViewById(R.id.savetrans_et_transamount);


        // Date Picker View
        dp_transdate = (DatePicker) findViewById(R.id.savetrans_dp_transdate);


        // visualize the original transaction as default
        et_price.setText(String.valueOf(transPrice));
        et_shares.setText(String.valueOf(transShares));
        et_amount.setText(String.valueOf(transAmount));

        // need to explain here, the monthOfYear here start from 0, that's why put -1 in the monthofyear arg
        dp_transdate.init( Integer.parseInt(DateArray[2]), Integer.parseInt(DateArray[1])-1, Integer.parseInt(DateArray[0]), null);


        //  setOnClickLister for btnUpdateTrans
        btnUpdateTrans = (Button) findViewById(R.id.savetrans_btn_savetrans);
        btnUpdateTrans.setOnClickListener(this);


        dbManager = new DBManager(this);
        dbManager.open();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.savetrans_btn_savetrans:
                float price, amount;
                int shares;
                Date transDate;


                // Get Transaction Date
                Calendar cal = Calendar.getInstance();
                cal.set(dp_transdate.getYear(), dp_transdate.getMonth(), dp_transdate.getDayOfMonth());
                transDate = cal.getTime();

                // Get Transaction price, shares, and amount
                price = Float.valueOf(et_price.getText().toString());
                shares = Integer.valueOf(et_shares.getText().toString());
                amount = Float.valueOf(et_amount.getText().toString());

                // if it is sell, shares = -shares
                if (sp_transstate.getSelectedItemId() == 1){
                    shares = -shares;
                }


                // update new transaction record
                Transaction trans = new Transaction(transFundSymbol, transFundName, transDate, price, shares, amount,transId);

                dbManager.updateTrans(trans);
                dbManager.close();


                // send update trans list code
                Intent intent = new Intent();
                intent.putExtra(RESULT_UPDATEDTRANS, true);
                setResult(RESULT_OK, intent);

                finish();
                break;
        }
    }

}

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

import java.util.Calendar;
import java.util.Date;

public class AddTransActivity extends AppCompatActivity implements View.OnClickListener{


    DatePicker dp_transdate;
    EditText et_price, et_shares, et_amount;
    Button btnAddTrans;
    Spinner  sp_transstate;

    String fundSymbol, fundName;

    DBManager dbManager;


    public static final String RESULT_ADDEDTRANS = "addFund_result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_trans);


        Intent intent_main = getIntent();
        fundSymbol = intent_main.getStringExtra(OneFundDetailActivity.EXTRA_FUNDSYMBOL);
        fundName = intent_main.getStringExtra(OneFundDetailActivity.EXTRA_FUNDNAME);

        setTitle("Transaction of " + fundSymbol);

        /** Find View by Id **/

        // EditText Views of price, shares, and amount
        et_price = (EditText) findViewById(R.id.savetrans_et_transprice);
        et_shares = (EditText) findViewById(R.id.savetrans_et_transshares);
        et_amount = (EditText) findViewById(R.id.savetrans_et_transamount);

        // spinner view of trans type
        sp_transstate= findViewById(R.id.savetrans_spinner_transstate);

        // Date Picker View
        dp_transdate = (DatePicker) findViewById(R.id.savetrans_dp_transdate);



        /** Set the transaction type drop-down list **/
        //create a list of items for the spinner.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, OneFundDetailActivity.tranTypes);
        //set the spinners adapter to the previously created one.
        sp_transstate.setAdapter(adapter);



        /** set btnAddTrans OnClickListener **/
        btnAddTrans = (Button) findViewById(R.id.savetrans_btn_savetrans);
        btnAddTrans.setOnClickListener(this);


        /** dbManager **/
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
                amount = Float.valueOf(et_amount.getText().toString());
                shares = Integer.valueOf(et_shares.getText().toString());

                // if it is sell, shares = -shares
                if (sp_transstate.getSelectedItemId() == 1){
                    shares = -shares;
                }


                // Insert new transaction record
                Transaction trans = new Transaction(fundSymbol, fundName, transDate, price, shares, amount);
                dbManager.insert(trans);


                // send update trans list code
                Intent intent = new Intent();
                intent.putExtra(RESULT_ADDEDTRANS, true);
                setResult(RESULT_OK, intent);

                finish();
                break;
        }
    }
}

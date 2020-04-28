package com.example.smartinvest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Date;

import static com.example.smartinvest.SearchFundIntentService.RESULT_EXTRA;

public class AddTransActivity extends AppCompatActivity implements View.OnClickListener{

    //CalendarView cv_transdate;
    DatePicker dp_transdate;

    EditText et_price, et_shares, et_amount;
    Button btnAddTrans;

    String fundSymbol, fundName;

    DBManager dbManager;


    public static final String RESULT_ADDEDTRANS = "addFund_result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trans);


        Intent intent_main = getIntent();
        fundSymbol = intent_main.getStringExtra(OneFundDetailActivity.EXTRA_FUNDSYMBOL);
        fundName = intent_main.getStringExtra(OneFundDetailActivity.EXTRA_FUNDNAME);

        setTitle("Transaction of " + fundSymbol);


        /** Set the transaction state drop-down list**/
        //get the spinner from the xml.
        Spinner  sp_transstate= findViewById(R.id.addtrans_spinner_transstate);
        //create a list of items for the spinner.
        String[] items = new String[]{"Buy", "Sell"};
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        sp_transstate.setAdapter(adapter);



        // EditText Views of price, shares, and amount
        et_price = (EditText) findViewById(R.id.addtrans_et_transprice);
        et_shares = (EditText) findViewById(R.id.addtrans_et_transshares);
        et_amount = (EditText) findViewById(R.id.addtrans_et_transamount);

        // Date Picker View
        dp_transdate = (DatePicker) findViewById(R.id.addtrans_dp_transdate);


        btnAddTrans = (Button) findViewById(R.id.addtrans_btn_savetrans);
        btnAddTrans.setOnClickListener(this);


        dbManager = new DBManager(this);
        dbManager.open();

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addtrans_btn_savetrans:
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

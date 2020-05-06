package com.example.smartinvest;


import android.os.Parcel;
import android.os.Parcelable;


import java.util.List;

public class Fund implements Parcelable {
    private String fundSymbol;
    private String fundName;
    private float peakARR; // peak annual return rate
    private float currPrice;
    private List<Transaction> transList;


    public Fund()
    {
        this.fundSymbol = "";
        this.fundName = "";
    }

    public Fund(String fundSymbol)
    {

        this.fundSymbol = fundSymbol;
        this.fundName = "";
    }

    public Fund(String fundSymbol, String fundName)
    {
        this.fundSymbol = fundSymbol;
        this.fundName = fundName;
    }



    public String getFundSymbol()
    {

        return this.fundSymbol;
    }

    public void setFundSymbol(String fundSymbol)
    {
        this.fundSymbol = fundSymbol;
    }


    public String getFundName()
    {
        return this.fundName;
    }

    public void setFundName(String fundName)
    {
        this.fundName = fundName;
    }




    /** Parcelable Methods **/
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(fundSymbol);
        out.writeString(fundName);
    }

    public static final Parcelable.Creator<Fund> CREATOR = new Parcelable.Creator<Fund>() {
        @Override
        public Fund createFromParcel(Parcel in) {
            return new Fund(in);
        }

        public Fund[] newArray(int size) {
            return new Fund[size];
        }
    };

    private Fund(Parcel in) {
        setFundSymbol(in.readString());
        setFundName(in.readString());
    }
}


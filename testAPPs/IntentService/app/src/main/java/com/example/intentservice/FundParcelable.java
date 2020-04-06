package com.example.intentservice;

import android.content.ClipData;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FundParcelable implements Parcelable {


    public static final Parcelable.Creator<FundParcelable> CREATOR = new Parcelable.Creator<FundParcelable>(){
        @Override
        public FundParcelable createFromParcel(Parcel source) {
            return new FundParcelable(source);
        }

        @Override
        public FundParcelable[] newArray(int size) {
            return new FundParcelable[size];
        }
    };


    private List<Fund> funds;


    public FundParcelable(Parcel parcel) {
        funds = new ArrayList<Fund>();
        int len = parcel.readInt();
        for (int i=0; i<len; i++)
            funds.add(new Fund(
                    parcel.readString(),
                    parcel.readString()));
    }


    public FundParcelable(){
        funds = new ArrayList<Fund>();
    }

    // following for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(funds.size());
        for (Fund fund : funds) {
            dest.writeString(fund.getFundSymbol());
            dest.writeString(fund.getFundName());
        }
    }



}

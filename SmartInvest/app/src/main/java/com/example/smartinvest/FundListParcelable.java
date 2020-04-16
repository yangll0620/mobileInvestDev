package com.example.smartinvest;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FundListParcelable implements Parcelable {
    public List<Fund> fundList;


    public FundListParcelable(List<Fund> fundList){
        this.fundList = new ArrayList<Fund>();
        this.fundList.addAll(fundList);
    }


    /** Parcelable Methods **/
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(fundList.size());
        for(Fund fund:fundList){
            out.writeString(fund.getFundSymbol());
            out.writeString(fund.getFundName());
        }
    }

    public static final Parcelable.Creator<FundListParcelable> CREATOR = new Parcelable.Creator<FundListParcelable>() {
        @Override
        public FundListParcelable createFromParcel(Parcel in) {
            return new FundListParcelable(in);
        }

        public FundListParcelable[] newArray(int size) {
            return new FundListParcelable[size];
        }
    };

    private FundListParcelable(Parcel in) {
        fundList = new ArrayList<Fund>();
        int len = in.readInt();
        for (int i=0; i<len; i++){
            fundList.add(new Fund(in.readString(), in.readString()));
        }
    }

    public int len(){
        return fundList.size();
    }
}

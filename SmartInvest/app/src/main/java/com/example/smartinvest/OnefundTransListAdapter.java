package com.example.smartinvest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OnefundTransListAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<Transaction> onefundTransList = null;

    public OnefundTransListAdapter(Context context, List<Transaction> onefundTransList) {
        mContext = context;
        this.onefundTransList = onefundTransList;
        inflater = LayoutInflater.from(mContext);
    }


    private class itemViewHolder {
        TextView tvTransDate;
        TextView tvTransPrice;
        TextView tvTransShares;
        TextView tvTransAmount;
    }

    @Override
    public int getCount() {
        return onefundTransList.size();
    }

    @Override
    public Transaction getItem(int position) {
        return onefundTransList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final itemViewHolder vholder;
        if (view == null) {
            view = inflater.inflate(R.layout.activity_onefund_trans_lv_item, null);

            // Locate the TextViews in listview_item.xml
            vholder = new itemViewHolder();
            vholder.tvTransDate = (TextView) view.findViewById(R.id.onefund_transitem_tv_date);
            vholder.tvTransPrice = (TextView) view.findViewById(R.id.onefund_transitem_tv_price);
            vholder.tvTransShares = (TextView) view.findViewById(R.id.onefund_transitem_tv_shares);
            vholder.tvTransAmount = (TextView) view.findViewById(R.id.onefund_transitem_tv_amount);

            view.setTag(vholder);
        } else {
            vholder = (itemViewHolder) view.getTag();
        }

        // Set the results into TextViews
        SimpleDateFormat format = new SimpleDateFormat(OneFundDetailActivity.DATEFORMAT);
        Date transDate = onefundTransList.get(position).getTransDate();
        vholder.tvTransDate.setText(format.format(transDate));
        vholder.tvTransPrice.setText(String.valueOf(onefundTransList.get(position).getTransPrice()));
        vholder.tvTransShares.setText(String.valueOf(onefundTransList.get(position).getTransShares()));
        vholder.tvTransAmount.setText(String.valueOf(onefundTransList.get(position).getTransAmount()));

        return view;
    }
}

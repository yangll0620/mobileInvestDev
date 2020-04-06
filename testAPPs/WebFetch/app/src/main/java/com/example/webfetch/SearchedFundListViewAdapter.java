package com.example.webfetch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchedFundListViewAdapter extends BaseAdapter {


    Context mContext;
    LayoutInflater inflater;
    private List<String> fundSymbolsList = null;


    public SearchedFundListViewAdapter(Context context, List<String> fundSymbolsList) {
        mContext = context;
        this.fundSymbolsList = fundSymbolsList;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return fundSymbolsList.size();
    }

    @Override
    public String getItem(int position) {
        return fundSymbolsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class ViewHolder {
        TextView name;
    }
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.activity_main, null);

            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.tv_price);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(fundSymbolsList.get(position));
        return view;
    }


}

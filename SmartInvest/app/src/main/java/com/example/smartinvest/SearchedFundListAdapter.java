package com.example.smartinvest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchedFundListAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<Fund> searchedfundsList = null;

    public SearchedFundListAdapter(Context context, List<Fund> searchedfundsList) {
        mContext = context;
        this.searchedfundsList = searchedfundsList;
        inflater = LayoutInflater.from(mContext);
    }


    private class ViewHolder {
        TextView tv_fundsymbol;
        TextView tv_fundname;
    }

    @Override
    public int getCount() {
        return searchedfundsList.size();
    }

    @Override
    public Fund getItem(int position) {
        return searchedfundsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder vholder;
        if (view == null) {
            view = inflater.inflate(R.layout.activity_searchedfund_listview_item, null);

            // Locate the TextViews in listview_item.xml
            vholder = new ViewHolder();
            vholder.tv_fundsymbol = (TextView) view.findViewById(R.id.searchedfund_symbol);
            vholder.tv_fundname = (TextView) view.findViewById(R.id.searchedfund_name);
            view.setTag(vholder);
        } else {
            vholder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        vholder.tv_fundsymbol.setText(searchedfundsList.get(position).getFundSymbol());
        vholder.tv_fundname.setText(searchedfundsList.get(position).getFundName());

        return view;
    }
}

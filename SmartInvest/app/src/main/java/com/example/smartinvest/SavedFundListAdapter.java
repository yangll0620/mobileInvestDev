package com.example.smartinvest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SavedFundListAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<Fund> savedfundsList = null;

    public SavedFundListAdapter(Context context, List<Fund> savedfundsList) {
        mContext = context;
        this.savedfundsList = savedfundsList;
        inflater = LayoutInflater.from(mContext);
    }


    private class ViewHolder {
        TextView tv_savedfund_symbol;
        TextView tv_savedfund_name;
    }

    @Override
    public int getCount() {
        return savedfundsList.size();
    }

    @Override
    public Fund getItem(int position) {
        return savedfundsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder vholder;
        if (view == null) {
            view = inflater.inflate(R.layout.activity_savedfund_listview_item, null);

            // Locate the TextViews in listview_item.xml
            vholder = new ViewHolder();
            vholder.tv_savedfund_symbol = (TextView) view.findViewById(R.id.savedfunditem_symbol);
            vholder.tv_savedfund_name = (TextView) view.findViewById(R.id.savedfunditem_name);
            view.setTag(vholder);
        } else {
            vholder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        vholder.tv_savedfund_symbol.setText(savedfundsList.get(position).getFundSymbol());
        vholder.tv_savedfund_name.setText(savedfundsList.get(position).getFundName());

        return view;
    }
}

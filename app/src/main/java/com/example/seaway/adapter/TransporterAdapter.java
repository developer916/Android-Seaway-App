package com.example.seaway.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.seaway.R;
import com.example.seaway.list.TransporterList;

import java.util.ArrayList;

public class TransporterAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private ArrayList<TransporterList> mTransporters;
    private ArrayList<TransporterList> filterTransporters;
    CustomTransporterFilter filter;

    public TransporterAdapter(Context mContext, ArrayList<TransporterList> mCustomers) {
        this.mContext = mContext;
        this.mTransporters = mCustomers;
        this.filterTransporters = mCustomers;
    }

    @Override
    public int getCount() {
        return mTransporters.size();
    }

    @Override
    public Object getItem(int position) {
        return mTransporters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter(){
        if(filter == null){
            filter = new CustomTransporterFilter();
        }
        return filter;
    }

    class CustomTransporterFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length() >0) {
                constraint = constraint.toString().toUpperCase();

                ArrayList<TransporterList> filters = new ArrayList<TransporterList>();
                for(int i=0; i <filterTransporters.size(); i++) {
                    if (filterTransporters.get(i).getCompanyName().toUpperCase().contains(constraint)) {
                        filters.add(filterTransporters.get(i));
                    }
                }
                results.count = filters.size();
                results.values = filters;
            } else {
                results.count = filterTransporters.size();
                results.values = filterTransporters;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            mTransporters = (ArrayList<TransporterList>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.customer_item, parent, false);
        }
        TextView tvCompanyName = (TextView) convertView.findViewById(R.id.companyName);
        tvCompanyName.setText(mTransporters.get(position).getCompanyName());
        convertView.setTag(mTransporters.get(position).getTransporterID());
        return convertView;
    }
}

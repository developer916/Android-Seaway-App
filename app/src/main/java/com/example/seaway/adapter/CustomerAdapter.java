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
import com.example.seaway.list.CustomersList;

import java.util.ArrayList;

public class CustomerAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private ArrayList<CustomersList> mCustomers;
    private ArrayList<CustomersList> filterCustomers;

    CustomCustomerFilter filter;

    public CustomerAdapter(Context mContext, ArrayList<CustomersList> mCustomers) {
        this.mContext = mContext;
        this.mCustomers = mCustomers;
        this.filterCustomers = mCustomers;
    }

    @Override
    public int getCount() {
        return mCustomers.size();
    }

    @Override
    public Object getItem(int position) {
        return mCustomers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter(){
        if(filter == null){
            filter = new CustomCustomerFilter();
        }
        return filter;
    }

    class CustomCustomerFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length() >0) {
                constraint = constraint.toString().toUpperCase();

                ArrayList<CustomersList> filters = new ArrayList<CustomersList>();
                for(int i=0; i <filterCustomers.size(); i++) {
                    if (filterCustomers.get(i).getCompanyName().toUpperCase().contains(constraint)) {
                        filters.add(filterCustomers.get(i));
                    }
                }
                results.count = filters.size();
                results.values = filters;
            } else {
                results.count = filterCustomers.size();
                results.values = filterCustomers;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            mCustomers = (ArrayList<CustomersList>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.customer_item, parent, false);
        }
        TextView tvCompanyName = (TextView) convertView.findViewById(R.id.companyName);
        tvCompanyName.setText(mCustomers.get(position).getCompanyName());
        convertView.setTag(mCustomers.get(position).getCustomerId());
        return convertView;
    }

}

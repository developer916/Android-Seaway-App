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
import com.example.seaway.list.DropOffLocationList;

import java.util.ArrayList;

public class DropoffLocationAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private ArrayList<DropOffLocationList> mDropoffLocationList;
    private ArrayList<DropOffLocationList> filterDropoffLocationList;

    CustomDropoffLocationFilter filter;

    public DropoffLocationAdapter(Context mContext, ArrayList<DropOffLocationList> mCustomers) {
        this.mContext = mContext;
        this.mDropoffLocationList = mCustomers;
        this.filterDropoffLocationList = mCustomers;
    }

    @Override
    public int getCount() {
        return mDropoffLocationList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDropoffLocationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter(){
        if(filter == null){
            filter = new CustomDropoffLocationFilter();
        }
        return filter;
    }

    class CustomDropoffLocationFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length() >0) {
                constraint = constraint.toString().toUpperCase();

                ArrayList<DropOffLocationList> filters = new ArrayList<DropOffLocationList>();
                for(int i=0; i <filterDropoffLocationList.size(); i++) {
                    if (filterDropoffLocationList.get(i).getLocationName().toUpperCase().contains(constraint)) {
                        filters.add(filterDropoffLocationList.get(i));
                    }
                }
                results.count = filters.size();
                results.values = filters;
            } else {
                results.count = filterDropoffLocationList.size();
                results.values = filterDropoffLocationList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            mDropoffLocationList = (ArrayList<DropOffLocationList>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.customer_item, parent, false);
        }
        TextView tvCompanyName = (TextView) convertView.findViewById(R.id.companyName);
        tvCompanyName.setText(mDropoffLocationList.get(position).getLocationName());
        convertView.setTag(mDropoffLocationList.get(position).getDropOffLocationID());
        return convertView;
    }
}

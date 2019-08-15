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
import com.example.seaway.list.PickupLocationList;

import java.util.ArrayList;

public class PickupLocationAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private ArrayList<PickupLocationList> mPickupLocationList;
    private ArrayList<PickupLocationList> filterPickupLocationList;

    CustomPickupLocationFilter filter;

    public PickupLocationAdapter(Context mContext, ArrayList<PickupLocationList> mCustomers) {
        this.mContext = mContext;
        this.mPickupLocationList = mCustomers;
        this.filterPickupLocationList = mCustomers;
    }

    @Override
    public int getCount() {
        return mPickupLocationList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPickupLocationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter(){
        if(filter == null){
            filter = new CustomPickupLocationFilter();
        }
        return filter;
    }

    class CustomPickupLocationFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length() >0) {
                constraint = constraint.toString().toUpperCase();

                ArrayList<PickupLocationList> filters = new ArrayList<PickupLocationList>();
                for(int i=0; i <filterPickupLocationList.size(); i++) {
                    if (filterPickupLocationList.get(i).getLocationName().toUpperCase().contains(constraint)) {
                        filters.add(filterPickupLocationList.get(i));
                    }
                }
                results.count = filters.size();
                results.values = filters;
            } else {
                results.count = filterPickupLocationList.size();
                results.values = filterPickupLocationList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            mPickupLocationList = (ArrayList<PickupLocationList>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.customer_item, parent, false);
        }
        TextView tvCompanyName = (TextView) convertView.findViewById(R.id.companyName);
        tvCompanyName.setText(mPickupLocationList.get(position).getLocationName());
        convertView.setTag(mPickupLocationList.get(position).getPickUpLocationID());
        return convertView;
    }

}

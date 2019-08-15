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
import com.example.seaway.list.CurrentDispatchList;
import com.example.seaway.list.CustomersList;

import java.util.ArrayList;

public class CurrentDispatchAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private ArrayList<CurrentDispatchList> mCurrentDispatch;
    private ArrayList<CurrentDispatchList> filterCurrentDispatch;
    CustomCurrentDispatchFilter filter;

    public CurrentDispatchAdapter(Context mContext, ArrayList<CurrentDispatchList> mCurrentDispatch) {
        this.mContext = mContext;
        this.mCurrentDispatch = mCurrentDispatch;
        this.filterCurrentDispatch = mCurrentDispatch;
    }

    @Override
    public int getCount() {
        return mCurrentDispatch.size();
    }

    @Override
    public Object getItem(int position) {
        return mCurrentDispatch.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter(){
        if(filter == null){
            filter = new CustomCurrentDispatchFilter();
        }
        return filter;
    }

    class CustomCurrentDispatchFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length() >0) {
                constraint = constraint.toString().toUpperCase();

                ArrayList<CurrentDispatchList> filters = new ArrayList<>();
                for(int i=0; i <filterCurrentDispatch.size(); i++) {
                    if (filterCurrentDispatch.get(i).getCompanyName().toUpperCase().contains(constraint) || filterCurrentDispatch.get(i).getDispatchOrderID().toString().toUpperCase().contains(constraint)) {
                        filters.add(filterCurrentDispatch.get(i));
                    }
                }
                results.count = filters.size();
                results.values = filters;
            } else {
                results.count = filterCurrentDispatch.size();
                results.values = filterCurrentDispatch;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            mCurrentDispatch = (ArrayList<CurrentDispatchList>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.current_dispatch_item, parent, false);
        }
        TextView tvCustomerName = (TextView) convertView.findViewById(R.id.dispatch_customer);
        TextView tvDispatchOrderID = (TextView) convertView.findViewById(R.id.dispatch_order_id);
        tvCustomerName.setText(mCurrentDispatch.get(position).getCompanyName());
        tvDispatchOrderID.setText(String.valueOf(mCurrentDispatch.get(position).getDispatchOrderID()));
        convertView.setTag(mCurrentDispatch.get(position).getDispatchID());
        return convertView;
    }
}

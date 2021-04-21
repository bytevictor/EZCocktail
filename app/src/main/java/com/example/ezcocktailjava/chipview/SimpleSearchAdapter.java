package com.example.ezcocktailjava.chipview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;

public class SimpleSearchAdapter extends ArrayAdapter<String>{

    private ChipAdapter adapter;
    private ArrayList<Object> data = new ArrayList<>();

    public SimpleSearchAdapter(Context context,ChipAdapter adapter){
        super(context,-1);
        this.adapter = adapter;
        this.data = adapter.data;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                if(charSequence == null || charSequence.length() == 0){
                    results.values = adapter.data;
                    results.count = adapter.data.size();
                }else{
                    ArrayList<Object> tmp = new ArrayList<>();
                    for(int i = 0;i < adapter.data.size();i++){
                        if(adapter.data.get(i).toString().toLowerCase().indexOf(charSequence.toString().toLowerCase()) == 0){
                            tmp.add(adapter.data.get(i));
                        }
                    }
                    results.values = tmp;
                    results.count = tmp.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //recoge un array con los elementos pasados por el filtro (menos elementos que antes normalmente)
                data = (ArrayList<Object>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pos = adapter.data.indexOf(data.get(position));
        if(pos != -1) {
            return adapter.createSearchView(getContext(), adapter.isSelected(pos), pos);
        }else {
            return null;
        }
    }
}

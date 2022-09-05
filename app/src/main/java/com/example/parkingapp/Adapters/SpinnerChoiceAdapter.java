package com.example.parkingapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.parkingapp.R;

import java.util.List;
import java.util.zip.Inflater;

public class SpinnerChoiceAdapter extends BaseAdapter {

    Context context;
    List<String> list;

    public SpinnerChoiceAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.spinner_item, parent, false);

        TextView tv_choice = view.findViewById(R.id.tv_choice);
        tv_choice.setText(list.get(position));

        return view;
    }
}

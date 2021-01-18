package com.gradelogics.overstocked;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class analysisAdapter extends ArrayAdapter<stock_sym> {

    ArrayList<stock_sym> items;
    public analysisAdapter(Context context, int resource,ArrayList<stock_sym> objects) {
        super(context, resource);
        items=objects;
    }
    public int getCount(){
        return items.size();
    }

    public stock_sym getItem(int position){
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
       // Log.e("st","getview");
        stock_sym sym = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_analysis_item, parent, false);
        }
        // Lookup view for data population
        NumberFormat format=NumberFormat.getCurrencyInstance(Locale.US);
        TextView symName = (TextView) convertView.findViewById(R.id.sym_text);
        TextView symPrice = (TextView) convertView.findViewById(R.id.sym_price);
        TextView symChanger = (TextView) convertView.findViewById(R.id.sym_change);
        TextView symHigh=(TextView)convertView.findViewById(R.id.txt_high);
        TextView symLow=(TextView)convertView.findViewById(R.id.txt_low) ;

        symName.setText(sym.sym_name);
        Log.e("analysis adp",sym.sym_name);
        symHigh.setText( format.format(sym.high));
        symLow.setText( format.format(sym.low));
        symPrice.setText( format.format(sym.current_price));
        symChanger.setText(format.format(sym.sym_details.change_in_price));

        // Return the completed view to render on screen
        return convertView;
    }
}

package com.gradelogics.overstocked;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class alertAdapter extends ArrayAdapter<stock_sym.alertz> {
    ArrayList<stock_sym.alertz> items;
    List<stock_sym.alertz> items_final;
    SharedPreferences sharedpreferences;

    public alertAdapter(Context context, int resource, List<stock_sym.alertz> objects) {
        super(context, resource, objects);
        items=new ArrayList<stock_sym.alertz>();
        items.addAll(objects);
        items_final=objects;
        // Log.e("adapter create: size",String.valueOf(items_final.size()));
        sharedpreferences = context.getSharedPreferences("stockfolio", Context.MODE_PRIVATE);

        //  Log.e("st","in dapter sym");
    }

    public stock_sym.alertz getItem(int position){

        return items.get(position);
    }

    @Override
    public int getCount()
    {
        return items.size();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        try {
            final stock_sym.alertz alertz = getItem(position);
            // sym.get_details();
            //  Log.e("st","getview");
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.alert_item, parent, false);
            }
            // Lookup view for data population
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
            TextView symName = (TextView) convertView.findViewById(R.id.txtbuydate);
            TextView buyqty = (TextView) convertView.findViewById(R.id.txtbuyqty);
            LinearLayout delme = (LinearLayout) convertView.findViewById(R.id.delme);

            //convertView.setTag(position);

            delme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     alertz.del();
                    //Log.e("red", String.valueOf(position) + " - " + String.valueOf(items.size()));
                    //alert_activity.class.ad
                    items.remove(position);
                    notifyDataSetChanged();
                }
            });

            // TextView buyprice = (TextView) convertView.findViewById(R.id.txtbuyprice);
            // Populate the data into the template view using the data object
            symName.setText(alertz.symbol);
            buyqty.setText(String.valueOf("Alert when price " + alertz.criteria) + " " + format.format(alertz.price));
            // buyprice.setText(String.valueOf(alertz.price));

            // Return the completed view to render on screen
        }catch (Exception ex)
        {
            Log.e("ex",ex.toString());
        }
        finally {
            return convertView;
        }

    }


}

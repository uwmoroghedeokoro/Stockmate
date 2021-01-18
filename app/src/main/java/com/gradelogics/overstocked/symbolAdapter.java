package com.gradelogics.overstocked;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gradelogics.overstocked.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class symbolAdapter extends ArrayAdapter<stock_sym> {

    ArrayList<stock_sym>items;
     List<stock_sym>items_final;
    SharedPreferences sharedpreferences;
    Context cx;

    public symbolAdapter(Context context, int resource, List<stock_sym> objects) {
        super(context, resource, objects);
        items=new ArrayList<stock_sym>();
        items.addAll(objects);
        items_final=objects;
        cx=context;
       // Log.e("adapter create: size",String.valueOf(items_final.size()));
        sharedpreferences = context.getSharedPreferences("stockfolio", Context.MODE_PRIVATE);

        //  Log.e("st","in dapter sym");
    }

    public stock_sym getItem(int position){
        return StockFragment.my_syms.get(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final stock_sym sym = getItem(position);
        sym.get_details();
      //  Log.e("st","getview");
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.symbol_list_item_watch, parent, false);
        }
        // Lookup view for data population
        NumberFormat format=NumberFormat.getCurrencyInstance(Locale.US);
        TextView symName = (TextView) convertView.findViewById(R.id.sym_text);
        TextView symPrice = (TextView) convertView.findViewById(R.id.sym_price);
        TextView symChange = (TextView) convertView.findViewById(R.id.sym_change);
        TextView symShares=(TextView)convertView.findViewById(R.id.txt_shares);
        TextView symChanger=(TextView)convertView.findViewById(R.id.txt_change_in_price) ;
        final ImageView star_watch=(ImageView)convertView.findViewById(R.id.img_star);

        star_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sym.watch==false)
                {
                    Toast.makeText(getContext(),"Added to Watch List",Toast.LENGTH_SHORT).show();
                    sym.update_watcher(1);
                    star_watch.setImageDrawable(getContext().getDrawable(R.drawable.star_select));
                  //  StockFragment.adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(getContext(),"Removed from Watch List",Toast.LENGTH_SHORT).show();
                    sym.update_watcher(0);
                    star_watch.setImageDrawable(getContext().getDrawable(R.drawable.star_empty));
                }



            }
        });

        // Populate the data into the template view using the data object
        if (sym.watch==true)
            star_watch.setImageDrawable(getContext().getDrawable(R.drawable.star_select));
        else
            star_watch.setImageDrawable(getContext().getDrawable(R.drawable.star_empty));
        symName.setText(sym.sym_name);
        symShares.setText(String.valueOf(sym.sym_details.share_count));
        symPrice.setText( format.format(sym.current_price));
        symChanger.setText(format.format(sym.sym_details.change_in_price));

        String dOpt=sharedpreferences.getString("dOpt","perf");
        float displayValue=0;
       // Log.e("in adapter Opt",dOpt);
        if (dOpt.equals("perf")) {
            displayValue=sym.change;
            symChange.setText(String.valueOf(sym.change) + "%");
        } else if (dOpt.equals("eq")){
           // Log.e("in adapter eq=",dOpt);
            displayValue=sym.sym_details.equity;
            symChange.setText(format.format(displayValue));
        }if (dOpt.equals("ret"))
        {
            displayValue=sym.sym_details.total_return;
            symChange.setText(format.format(displayValue));
        }
        if (displayValue<0)
            symChange.setTextColor(Color.parseColor("#C02432"));
        if (displayValue>0)
            symChange.setTextColor(Color.parseColor("#00b300"));

        if (sym.sym_details.change_in_price<0)
            symChanger.setBackground(cx.getDrawable(R.drawable.round_edge_red)) ;//Color.parseColor("#C02432"));
        if (sym.sym_details.change_in_price>0)
           symChanger.setBackground(cx.getDrawable(R.drawable.round_edge_green));//Color.parseColor("#00b300"));

        // Return the completed view to render on screen

        return convertView;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        List<stock_sym> tmpHolder=new ArrayList<stock_sym>();
        tmpHolder.addAll(items);
        StockFragment.my_syms.clear();


        if (charText.length() == 0) {
            StockFragment.my_syms.addAll(items);
          //  Log.e("filter 0",String.valueOf(items_final.size()));
        } else {
           // Log.e("filter >0",String.valueOf(items_final.size()));
            for (stock_sym stck : tmpHolder) {
                if (stck.sym_name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    StockFragment.my_syms.add(stck);
                }
            }
        }
        notifyDataSetChanged();
    }

}

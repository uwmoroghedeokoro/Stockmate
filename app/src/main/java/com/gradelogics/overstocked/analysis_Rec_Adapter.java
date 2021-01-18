package com.gradelogics.overstocked;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class analysis_Rec_Adapter extends RecyclerView.Adapter<analysis_Rec_Adapter.MyViewHolder> {

    private List<stock_sym> stockList;
    Context cx;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView symName, symPrice, gain,gain_perc,high,low;

        public MyViewHolder(View view) {
            super(view);
            symName = (TextView) view.findViewById(R.id.sym_text);
            symPrice = (TextView) view.findViewById(R.id.sym_price);
            gain = (TextView) view.findViewById(R.id.txt_change_in_price);
            //gain_perc = (TextView) view.findViewById(R.id.sym_change);
            high = (TextView) view.findViewById(R.id.txt_high);
            low = (TextView) view.findViewById(R.id.txt_low);


        }
    }

    public analysis_Rec_Adapter(List<stock_sym> stock_list,Context context) {
        this.stockList = stock_list;cx=context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lv_analysis_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        stock_sym stk = stockList.get(position);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        holder.symName.setText(stk.sym_name);
        holder.symPrice.setText(format.format(stk.current_price));
       // holder.gain_perc.setText(String.format("%.2f", stk.change_perc) + "%");
        holder.gain.setText(stk.change_perc >0?"+" + String.format("%.2f", stk.change_perc) + "%":String.format("%.2f", stk.change_perc) + "%");
        holder.high.setText(format.format(stk.high));
        holder.low.setText(format.format(stk.low));

        if (stk.change_perc<0)
            holder.gain.setBackground(cx.getDrawable(R.drawable.round_edge_red)) ;//Color.parseColor("#C02432"));
        else
            holder.gain.setBackground(cx.getDrawable(R.drawable.round_edge_green));//Color.parseColor("#00b300"));

        //  holder.genre.setText(movie.getGenre());
       // holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}

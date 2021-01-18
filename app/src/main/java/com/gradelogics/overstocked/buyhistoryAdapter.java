package com.gradelogics.overstocked;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gradelogics.overstocked.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class buyhistoryAdapter extends RecyclerView.Adapter<buyhistoryAdapter.MyViewHolder>  {
    private List<stock_sym> stockList;
    Context cx;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView bo_shares, bo_date, bo_price,gain_perc,high,low;
        public ImageView btndel,btnedit;

        public MyViewHolder(View view) {
            super(view);
            bo_shares = (TextView) view.findViewById(R.id.buy_shares);
            bo_date = (TextView) view.findViewById(R.id.txt_buy_date);
            bo_price = (TextView) view.findViewById(R.id.buy_price);

            btndel=(ImageView)view.findViewById(R.id.btn_del);
            btnedit=(ImageView)view.findViewById(R.id.btn_edit);
            //gain_perc = (TextView) view.findViewById(R.id.sym_change);
            //high = (TextView) view.findViewById(R.id.txt_high);
            //low = (TextView) view.findViewById(R.id.txt_low);


        }
    }

    public buyhistoryAdapter(List<stock_sym> stock_list,Context context) {
        this.stockList = stock_list;cx=context;
    }
    @Override
    public buyhistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buy_history_item, parent, false);

        return new buyhistoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(buyhistoryAdapter.MyViewHolder holder, final int position) {
        final stock_sym stk = stockList.get(position);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
       // holder.bo_shares.setText(stk.buy_qty);
        holder.bo_date.setText(stk.getFormattedDate());
        holder.bo_shares.setText(String.valueOf(stk.buy_qty) + " shares");
        holder.bo_price.setText(format.format(stk.buy_price));
        Log.e("syname",stk.sym_name);

        holder.btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  if (position>0)
                {
                  //  int selcted_id = my_syms.get(position - 1).id;
                    Intent intent = new Intent(cx, activity_add_buy.class);
                    intent.putExtra("buy_id", stk.id);
                    cx.startActivity(intent);
                }
            }
        });
        //  holder.genre.setText(movie.getGenre());
        // holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}

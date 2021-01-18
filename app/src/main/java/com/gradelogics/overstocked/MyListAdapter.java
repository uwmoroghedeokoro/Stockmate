package com.gradelogics.overstocked;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gradelogics.overstocked.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private List<stock_sym> listdata;
    SharedPreferences sharedpreferences;
    static Context cx;

    // RecyclerView recyclerView;
    public MyListAdapter(List<stock_sym> listdata,Context context) {
        this.listdata = listdata;
        sharedpreferences = context.getSharedPreferences("stockfolio", Context.MODE_PRIVATE);
        cx=context;
        //Log.e("list size",String.valueOf(listdata.size()));
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.symbol_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final stock_sym myListData = listdata.get(position);
        myListData.get_details();
        holder.txtSym.setText(myListData.sym_name);
        float change_price=myListData.sym_details.change_in_price;
//Log.e("cip",String.valueOf(change_price));
            NumberFormat format=NumberFormat.getCurrencyInstance(Locale.US);

            holder.txtShares.setText(String.valueOf(myListData.sym_details.share_count));
            holder.txtPrice.setText(format.format(myListData.current_price));
            holder.txtChange_in_price.setText(format.format(change_price));

     //  holder.txtChange_in_price.setText("("+ format.format(myListData.sym_details.change_in_price)+")");
      //  holder.txtChange.setText( format.format(myListData.change));

        //calculate stock daily change
        float stock_daily_change=change_price*myListData.sym_details.share_count;
        MainFragment.dail_val+=stock_daily_change;

       // Log.e(myListData.sym_name + " change",String.valueOf(stock_daily_change));
        MainFragment.update_daily_change();
        //

        String dOpt=sharedpreferences.getString("dOpt","perf");
        float displayValue=0;
        // Log.e("in adapter Opt",dOpt);
        if (dOpt.equals("perf")) {
            displayValue=myListData.change;
            holder.txtChange.setText(String.valueOf(myListData.change) + "%");
        } else if (dOpt.equals("eq")){
            // Log.e("in adapter eq=",dOpt);
            displayValue=myListData.sym_details.equity;
            holder.txtChange.setText(format.format(displayValue));
        }if (dOpt.equals("ret"))
        {
            displayValue=myListData.sym_details.total_return;
            holder.txtChange.setText(format.format(displayValue));
        }

        if (displayValue<0)
            holder.txtChange.setTextColor(Color.parseColor("#C02432"));
        if (displayValue>0)
            holder.txtChange.setTextColor(Color.parseColor("#00b300"));

        if (change_price<0)
            holder.txtChange_in_price.setBackground(cx.getDrawable(R.drawable.round_edge_red)) ;//Color.parseColor("#C02432"));
        if (change_price>0)
            holder.txtChange_in_price.setBackground(cx.getDrawable(R.drawable.round_edge_green));//Color.parseColor("#00b300"));

      //  holder.imageView.setImageResource(listdata[position].getImgId());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sym_details=new Intent(cx,activity_sym_details.class);
                sym_details.putExtra("symname",myListData.sym_name);
                sym_details.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cx.startActivity(sym_details);
              //  Toast.makeText(view.getContext(),"click on item: "+myListData.getDescription(),Toast.LENGTH_LONG).show();
            }
        });



    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView txtSym,txtShares,txtPrice,txtChange,txtChange_in_price;
        public LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            //this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.txtSym = (TextView) itemView.findViewById(R.id.sym_text);
            this.txtShares = (TextView) itemView.findViewById(R.id.txt_shares);
            this.txtPrice = (TextView) itemView.findViewById(R.id.sym_price);
            this.txtChange = (TextView) itemView.findViewById(R.id.sym_change);
            this.txtChange_in_price = (TextView) itemView.findViewById(R.id.txt_change_in_price);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.sym_layout);

        }
    }
}
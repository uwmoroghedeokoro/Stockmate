package com.gradelogics.overstocked;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.gradelogics.overstocked.R;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class activity_sym_details extends rootAnimator {


    RecyclerView lv;
    String sym_name;
    stock_sym thistock;
    Toolbar toolbar;
    ColumnChartView columnChartView;
    TextView icofilter;
    SharedPreferences sharedpreferences;
    buyhistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sym_details);


        lv=(RecyclerView) findViewById(R.id.list_orders);
//        ViewGroup headerView = (ViewGroup)getLayoutInflater().inflate(R.layout.lv_header, lv,false);
        // Add header view to the ListView
      //  lv.addHeaderView(headerView);

        sharedpreferences = getSharedPreferences("stockfolio", Context.MODE_PRIVATE);

        sym_name=getIntent().getExtras().getString("symname");

        thistock=new stock_sym(getApplicationContext());
        thistock.sym_name=sym_name;
        thistock.get_details();;

        NumberFormat format=NumberFormat.getInstance(Locale.US);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(sym_name);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(Color.parseColor("#46B7F8"));


        icofilter=(TextView)findViewById(R.id.btn_filter);
        icofilter.setPaintFlags(icofilter.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        icofilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(activity_sym_details.this);
                dialog.setContentView(R.layout.chart_display_options);

                RadioButton chkperm=(RadioButton)dialog.findViewById(R.id.chk_perf);
                RadioButton chkequ=(RadioButton)dialog.findViewById(R.id.chk_equ);
                RadioButton chkret=(RadioButton)dialog.findViewById(R.id.chk_ret);

                chkperm.setOnCheckedChangeListener(new activity_sym_details.myCheckBoxChnageClicker("perf"));
                chkequ.setOnCheckedChangeListener(new activity_sym_details.myCheckBoxChnageClicker("eq"));
                chkret.setOnCheckedChangeListener(new activity_sym_details.myCheckBoxChnageClicker("ret"));

                String dOpt=sharedpreferences.getString("chOpt","perf");
                if(dOpt.equals("perf"))
                {
                    chkperm.setChecked(true);
                }else if (dOpt.equals("eq"))
                    chkequ.setChecked(true);
                else if (dOpt.equals("ret"))
                    chkret.setChecked(true);

                dialog.setTitle("Display Options");
                dialog.show();
            }
        });


        load_details();

    }

    @Override
    public void onResume() {

        super.onResume();
         Log.e("StockD","resume deails");
        load_details();
    }

    class myCheckBoxChnageClicker implements CheckBox.OnCheckedChangeListener
    {
        String dOpt="";
        SharedPreferences sharedpreferences;
        SharedPreferences.Editor editor;
        public  myCheckBoxChnageClicker(String opt)
        {
            dOpt=opt;


        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub

            // Toast.makeText(CheckBoxCheckedDemo.this, &quot;Checked =&gt; &quot;+isChecked, Toast.LENGTH_SHORT).show();
            Log.e("f",String.valueOf(isChecked));
            if (isChecked==true)
            {

                final Dialog dialog = new Dialog(activity_sym_details.this);
                dialog.setContentView(R.layout.chart_display_options);

                RadioButton chkperm=(RadioButton)dialog.findViewById(R.id.chk_perf);
                RadioButton chkequ=(RadioButton)dialog.findViewById(R.id.chk_equ);
                RadioButton chkret=(RadioButton)dialog.findViewById(R.id.chk_ret);


                if (dOpt.equals("perf")) {
                    chkperm.setChecked(true);
                    icofilter.setText("Stock Price");
                }
                else if (dOpt.equals("eq")) {
                    chkequ.setChecked(true);
                    icofilter.setText("Total Equity");
                }
                else if (dOpt.equals("ret")) {
                    chkret.setChecked(true);
                    icofilter.setText("Total Return");
                }
                //get Shared Pref
                sharedpreferences = getSharedPreferences("stockfolio", Context.MODE_PRIVATE);
                editor = sharedpreferences.edit();


                editor.putString("chOpt",dOpt);
                editor.commit();
                Log.e("chOpt",dOpt);
             load_details();
                adapter.notifyDataSetChanged();

                //  dialog.dismiss();
            }

        }
    }
    private void load_details()
    {

        DecimalFormat formatter = new DecimalFormat("#,###,###");

        TextView txt_sim_price=(TextView)findViewById(R.id.txt_sim);



        Button sell_button=(Button)findViewById(R.id.btn_sell_shares);
        sell_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialog = new Dialog(activity_sym_details.this);
                dialog.setContentView(R.layout.sell_share_layout);
                dialog.setTitle("Sell Shares");

                Button dialogButton = (Button) dialog.findViewById(R.id.btn_sv);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stock_sym stockm=new stock_sym(getApplicationContext());
                        stockm.sym_name=sym_name;
                        EditText alert_v=(EditText)dialog.findViewById(R.id.share_vol);

                        if (!alert_v.getText().toString().equals("")) {

                            //stock_sym this_order=new stock_sym(getApplicationContext());
                            //stockm.sym_name = sym_spin.getSelectedItem().toString();
                            stockm.buy_qty = Integer.parseInt(alert_v.getText().toString());
                            stockm.buy_fee = 0;
                            stockm.buy_price = 0;
                            stockm.buy_total = 0;

                            Calendar calendar = Calendar.getInstance();
                            //calendar.set(year, month, day);
                            //return calendar.getTimeInMillis();
                            stockm.buy_date = calendar.getTimeInMillis();
                            //  Log.e("sel_date", String.valueOf(this_order.getFormattedDate()));

                            stockm.record_sell(Integer.parseInt(alert_v.getText().toString()));


                          finish();
                          startActivity(getIntent());
                        }
                    }
                });

                dialog.show();

            }
        });

        TextView txtsymname=(TextView)findViewById(R.id.txt_sym_name);
        final TextView txtsymprice=(TextView)findViewById(R.id.txt_sym_price);

        txtsymname.setText(sym_name);
        final NumberFormat format=NumberFormat.getCurrencyInstance(Locale.US);
        txtsymprice.setText(format.format(thistock.sym_details.current_price));

        final ArrayList<stock_sym> my_syms=thistock.buy_history();

         adapter=new buyhistoryAdapter(my_syms,this);
        stock_sym.portfolio portfolio=thistock.portfolio_position();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity_sym_details.this);
        lv.setLayoutManager(mLayoutManager);
        lv.setAdapter(adapter);
        //NumberFormat format = NumberFormat.getCurrencyInstance();

        TextView txtshares=(TextView)findViewById(R.id.txt_shares);
        final TextView txtequity=(TextView)findViewById(R.id.txt_equity);
        final TextView txtavgcost=(TextView)findViewById(R.id.txt_avg_cost);
        final TextView txttotalreturn=(TextView)findViewById(R.id.txt_total_return);
        final TextView txttotalinvest=(TextView)findViewById(R.id.txt_total_invest);
        final TextView txtdiversity=(TextView)findViewById(R.id.txt_diversity);

        txtdiversity.setText(String.valueOf((thistock.sym_details.total_invest/portfolio.total_invest)*100) + "%");
        txtshares.setText(formatter.format(thistock.sym_details.share_count));
        txtequity.setText(format.format(thistock.sym_details.equity));
        txttotalinvest.setText(format.format(thistock.sym_details.total_invest));
        txtavgcost.setText(format.format(thistock.sym_details.avg_cost));



        if((thistock.sym_details.total_return>0))
        {
            txttotalreturn.setTextColor(Color.parseColor("#0FDA49"));
        }
        else
        txttotalreturn.setTextColor(Color.parseColor("#F92051"));

        txttotalreturn.setText(thistock.sym_details.total_return >0 ? "+" + format.format(thistock.sym_details.total_return):format.format(thistock.sym_details.total_return));

       /* lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              if (position>0)
              {
                  int selcted_id = my_syms.get(position - 1).id;
                  Intent intent = new Intent(getApplicationContext(), activity_add_buy.class);
                  intent.putExtra("buy_id", selcted_id);
                  startActivity(intent);
              }
            }
        });*/


        txt_sim_price.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Dialog sim_dialog=new Dialog(activity_sym_details.this);
                sim_dialog.setContentView(R.layout.pop_sim_price);

                Button preview_btn=(Button)sim_dialog.findViewById(R.id.btn_preview);
                preview_btn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v)
                    {
                        EditText edt_sim_price=(EditText)sim_dialog.findViewById(R.id.edt_sim_price);
                        if(!edt_sim_price.equals(""))
                        {
                            thistock.get_details_simulated(Float.valueOf(edt_sim_price.getText().toString()));;
                            txtequity.setText(format.format(thistock.sym_details.equity));
                            txtsymprice.setText(format.format(Float.valueOf(edt_sim_price.getText().toString())));
                            if((thistock.sym_details.total_return>0))
                            {
                                txttotalreturn.setTextColor(Color.parseColor("#0FDA49"));
                            }
                            else
                                txttotalreturn.setTextColor(Color.parseColor("#F92051"));

                            txttotalreturn.setText(thistock.sym_details.total_return >0 ? "+" + format.format(thistock.sym_details.total_return):format.format(thistock.sym_details.total_return));

                            Toast.makeText(getApplicationContext(),"Displaying your simulated Position",Toast.LENGTH_LONG).show();
                            sim_dialog.dismiss();
                        }
                    }
                });
                sim_dialog.show();
            }
        });

//        columnChartView=(ColumnChartView) findViewById(R.id.lineC);

        Date date = new Date();
        String dateInString = "2011-11-30";  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

       // Date startDate = new DateTime.Now.;
        //set date to now-6;

       // startDate
         c.add(Calendar.DATE, -8);

        int xx=0;


        int x=0;

        String[] axisData=new String[5];
        float[] yAxisData = new float[5];

        stock_sym stocki=new stock_sym(getApplicationContext());
        stocki.sym_name=sym_name;

        float y_lg = 0,y_sm=1000000000;

        int h=0;
        String[] dateArr=new String[8];

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String outputDate = simpleDateFormat.format(calendar.getTime());

        Calendar cc=Calendar.getInstance();
        ArrayList<String> valid_days=new ArrayList<>();

        while (xx<5)
        {
            if(cc.get(Calendar.DAY_OF_WEEK)!=1 && cc.get(Calendar.DAY_OF_WEEK)!=7)
            {
                valid_days.add(sdf.format( new Date(cc.getTimeInMillis())));
                xx++;
            }
           cc.add(Calendar.DATE, -1);
           // Log.e("cal",cc.getTime().toString());
        }

        float last_val=0;
       // x=xx;
        xx--;
        while ( x<5) /// change this while loop to lop through arraylisy instead
        {

            c.add(Calendar.DATE, 1);
            sdf=new SimpleDateFormat("yyyy-MM-dd");
            Date resultdate = new Date(c.getTimeInMillis());
            String date_s=sdf.format(resultdate);

            sdf = new SimpleDateFormat("E");
            String strDate = "2013-05-15T10:00:00-0700";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date rdate = null;
            try {
                rdate = dateFormat.parse(valid_days.get(xx));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            dateInString = sdf.format(rdate);
//if(c.get(Calendar.DAY_OF_WEEK) != 6 && c.get(Calendar.DAY_OF_WEEK) != 7)
{
  //  Log.e("String date:",String.valueOf(c.get(Calendar.DAY_OF_WEEK)));
    axisData[x] = (dateInString);
    stocki.get_details_byDate(valid_days.get(xx));

    //let's decide what data to display on chart

    String chOpt = sharedpreferences.getString("chOpt", "perf");

    if (chOpt.equals("perf")) {
        //show stock price
        if (stocki.sym_details.current_price == 0)
            yAxisData[x] = last_val;
        else {
            yAxisData[x] = stocki.sym_details.current_price;
            last_val = stocki.sym_details.current_price;
        }


    } else if (chOpt.equals("eq")) {
        //show total equity
        if (stocki.sym_details.equity == 0)
            yAxisData[x] = last_val;
        else {
            yAxisData[x] = stocki.sym_details.equity;
            last_val = stocki.sym_details.equity;
        }
    } else if (chOpt.equals("ret")) {
        //show return
        if (stocki.sym_details.total_return == 0)
            yAxisData[x] = last_val;
        else {
            yAxisData[x] = stocki.sym_details.total_return;
            last_val = stocki.sym_details.total_return;
        }


    }
}// end if
            //
         //   Log.e("value: ",String.valueOf(stocki.sym_details.equity));

            xx--;x++;

        }//end while

// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        for (float v : yAxisData)
        {
            if(v>y_lg)
                y_lg=v;
            if (v<y_sm)
                y_sm=v;
        }


        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        Column line = new Column(yAxisValues).setHasLabelsOnlyForSelected(true);


        //populate axis data
        ArrayList<String> labels = new ArrayList<String>();
        for(int i = 0; i < axisData.length; i++){
            labels.add(axisData[i]);
        }

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < yAxisData.length; i++){
            entries.add(new BarEntry(yAxisData[i], i));
        }


        LineDataSet dataset = new LineDataSet(entries,"");
       // dataset.setColors(ColorTemplate.VORDIPLOM_COLORS);

        dataset.setColor(Color.parseColor("#3a93f2"));
        dataset.setDrawFilled(true);
        dataset.setDrawCircles(false);
        dataset.setFillColor(Color.parseColor("#ebfbff"));

        LineChart barchart=(LineChart) findViewById(R.id.lineC);
        barchart.clear();
        dataset.setLineWidth(1f);
        // dataset.
        LineData data = new LineData(labels, dataset);

        CustomMarkerView mv = new CustomMarkerView (activity_sym_details.this, R.layout.custom_marker_view);
        barchart.setMarkerView(mv);

        //Axe Y

       // barchart.getAxisLeft().setAxisMaxValue(y_lg);
       // barchart.getAxisLeft().setAxisMinValue(y_sm);

        barchart.getAxisLeft().setDrawTopYLabelEntry(false);
        barchart.getAxisLeft().setEnabled(true);
        barchart.getAxisLeft().setDrawAxisLine(false);
        barchart.getAxisLeft().setDrawGridLines(false);

        barchart.getAxisLeft().setDrawAxisLine(false);
        barchart.getAxisLeft().setDrawGridLines(false);
        barchart.getXAxis().setDrawGridLines(false);

        barchart.getAxisLeft().setAxisLineColor(Color.parseColor("#000000"));
        barchart.getAxisLeft().setTextColor(Color.parseColor("#333333"));

        barchart.getAxisLeft().setDrawLabels(true);
        barchart.getAxisRight().setDrawLabels(false);
        barchart.getAxisRight().setDrawTopYLabelEntry(true);
        barchart.getAxisRight().setEnabled(false);
        barchart.getAxisRight().setAxisLineColor(Color.parseColor("#000000"));
        barchart.getAxisRight().setTextColor(Color.parseColor("#333333"));

        barchart.getLegend().setEnabled(false);
        barchart.getXAxis().setTextColor(Color.parseColor("#333333"));
        barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
      //  barchart.getAxisLeft().setStartAtZero(true);
        barchart.setData(data);
        barchart.setDescription("");
        barchart.setDescriptionColor(Color.parseColor("#333333"));
        //barchart.setDescriptionPosition(3f,3f);


        barchart.getAxisLeft().setAxisMaxValue(y_lg);
        barchart.getAxisLeft().setLabelCount(5,true);
        barchart.getAxisLeft().setAxisMinValue(y_sm-(y_lg-y_sm));

        barchart.getXAxis().setDrawGridLines(false);
        barchart.getAxisLeft().setDrawGridLines(false);
        barchart.getAxisRight().setDrawGridLines(false);
        barchart.animateY(1000);


        String chOpt=sharedpreferences.getString("chOpt","perf");
        if (chOpt.equals("perf")) {
          // chkperm.setChecked(true);
            icofilter.setText("Stock Price");
        }
        else if (chOpt.equals("eq")) {
           // chkequ.setChecked(true);
            icofilter.setText("Total Equity");
        }
        else if (chOpt.equals("ret")) {
           // chkret.setChecked(true);
            icofilter.setText("Total Return");
        }

    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {

            NumberFormat format = NumberFormat.getInstance(Locale.US);
            Log.e("chart value",String.valueOf(value.getY()));
           Toast.makeText(getApplicationContext(),"Net position: " + format.format((value.getY())),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() { }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        stock_sym stk=new stock_sym(activity_sym_details.this);
        if (stk.is_alert_set(sym_name))
            getMenuInflater().inflate(R.menu.sym_detail_menu_full, menu);
        else
            getMenuInflater().inflate(R.menu.sym_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                //startActivityAfterCleanup(MainActivity.class);
                finish();
                return true;

        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void startActivityAfterCleanup(Class<?> cls) {
        //  if (projectsDao != null) projectsDao.close();
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}

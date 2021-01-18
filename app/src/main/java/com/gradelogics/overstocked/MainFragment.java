package com.gradelogics.overstocked;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.Line;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private dbHelper db_helper;
    //   private SQLiteDatabase db;
    private RecyclerView listview_ticker;
    stock_sym stocki;
    LinearLayout welcomelayout;
    static SharedPreferences sharedpreferences;
    MyListAdapter adapter;
    LinearLayout icofilter,filterlay;
    TextView filtertext;
    NotificationCompat.InboxStyle inboxStyle;
    View root;
    public static TextView daily_value_change;
    PhoneUnlockReceiver mReceiver = new PhoneUnlockReceiver();
    float x1, x2, y1, y2=0;
ShimmerFrameLayout shimmerFrameLayout;
  //  private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         root = inflater.inflate(R.layout.app_bar_main, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MainFragment.this.getActivity().getWindow().setStatusBarColor(Color.parseColor("#46B7F8"));
        }

        sharedpreferences = MainFragment.this.getActivity().getSharedPreferences("stockfolio", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        listview_ticker = (RecyclerView) root.findViewById(R.id.listview_ticker);
        listview_ticker.setNestedScrollingEnabled(false);
        LinearLayout lyportfolio = (LinearLayout) root.findViewById(R.id.ly_portfolio);


        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_UNLOCKED);


        icofilter = (LinearLayout) root.findViewById(R.id.btn_filter);
        filtertext = (TextView) root.findViewById(R.id.filter_text);
        filterlay=(LinearLayout)root.findViewById(R.id.filter_lay);
        // icofilter.setPaintFlags(icofilter.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        icofilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainFragment.this.getActivity());
                dialog.setContentView(R.layout.display_option_dialog);

                RadioButton chkperm = (RadioButton) dialog.findViewById(R.id.chk_perf);
                RadioButton chkequ = (RadioButton) dialog.findViewById(R.id.chk_equ);
                RadioButton chkret = (RadioButton) dialog.findViewById(R.id.chk_ret);

                chkperm.setOnCheckedChangeListener(new myCheckBoxChnageClicker("perf"));
                chkequ.setOnCheckedChangeListener(new myCheckBoxChnageClicker("eq"));
                chkret.setOnCheckedChangeListener(new myCheckBoxChnageClicker("ret"));

                String dOpt = sharedpreferences.getString("dOpt", "perf");
                if (dOpt.equals("perf")) {
                    chkperm.setOnCheckedChangeListener(null);
                    chkperm.setChecked(true);
                    chkperm.setOnCheckedChangeListener(new myCheckBoxChnageClicker("perf"));
                } else if (dOpt.equals("eq")) {
                    chkequ.setOnCheckedChangeListener(null);
                    chkequ.setChecked(true);
                    chkequ.setOnCheckedChangeListener(new myCheckBoxChnageClicker("eq"));
                }
                else if (dOpt.equals("ret")) {
                    chkret.setOnCheckedChangeListener(null);
                    chkret.setChecked(true);
                    chkret.setOnCheckedChangeListener(new myCheckBoxChnageClicker("ret"));
                }
                dialog.setTitle("Display Options");
                dialog.show();
            }
        });

      //  setSupportActionBar(toolbar);

        // db_helper=new dbHelper(getApplicationContext());
        // db=db_helper.getWritableDatabase();


        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainFragment.this.getActivity(), activity_add_buy.class);
                startActivity(intent);
                //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });


       /* DrawerLayout drawer = (DrawerLayout) MainFragment.this.getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) MainFragment.this.getActivity().findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
*/


        //  handler.post(runnable);
        handler.postDelayed( runnable, 3000);//1200000

        //  load_portfolio();
        // load_all_syms();
        //  buildGraph();

        // Log.e("app","mainactivity");
        //  load_all_syms();
        // new sync_7_days().execute("");



        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.pullToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new sync_symbols_price().execute("");

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // listview_ticker.setHasFixedSize(false);
        // setListViewHeightBasedOnChildren(listview_ticker);

        // Intent intent=new Intent(getApplicationContext(),activitysplash.class);
        // startActivity(intent);

        LinearLayout circleperc=(LinearLayout)root.findViewById(R.id.circle_perc);
        android.view.ViewGroup.LayoutParams mParams = circleperc.getLayoutParams();
        //mParams.height = circleperc.getMeasuredWidth();
       // Log.e("circlewidth",String.valueOf(circleperc.getMeasuredWidth()));
       // circleperc.setLayoutParams(mParams);

        return  root;
    }

    private void load_all_content()
    {
        load_portfolio();
        load_all_syms();
        buildGraph();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) MainFragment.this.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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

                final Dialog dialog = new Dialog(MainFragment.this.getContext());
                dialog.setContentView(R.layout.display_option_dialog);

                RadioButton chkperm=(RadioButton)dialog.findViewById(R.id.chk_perf);
                RadioButton chkequ=(RadioButton)dialog.findViewById(R.id.chk_equ);
                RadioButton chkret=(RadioButton)dialog.findViewById(R.id.chk_ret);


                if (dOpt.equals("perf")) {
                    chkperm.setChecked(true);
                    filtertext.setText("Stock Performance");
                }
                else if (dOpt.equals("eq")) {
                    chkequ.setChecked(true);
                    filtertext.setText("Stock Equity");
                }
                else if (dOpt.equals("ret")) {
                    chkret.setChecked(true);
                    filtertext.setText("Total Return");
                }
                //get Shared Pref
                sharedpreferences = MainFragment.this.getActivity().getSharedPreferences("stockfolio", MODE_PRIVATE);
                editor = sharedpreferences.edit();


                editor.putString("dOpt",dOpt);
                editor.commit();
                // Log.e("dOpt",dOpt);

                load_all_content();

                adapter.notifyDataSetChanged();

                //  dialog.dismiss();
            }

        }
    }

    private void buildGraph()
    {


        ////BUILD BAR CHART
        Date date = new Date();
        String dateInString = "2011-11-30";  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        //set date to now-6;
       // c.add(Calendar.DATE, -10);
        int x=0;

        String[] axisData=new String[5];
        float[] yAxisData = new float[5];
        float[] daily_equit=new float[5];
        float y_lg = 0,y_sm=1000000000;

        stocki=new stock_sym(MainFragment.this.getActivity());
        stocki.sym_name="";

        NumberFormat format=NumberFormat.getCurrencyInstance(Locale.US);
        // Log.e("history size", String.valueOf(stocki.all_buy_history().size()));

        float last_val=0;
        float last_val_today=0;

        //minus 10 days, but ignore weekends
        int d=0;
        while(d<5)
        {

            sdf=new SimpleDateFormat("yyyy-MM-dd");
            Date resultdate = new Date(c.getTimeInMillis());
            String date_s=sdf.format(resultdate);

            sdf = new SimpleDateFormat("E");
            dateInString = sdf.format(resultdate);

            if (!dateInString.equals("Sun") && !dateInString.equals("Sat"))
            {
               d++;
            }
            c.add(Calendar.DATE, -1);
        }

        float daily_equity_change=0;
        while (x<5)
        {
            c.add(Calendar.DATE, 1);
            sdf=new SimpleDateFormat("yyyy-MM-dd");
            Date resultdate = new Date(c.getTimeInMillis());
            String date_s=sdf.format(resultdate);

            sdf = new SimpleDateFormat("E");
            dateInString = sdf.format(resultdate);

            // Log.e("String date for graph:",date_s);
           // axisData[x]=dateInString;
            stocki.get_details_byDate(date_s);
            if (!dateInString.equals("Sun") && !dateInString.equals("Sat")) {

                axisData[x]=dateInString;
                yAxisData[x] = stocki.sym_details.equity;
                last_val= stocki.sym_details.equity;

                x++;
            }

        }


        //check if portfolio exists
        stock_sym.portfolio portfolio=stocki.portfolio_position();

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

     //   Log.e("axis size",String.valueOf(axisData.length));
       // Log.e("Yaxis size",String.valueOf(yAxisData.length));

        //populate axis data
        ArrayList<String> labels = new ArrayList<String>();
        for(int i = 0; i < axisData.length; i++){

            labels.add(axisData[i]);
          //  Log.e("d",String.valueOf(axisData[i]));
        }

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < yAxisData.length; i++){
            entries.add(new BarEntry(yAxisData[i], i));
           // Log.e("e",String.valueOf(i) + " - " +String.valueOf(yAxisData[i]));
        }


        LineDataSet dataset = new LineDataSet(entries,"");
        dataset.setValueTextColor(Color.parseColor("#ffffff"));

        dataset.setDrawValues(false);


        LineChart barchart=(LineChart) root.findViewById(R.id.lineC);
      //  barchart.setBackgroundColor(Color.parseColor("#ebfbff"));

        barchart.setTouchEnabled(true);
        CustomMarkerView mv = new CustomMarkerView (MainFragment.this.getContext(), R.layout.custom_marker_view);
        barchart.setMarkerView(mv);

       // barchart.getAxisLeft().setAxisMaxValue(y_lg);
       // barchart.getAxisLeft().setAxisMinValue(y_sm);

        LineData data = new LineData(labels, dataset);
        dataset.setColor(Color.parseColor("#3a93f2"));
        dataset.setDrawFilled(true);
        dataset.setDrawCircles(false);
        dataset.setFillColor(Color.parseColor("#ebfbff"));
        //Axe Y
        // barchart.getAxisLeft().setAxisMaxValue(100);
        barchart.getAxisLeft().setDrawTopYLabelEntry(false);
        // barchart.getAxisLeft().setEnabled(false);
        barchart.getAxisLeft().setDrawAxisLine(false);
        barchart.getAxisLeft().setDrawGridLines(false);

        barchart.getAxisLeft().setAxisLineColor(Color.parseColor("#000000"));
        barchart.getAxisLeft().setTextColor(Color.parseColor("#ffffff"));

        barchart.getAxisLeft().setDrawLabels(false);
        barchart.getAxisRight().setDrawLabels(false);
        barchart.getAxisRight().setDrawTopYLabelEntry(false);
        // barchart.getAxisRight().setEnabled(false);
        barchart.getAxisRight().setAxisLineColor(Color.parseColor("#000000"));
        //barchart.getAxisRight().setTextColor(Color.parseColor("#ffffff"));

        barchart.getLegend().setEnabled(false);
        barchart.getXAxis().setTextColor(Color.parseColor("#ffffff"));
        barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barchart.setData(data);
        barchart.setDescription("");
        barchart.setDescriptionColor(Color.parseColor("#ffffff"));
        //barchart.setDescriptionPosition(3f,3f);


        barchart.getXAxis().setDrawGridLines(false);
        barchart.getAxisLeft().setDrawGridLines(false);
        barchart.getAxisRight().setDrawGridLines(false);
        barchart.animateY(1000);

        ////

    }


    private class sync_7_days extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {


                String domain,schyear,schterm="";
                int studentid=0;
                SharedPreferences prefs=MainFragment.this.getActivity().getSharedPreferences("gradelogics",MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString("student_object", "");
                // student stuobj = gson.fromJson(json, student.class);
                Log.e("sync","7 days");

                URL url=new URL("https://stockmate.glazedev.com/api/stocks/current/sevendays");
                // Log.e("url",url.toString());
                urlConnection=(HttpURLConnection)url.openConnection();

                InputStream in=new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));

                String line;
                while((line=reader.readLine()) != null){
                    result=line;
                }

            }catch (Exception e){
                Log.e("error",e.toString());
            }finally {
                urlConnection.disconnect();
            }

            //  Log.e(" grades json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            // final ArrayList<transaction> dataSet=new ArrayList<transaction>();
            try {


                JSONArray jArray=new JSONArray(res);
                SharedPreferences.Editor editor=MainFragment.this.getActivity().getSharedPreferences("overstockd",MODE_PRIVATE).edit();
                stock_sym stockSym=new stock_sym(MainFragment.this.getActivity());
                for (int x=0;x<jArray.length();x++)
                {
                    // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject jObj=jArray.getJSONObject(x);

                    String[]dateS=jObj.getString("import_date").split("\\s+");
                    String dateInString = dateS[0];  // Start date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    Date date = sdf.parse(dateInString);

                    if (stockSym.getSymExist_withDate(jObj.getString("symbol"),dateInString) == 0) {
                        stockSym.sym_name = jObj.getString("symbol");
                        stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("last_trade_price")).floatValue();
                        stockSym.import_date=dateInString;

                        //  stockSym.add_sym_to_db(false);

                        // stockSym.add_sym_to_db_simple(false);
                        // Log.e("stockd","add sym to db: " +  stockSym.sym_name + " date:" + dateInString);
                    }else
                    {
                        stockSym.sym_name = jObj.getString("symbol");
                        stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("last_trade_price")).floatValue();
                        //Log.e(stockSym.sym_name,String.valueOf(jObj.getDouble("last_trade_price")));
                        stockSym.import_date=dateInString;

                        //  stockSym.add_sym_to_db(true);

                        // stockSym.add_sym_to_db_simple(true);
                        // Log.e("stockd","update db: " +  stockSym.sym_name + " date:" + dateInString);
                    }


                    if (stockSym.getSymExist(jObj.getString("symbol")) == 0) {
                        stockSym.sym_name = jObj.getString("symbol");
                        stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("last_trade_price")).floatValue();
                        // stockSym.import_date=dateInString;
                        // stockSym.add_sym_to_db(false);
                        stockSym.add_sym_to_db_simple(false);
                        // Log.e("stockd","add sym to db: " +  stockSym.sym_name + " date:" + dateInString);
                    }else
                    {
                        stockSym.sym_name = jObj.getString("symbol");
                        stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("last_trade_price")).floatValue();
                        stockSym.import_date=dateInString;
                        // stockSym.add_sym_to_db(true);
                        stockSym.add_sym_to_db_simple(true);
                        //  Log.e("stockd","update db: " +  stockSym.sym_name + " date:" + dateInString);
                    }

                }
                // Log.e("sch json",schObject.toString());

                new sync_symbols_price().execute("");

            }catch (Exception e)
            {

            }finally {
                // loadUI();
            }
        }
    }

    final Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try{
                //do your code here
               // load_all_content();
                 Log.e("main activity","refresher");
            }
            catch (Exception e) {
                // TODO: handle exception
            }
            finally{
                //also call the same runnable to call it at regular interval
                handler.postDelayed(this, 110000);//1200000
            }
        }
    };

    private void load_portfolio() {
        stock_sym istok=new stock_sym(MainFragment.this.getActivity());

        dail_val=0;
        TextView portfolio_value=(TextView)root.findViewById(R.id.txt_portfolio_value);
        TextView postfolio_position=(TextView)root.findViewById(R.id.txt_position_value);
        TextView portfolio_net_pos=(TextView)root.findViewById(R.id.txt_position_perc);
        TextView txtLastUpdate=(TextView)root.findViewById(R.id.txt_last_update);
        daily_value_change=(TextView)root.findViewById(R.id.txt_daily_change);
        TextView txt_best_sym=(TextView)root.findViewById(R.id.txt_best_perform_sym);
        TextView txt_best_perc=(TextView)root.findViewById(R.id.txt_best_perform_sym_perc);

        TextView txt_least_sym=(TextView)root.findViewById(R.id.txt_least_perform_sym);
        TextView txt_least_perc=(TextView)root.findViewById(R.id.txt_least_perform_sym_perc);

        LinearLayout circleperc=(LinearLayout)root.findViewById(R.id.circle_perc);




        stock_sym.portfolio portfolio=istok.portfolio_position();
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

      //  daily_value_change.setText(format.format(today_value));

        txt_best_sym.setText(istok.best_perform().sym_name);
        txt_best_perc.setText(String.valueOf(istok.best_perform().change) + "%");

        txt_least_sym.setText(istok.least_perform().sym_name);
        txt_least_perc.setText(String.valueOf(istok.least_perform().change) + "%");

        portfolio_value.setText(format.format(portfolio.total_value));

        if (portfolio.net_pos<0) {
           postfolio_position.setTextColor(Color.parseColor("#CC143B"));
            //portfolio_net_pos.setTextColor(Color.parseColor("#CC143B"));
            portfolio_net_pos.setText(String.valueOf(portfolio.perc_pos)+"%");
            postfolio_position.setText(format.format(portfolio.net_pos));
            circleperc.setBackground(MainFragment.this.getContext().getDrawable(R.drawable.red_circle));
        }
        else {
           postfolio_position.setTextColor(Color.parseColor("#17B064"));
            //portfolio_net_pos.setTextColor(Color.parseColor("#17B064"));
            portfolio_net_pos.setText(String.valueOf("+"+portfolio.perc_pos)+"%");
            postfolio_position.setText("+"+format.format(Math.round(portfolio.net_pos)));
            circleperc.setBackground(MainFragment.this.getContext().getDrawable(R.drawable.green_circle));
        }


        txtLastUpdate.setText(istok.last_update());

        //check if portfolio exists

        SharedPreferences sharedPreferences = MainFragment.this.getActivity().getSharedPreferences("stockfolio", MODE_PRIVATE);
        String agreed = sharedPreferences.getString("agreed", "0");
        // Log.e("agreed",agreed);
        //agreed="0";
        if (agreed.equals("0")) {
            Intent intent = new Intent(MainFragment.this.getContext(), activitysplash.class);
            startActivity(intent);
        }
        Log.e("stock size",String.valueOf(istok.my_symbols().size()));
        if (istok.my_symbols().size()==0) {
            // welcomelayout.setVisibility(View.VISIBLE);
            Intent intent=new Intent(MainFragment.this.getContext(),activity_empty_portfolio.class);
            startActivity(intent);
            Log.e("ac","make visible:: value " + String.valueOf(portfolio.total_value));
        }

        // welcomelayout.setVisibility(View.GONE);
    }

public static float dail_val=0;

public static void update_daily_change()
{
    NumberFormat nf=NumberFormat.getCurrencyInstance(Locale.US);
    daily_value_change.setText(nf.format(dail_val));
    if(dail_val<0)
        daily_value_change.setTextColor(Color.parseColor("#FC1E46"));
    else
        daily_value_change.setTextColor(Color.parseColor("#08A038"));

    SharedPreferences.Editor editor=sharedpreferences.edit();
    editor.putString("daily_change",nf.format(dail_val));


}

    private void load_all_syms()
    {
        String[] syms={};
        stock_sym stockSym=new stock_sym(MainFragment.this.getActivity());
        try {

            String dOpt=sharedpreferences.getString("dOpt","pref");
            if (dOpt.equals("perf")) {
                // chkperm.setChecked(true);
                filtertext.setText("Stock Performance");
            }
            else if (dOpt.equals("eq")) {
                //  chkequ.setChecked(true);
                filtertext.setText("Stock Equity");
            }
            else if (dOpt.equals("ret")) {
                // chkret.setChecked(true);
                filtertext.setText("Total Return");
            }

            ArrayList<stock_sym> my_syms=stockSym.my_symbols();
            adapter=new MyListAdapter(my_syms,MainFragment.this.getActivity());

            //listview_ticker.setHasFixedSize(true);
            listview_ticker.setLayoutManager(new LinearLayoutManager(MainFragment.this.getActivity()));
            listview_ticker.setAdapter(adapter);

          /* listview_ticker.setOnClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   stock_sym item = (stock_sym)parent.getItemAtPosition(position);

                   Intent sym_details=new Intent(getApplicationContext(),activity_sym_details.class);
                   sym_details.putExtra("symname",item.sym_name);
                   startActivity(sym_details);
               }
           });*/

            //  setListViewHeightBasedOnChildren(listview_ticker);



        }catch (Exception ex)
        {
            Log.e("stockd",ex.getMessage());
        }



    }

    private class sync_symbols_price extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        boolean call_alerts=false;
        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,schterm="";
                int studentid=0;
                SharedPreferences prefs=MainFragment.this.getActivity().getSharedPreferences("gradelogics",MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString("student_object", "");
                // student stuobj = gson.fromJson(json, student.class);
                // Log.e("service","sync price");

                URL url=new URL("https://stockmate.glazedev.com/api/stocks/current");
                // Log.e("url",url.toString());
                urlConnection=(HttpURLConnection)url.openConnection();

                InputStream in=new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));

                String line;
                while((line=reader.readLine()) != null){
                    result=line;
                }

            }catch (Exception e){
                Log.e("error",e.toString());
            }finally {
                urlConnection.disconnect();
            }

            // Log.e(" main price json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            // final ArrayList<transaction> dataSet=new ArrayList<transaction>();
            try {

                Log.e("ready","YES");
                inboxStyle=new NotificationCompat.InboxStyle();
                JSONArray jArray=new JSONArray(res);
                SharedPreferences.Editor editor=MainFragment.this.getActivity().getSharedPreferences("overstockd",MODE_PRIVATE).edit();
                stock_sym stockSym=new stock_sym(MainFragment.this.getActivity());
                // JSONObject jObj=jArray.getJSONObject(x);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date c = Calendar.getInstance().getTime();
                String date = sdf.format(c);
                Log.e("datez",date.toString());

                // Log.e("symbolzz",jArray.toString());
                for (int x=0;x<jArray.length();x++)
                {
                    // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject jObj=jArray.getJSONObject(x);

                    // Log.e("symbol",jObj.toString());

                    String[]dateS=jObj.getString("import_date").split("\\s+");
                    String dateInString =jObj.getString("import_date");// dateS[0];  // Start date
                    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    // Calendar c = Calendar.getInstance();
                    // Date date = sdf.parse(dateInString);

                    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd '-' HH:mm");
                    // Calendar c = Calendar.getInstance();
                    // Date date = sdf.parse(Calendar.getInstance().toString());
                    //  Log.e("date",date.toString());

                    if (stockSym.getSymExist_withDate(jObj.getString("symbol"),dateInString) == 0) {
                        stockSym.sym_name = jObj.getString("symbol");
                        stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("last_trade_price")).floatValue();
                        stockSym.import_date=dateInString;

                        stockSym.add_sym_to_db_2(false);
                    }else
                    {
                        stockSym.sym_name = jObj.getString("symbol");
                        stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("last_trade_price")).floatValue();

                        stockSym.import_date=dateInString;

                        stockSym.add_sym_to_db_2(true);
                    }


                    if (stockSym.getSymExist(jObj.getString("symbol")) == 0) {
                        stockSym.sym_name = jObj.getString("symbol");
                        stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("last_trade_price")).floatValue();
                        // stockSym.import_date=dateInString;
                        // stockSym.add_sym_to_db(false);
                        stockSym.add_sym_to_db_simple(false);
                        // Log.e("stockd","add sym to db: " +  stockSym.sym_name + " date:" + dateInString);
                    }else
                    {
                        stockSym.sym_name = jObj.getString("symbol");
                        stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("last_trade_price")).floatValue();
                        stockSym.import_date=dateInString;
                        // stockSym.add_sym_to_db(true);
                        stockSym.add_sym_to_db_simple(true);
                        //   Log.e("stockd","update db: " +  stockSym.sym_name + " date:" + dateInString);
                    }
                    //check for price alerts
                    if (check_alerts(stockSym))
                        call_alerts=true;
                }
                //Log.e("sch json",schObject.toString());


            }catch (Exception e)
            {
                Log.e("syncer error",e.getMessage());
            }finally {
                // loadUI();
                load_all_syms();
                load_portfolio();
                buildGraph();

                if (call_alerts)
                    alert_notify();
            }
        }
    }



    void alert_notify()
    {
        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)

        int currentHourIn12Format = rightNow.get(Calendar.HOUR);
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        Log.e("hr",String.valueOf(currentHourIn24Format));
        // if (currentHourIn24Format==17)
        {
            stock_sym istok = new stock_sym(MainFragment.this.getActivity());
            Context cx=MainFragment.this.getActivity();
            stock_sym.portfolio portfolio = istok.portfolio_position();
            NumberFormat format = NumberFormat.getCurrencyInstance();

            final String NOTIFICATION_CHANNEL_ID = "channel_id_3";
//Notification Channel ID passed as a parameter here will be ignored for all the Android versions below 8.0
            NotificationCompat.Builder builder = new NotificationCompat.Builder(cx, NOTIFICATION_CHANNEL_ID);
            builder.setContentTitle("Stock Price Alerts ");
            builder.setContentText("The below stocks require your attention ");
            builder.setSmallIcon(R.drawable.stockmate_sm);
            builder.setLargeIcon(BitmapFactory.decodeResource(cx.getResources(), R.drawable.stockmate));
            // builder.setStyle(new NotificationCompat.InboxStyle());
            builder.setStyle(inboxStyle);

            // Unique identifier for notification
            final int NOTIFICATION_ID = 103;
            //This is what will will issue the notification i.e.notification will be visible

            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

            Intent intent = new Intent(cx, alert_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(cx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//Following will set the tap action
            builder.setContentIntent(pendingIntent);

            Notification notification = builder.build();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(cx);


            // This is the Notification Channel ID. More about this in the next section
            //final String NOTIFICATION_CHANNEL_ID = "channel_id";
//User visible Channel Name
            final String CHANNEL_NAME = "Notification Channel";
// Importance applicable to all the notifications in this Channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//Notification channel should only be created for devices running Android 26

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);
                //Boolean value to set if lights are enabled for Notifications from this Channel
                notificationChannel.enableLights(true);
                //Boolean value to set if vibration are enabled for Notifications from this Channel
                notificationChannel.enableVibration(true);
                //Sets the color of Notification Light
                notificationChannel.setLightColor(Color.GREEN);
                //Set the vibration pattern for notifications. Pattern is in milliseconds with the format {delay,play,sleep,play,sleep...}
                notificationChannel.setVibrationPattern(new long[]{
                        500,
                        500,
                        500,
                        500,
                        500
                });
                //Sets whether notifications from these Channel should be visible on Lockscreen or not
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                NotificationManager notificationManager = (NotificationManager) cx.getSystemService(MainFragment.this.getActivity().NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(notificationChannel);
                }


            }


            notificationManagerCompat.notify(NOTIFICATION_ID, notification);

            // Log.e("notify", "me");
        }
    }


    private boolean check_alerts(stock_sym symbolclass)
    {
        boolean callalert=false;
        try
        {
            NumberFormat format = NumberFormat.getCurrencyInstance();
            SharedPreferences sharedPreferences=MainFragment.this.getActivity().getSharedPreferences("stockfolio",MODE_PRIVATE);


            stock_sym stockclass=new stock_sym(MainFragment.this.getActivity());
            ArrayList<stock_sym.alertz>s_alerts = stockclass.my_alerts();
            // Log.e("alert sym",symbolclass.sym_name);
            for (stock_sym.alertz stockalert:s_alerts) {
                String stockid=sharedPreferences.getString(String.valueOf(stockalert.id),"0");
                if(stockid.equals("0"))
                {
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    //alert not yet met, let's check

                    if(stockalert.symbol.equals(symbolclass.sym_name))
                    {

                        if(stockalert.criteria.equals("Above")){
                            //price above
                            if (symbolclass.current_price>stockalert.price){
                                //price is above target price// call alert
                                inboxStyle.addLine(symbolclass.sym_name + " (" + format.format(symbolclass.current_price) + ") has gone above " + format.format(stockalert.price));
                                callalert=true;
                                Log.e("Stock  alert " + symbolclass.sym_name, "has gone above " + String.valueOf(stockalert.price));
                                editor.putString(String.valueOf(stockalert.id),"1");
                                editor.commit();
                            }
                        }else{
                            if (symbolclass.current_price<stockalert.price){
                                //price is above target price// call alert
                                inboxStyle.addLine(symbolclass.sym_name + " (" + format.format(symbolclass.current_price) + ") has fallen below " + format.format(stockalert.price));
                                callalert=true;
                                Log.e("Stock  alert " + symbolclass.sym_name, "has fallen below " + String.valueOf(stockalert.price));

                                editor.putString(String.valueOf(stockalert.id),"1");
                                editor.commit();
                            }
                        }
                    }
                }
            }


        }catch (Exception ex)
        {
            Log.e("Alertcheck Ex",ex.getMessage().toString());
        }
        finally {
            return callalert;
        }
    }

    private  void pass_code_check()
    {

        //check if session expired
        long last_session_start=sharedpreferences.getLong("session_start",0);


        boolean locked=sharedpreferences.getBoolean("islocked",false);

        Log.e("lock",String.valueOf(locked));
        if(locked) {
           // stock_sym stk = new stock_sym(MainFragment.this.getActivity());
            //if (stk.pass_code_exist())
            {
                // Intent intent = new Intent(getApplicationContext(), enter_passcode.class);
                // startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    KeyguardManager km = (KeyguardManager) MainFragment.this.getActivity().getSystemService(KEYGUARD_SERVICE);

                    if (km.isKeyguardSecure()) {
                        Intent authIntent = km.createConfirmDeviceCredentialIntent("Stockmate Portfolio Protected", "Enter your phone's PIN to gain access to your portfolio");
                        startActivityForResult(authIntent, 1);
                    }
                }
            }
        }
    }



    @Override
    public void onPause(){
        super.onPause();
        //unregisterReceiver(mReceiver);
    }

    public static void setListViewHeightBasedOnChildren(NonScrollListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;// + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    @Override
    public void onResume() {

        super.onResume();
        Log.e("StockD","back to MainFragment");
        // new sync_7_days().execute("");
        //  new sync_symbols_price().execute("");



       // pass_code_check();
      //  boolean locked=sharedpreferences.getBoolean("islocked",false);
        //if(!locked)
        {
MainFragment.this.getActivity().runOnUiThread(new Runnable() {
    @Override
    public void run() {
        load_all_content();
    }
});


        }



        // new sync_symbols().execute("");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Log.e("where","mainFragmet");
//            load_portfolio();
         //   load_all_syms();
          //  buildGraph();
        } else {

        }
    }

}

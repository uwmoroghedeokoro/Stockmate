package com.gradelogics.overstocked;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import lecho.lib.hellocharts.view.ColumnChartView;

 public  class alert_activity extends rootAnimator {

    ListView lv;
    stock_sym thistock;
    Toolbar toolbar;
    ColumnChartView columnChartView;
    TextView icofilter;
    SharedPreferences sharedpreferences;
    public alertAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alert_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getWindow().setStatusBarColor(Color.parseColor("#46B7F8"));


        actionBar.setTitle("My Stock Alerts");

        lv=(ListView)findViewById(R.id.lv);
        //ViewGroup headerView = (ViewGroup)getLayoutInflater().inflate(R.layout.lv_alert_header, lv,false);
        // Add header view to the ListView
       // lv.addHeaderView(headerView);

        thistock=new stock_sym(getApplicationContext());
       // final ArrayList<stock_sym> my_syms=new ArrayList<stock_sym>();
        final ArrayList<stock_sym.alertz> my_alertz=thistock.my_alerts();

        adapter=new alertAdapter(this,R.layout.alert_item,my_alertz);

        lv.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.alert_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
               finish();
                return true;
            case R.id.icon_add:
                //save order

                // custom dialog
                final Dialog dialog = new Dialog(alert_activity.this);
                dialog.setContentView(R.layout.pop_price_notify);
                dialog.setTitle("Net Position Notification");

                stock_sym stocksym=new stock_sym(getApplicationContext());
                ArrayList arrayList=stocksym.get_symbols();

                final Spinner spintarget=(Spinner)dialog.findViewById(R.id.spin_target);
                final EditText target_price=(EditText)dialog.findViewById(R.id.txt_price);

                ArrayList<String> opt=new ArrayList<String>();opt.add("Above");opt.add("Below");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_white_spinner,opt);
                spintarget.setAdapter(adapter); // this will set list of values to spinner

              //  stock_sym stocksym=new stock_sym(getApplicationContext());
              //  ArrayList arrayList=stocksym.get_symbols();

                ArrayAdapter<String> symadapter=new ArrayAdapter(this,R.layout.simple_white_spinner, arrayList);
                final Spinner sym_spin=dialog.findViewById(R.id.spin_sym);
                symadapter.setDropDownViewResource(R.layout.spinner_item_white);
                sym_spin.setAdapter(symadapter);

                Button dialogButton = (Button) dialog.findViewById(R.id.btn_sv);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stock_sym stockm=new stock_sym(getApplicationContext());
                        stockm.sym_name=sym_spin.getSelectedItem().toString();
                        String target_thresh = spintarget.getSelectedItem().toString();
                        //Log.e("spin",spinv);
                        if (!target_price.getText().toString().equals("")) {
                            float alert_value = Float.parseFloat(target_price.getText().toString());
                            stockm.add_sym_alert(alert_value,target_thresh);
                            dialog.dismiss();
                            finish();startActivity(getIntent());
                        }
                    }
                });

                dialog.show();

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

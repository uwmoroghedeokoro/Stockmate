package com.gradelogics.overstocked;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.gradelogics.overstocked.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class activity_add_buy extends rootAnimator {

    EditText txtdate,txtunits,txtprice,txtfees;
    DatePickerDialog picker;
    TextView txtTotal;
    int sel_year,sel_month,sel_day;
    float buy_total=0;
    int selected_rec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//nichlos mc
        // android.app.ActionBar bar=getActionBar();
        // bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.dirtyfog));

        setContentView(R.layout.activity_add_buy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(Color.parseColor("#46B7F8"));

        Intent intent=getIntent();
         selected_rec=intent.getIntExtra("buy_id",-1);

        if (selected_rec>0)
        {
            actionBar.setTitle("Edit Buy Order");
        }

        txtdate = (EditText) findViewById(R.id.txt_date);
        txtdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                picker = new DatePickerDialog(activity_add_buy.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                txtdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                sel_day=dayOfMonth;sel_month=monthOfYear;sel_year=year;
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        txtunits=(EditText)findViewById(R.id.txt_units);
        txtfees=(EditText)findViewById(R.id.txt_tran_fee);
        txtprice=(EditText)findViewById(R.id.txt_unit_price);
        txtdate=(EditText)findViewById(R.id.txt_date);
        txtTotal=(TextView)findViewById(R.id.txt_tran_total);

        txtunits.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculate_transaction_total();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtfees.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculate_transaction_total();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtprice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculate_transaction_total();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        load_data();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities


    private void load_data()
    {
        stock_sym stocksym=new stock_sym(getApplicationContext());
        ArrayList arrayList=stocksym.get_symbols();

        ArrayAdapter<String> adapter=new ArrayAdapter(this,R.layout.spinner_item_white, arrayList);
        Spinner sym_spin=findViewById(R.id.spin_sym);
        adapter.setDropDownViewResource(R.layout.spinner_item_white);
        sym_spin.setAdapter(adapter);

        if (selected_rec>0) {
            stocksym.get_buy_rec(selected_rec);
           // Log.e("rec date",stocksym.getFormattedDate());
            txtdate.setText(stocksym.getFormattedDate());
            txtunits.setText(String.valueOf(stocksym.buy_qty));
            txtprice.setText(String.valueOf(stocksym.buy_price));
            txtfees.setText(String.valueOf(stocksym.buy_fee));

            for (int position = 0; position < adapter.getCount(); position++) {

                if(adapter.getItem(position).equals(stocksym.sym_name)) {
                    Log.e("spinner",adapter.getItem(position));
                    sym_spin.setSelection(position);
                    return;
                }
            }

        }
           // Toast.makeText(getApplicationContext(),"selected " + String.valueOf(selected_rec),Toast.LENGTH_SHORT).show();
    }

    public static void selectSpinnerItemByValue(Spinner spnr, long value) {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if(adapter.getItemId(position) == value) {
                spnr.setSelection(position);
                return;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                //startActivityAfterCleanup(MainActivity.class);
                finish();
                return true;
            case R.id.save_btn:
                //save order
                save_order();
                return true;
            case R.id.del_btn:
                //save order
                if (selected_rec<0)
                    Toast.makeText(activity_add_buy.this,"Nothing to delete",Toast.LENGTH_LONG).show();
                    else
            {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Buy Order")
                        .setMessage("Sure you want to remove this order?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(activity_add_buy.this, "Buy Order Removed", Toast.LENGTH_SHORT).show();
                                //delete order
                                stock_sym stockz = new stock_sym(getApplication());
                                stockz.remove_buy_order(selected_rec);
                              stockz.get_buy_rec(selected_rec);
                              Intent intent=new Intent(getApplicationContext(),activity_sym_details.class);
                              intent.putExtra("symname",stockz.sym_name);
                           //   Log.e("putExtra",stockz.sym_name);
                            //  startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
                //save_order();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void save_order()
    {
        stock_sym this_order=new stock_sym(getApplicationContext());
        try {
            Spinner sym_spin = findViewById(R.id.spin_sym);

            if (!txtdate.getText().toString().equals("")) {
                this_order.sym_name = sym_spin.getSelectedItem().toString();
                this_order.buy_qty = Integer.parseInt(txtunits.getText().toString());
                this_order.buy_fee = txtfees.getText().toString().equals("")?0:Float.parseFloat(txtfees.getText().toString());
                this_order.buy_price = Float.parseFloat(txtprice.getText().toString());
                this_order.buy_total = (buy_total);

                this_order.buy_date = getTimeInMillis(sel_day, sel_month, sel_year);
              //  Log.e("sel_date", String.valueOf(this_order.getFormattedDate()));

                this_order.add_buy(selected_rec);

                Toast.makeText(getApplicationContext(), "Buy Order Saved", Toast.LENGTH_LONG).show();
//finish();
                Intent intent = new Intent(getApplicationContext(), NewMain.class);
                startActivity(intent);
            }else
            {
                //
                Toast.makeText(getApplicationContext(),"Please enter a purchase date",Toast.LENGTH_LONG).show();
            }

        }catch (Exception ex)
        {
            Log.e("StockD error",ex.getMessage());
        }

    }




    private void startActivityAfterCleanup(Class<?> cls) {
      //  if (projectsDao != null) projectsDao.close();
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public static long getTimeInMillis(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    private void calculate_transaction_total()
    {
        float stock_value,total_value,fees=0;
        int units;
         stock_value=0;

        if (!txtunits.getText().toString().equals("") && !txtprice.getText().toString().equals("")) {
            units = Integer.parseInt(txtunits.getText().toString());

            if(txtfees.getText().toString().equals(""))fees=0;
            else
            fees = Float.parseFloat(txtfees.getText().toString());

            if (!txtprice.getText().toString().equals("."))stock_value = Float.parseFloat(txtprice.getText().toString());


            total_value = ((units * stock_value) + fees);
            buy_total=total_value;

            NumberFormat format = NumberFormat.getCurrencyInstance();
            // ((TextView) findViewById(R.id.text_result)).setText(format.format(result));
            txtTotal.setText(String.valueOf(format.format(total_value)));
        }
    }
}

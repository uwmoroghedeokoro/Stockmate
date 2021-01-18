package com.gradelogics.overstocked;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gradelogics.overstocked.R;

import java.util.ArrayList;

public class live_stock_table extends rootAnimator implements SearchView.OnQueryTextListener {

    ListView listview_ticker;
    symbolAdapter adapter;
    SearchView searchView;
   public static ArrayList<stock_sym> my_syms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stock_table);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#46B7F8"));
        }

        listview_ticker=(ListView) findViewById(R.id.lv);
        // Locate the EditText in listview_main.xml
        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);

        Intent intent=getIntent();

            actionBar.setTitle("Live Stock Table");

            //load_all_syms();
    }


    public void load_all_syms()
    {
        String[] syms={"NCB","BIL","SVL","KW","SEP"};
        stock_sym stockSym=new stock_sym(getApplication());
        try {


           my_syms=stockSym.all_symbols();
             adapter=new symbolAdapter(this,R.layout.symbol_list_item,my_syms);

            listview_ticker.setAdapter(adapter);

            listview_ticker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final stock_sym item = (stock_sym)parent.getItemAtPosition(position);

                    // custom dialog
                    final Dialog dialog = new Dialog(live_stock_table.this);
                    dialog.setContentView(R.layout.pop_price_notify);
                    dialog.setTitle("Price Alerts");

                    Button dialogButton = (Button) dialog.findViewById(R.id.btn_sv);

                    stock_sym stockm=new stock_sym(getApplicationContext());
                    stockm.sym_name=item.sym_name;
                   // TextView txtsym=(TextView) dialog.findViewById(R.id.txt_sym);
                   // txtsym.setText(item.sym_name);
                    Log.e("selecte ",item.sym_name);
                    final EditText alert_v=(EditText)dialog.findViewById(R.id.txt_price);
                    final Spinner spinsym=(Spinner)dialog.findViewById(R.id.spin_sym);

                    ArrayList<String> opt=new ArrayList<String>();opt.add("Above");opt.add("Below");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_white_spinner,opt);
                    spinsym.setAdapter(adapter); // this will set list of values to spinner



                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            stock_sym stockm=new stock_sym(getApplicationContext());
                            stockm.sym_name=item.sym_name;
                            String spinv = spinsym.getSelectedItem().toString();
                            //Log.e("spin",spinv);
                            if (!alert_v.getText().toString().equals("")) {
                                float alert_value = Float.parseFloat(alert_v.getText().toString());
                                stockm.add_sym_alert(alert_value,spinv);
                                dialog.dismiss();
                            }
                        }
                    });

                    //dialog.show();
                   Intent sym_details=new Intent(getApplicationContext(),activity_sym_details.class);
                    sym_details.putExtra("symname",item.sym_name);
                    startActivity(sym_details);
                }
            });


        }catch (Exception ex)
        {
            Log.e("stockd",ex.getMessage());
        }



    }


   @Override
   public void onResume()
   {
       super.onResume();
       Log.e("where","JSE Live");
       load_all_syms();
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //    getMenuInflater().inflate(R.menu.sym_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
               // startActivityAfterCleanup(MainActivity.class);
                finish();
                return true;
            case R.id.alert_btn:
                //save order

                // custom dialog
               // startActivityAfterCleanup(alert_activity.class);
                //open alert activity

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

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.filter(s);
        return false;
    }
}

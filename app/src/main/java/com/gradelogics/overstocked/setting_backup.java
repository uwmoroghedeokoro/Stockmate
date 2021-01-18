package com.gradelogics.overstocked;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static android.accounts.AccountManager.newChooseAccountIntent;

public class setting_backup extends rootAnimator {

    private static final int PERMISSION_READ_STATE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final int REQUEST_CODE_PICK_ACCOUNT = 3;
    private String wantPermission = Manifest.permission.GET_ACCOUNTS;
    private String selected_account="-";
    TextView dialogButton;
    boolean restore=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_backup);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Cloud Backup");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#46B7F8"));
        }
        final stock_sym istok = new stock_sym(this);

        Switch simpleSwitch = (Switch) findViewById(R.id.opt_switch);
        //final stock_sym istok = new stock_sym(this);
        if (istok.isSync()) {
            simpleSwitch.setChecked(true);
        } else
            simpleSwitch.setChecked(false);


        Log.e("sync",String.valueOf(istok.isSync()));
        TextView optrestore=(TextView)findViewById(R.id.opt_restore);


        optrestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(setting_backup.this);
                dialog.setContentView(R.layout.pop_up_select_email_account);
                dialog.setTitle("Sell Shares");

                Button btnrestoredb=(Button)dialog.findViewById(R.id.btn_restoredb);

                btnrestoredb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!selected_account.equals("-")){
                            dialog.dismiss();
                            new restoreDB().execute("");
                        }

                    }
                });


                dialogButton = (TextView) dialog.findViewById(R.id.btn_sel_account);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stock_sym stockm=new stock_sym(getApplicationContext());
                       // account chooser
                        restore=true;
                       getEmails();
                    }
                });

                dialog.show();
            }
        });

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {

                    Log.e("switch", "checked");
                    if (!checkPermission(wantPermission)) {
                        requestPermission(wantPermission);
                    } else {
                        restore=false;
                        getEmails();
                    }


                } else {
                    istok.setDbSync(0);
                    Log.e("switch", "ucheck");
                }
            }
        });



    }

    private class restoreDB extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        protected String doInBackground(String... params) {
            String result="";
            //HttpURLConnection urlConnection;
            try {

                final stock_sym istok = new stock_sym(getApplicationContext());
                Gson gson=new Gson();

                Log.e("action","lets restore");
                URL url=new URL("https://stockmate.gradelogics.com/api/restoredb?email="+selected_account);
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


            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            res=res.replaceAll("\\\\","");
            res=res.substring(1, res.length()-1);
            Log.e("response",res);

            stock_sym stk=new stock_sym(getApplicationContext());



            try {
                JSONArray jArray = new JSONArray(res);

                if(jArray.length()<1)
                {
                    Toast.makeText(getApplicationContext(),"No backup data was found on server",Toast.LENGTH_LONG).show();
                }else {
                    stk.clear_portfolio();

                    for (int x = 0; x < jArray.length(); x++) {
                        // Log.e("array el",jArray.getJSONObject(x).toString());
                        JSONObject jObj = jArray.getJSONObject(x);

                        stock_sym this_order=new stock_sym(getApplicationContext());
                        this_order.sym_name = jObj.getString("sym_name");
                        this_order.buy_qty =jObj.getInt("buy_qty");
                        this_order.buy_fee = Float.valueOf(jObj.getString("buy_fee"));
                        this_order.buy_price = Float.valueOf(jObj.getString("buy_price"));
                        this_order.buy_total = Float.valueOf(jObj.getString("buy_total"));

                        this_order.buy_date = (jObj.getLong("buy_date"));
                        //  Log.e("sel_date", String.valueOf(this_order.getFormattedDate()));

                        this_order.add_buy(-1);

                        //Log.e("sym from restore", jObj.getString("sym_name"));
                    }
                    Toast.makeText(getApplicationContext(),"Restoration Completed Successfully",Toast.LENGTH_LONG).show();
                }
            }catch (Exception ex)
            {
                Log.e("restore error",ex.toString());
            }

        }
    }
    private boolean checkPermission(String permission){
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            if (result == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void requestPermission(String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale(setting_backup.this, permission)){
            Toast.makeText(getApplicationContext(), "Get account permission allows us to get your email",
                    Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(setting_backup.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getEmails();
                } else {
                    Toast.makeText(getApplicationContext(),"Permission Denied.",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void getEmails() {
      pickUserAccount();
    }


    public void pickUserAccount() {
        /*This will list all available accounts on device without any filtering*/

        Intent intent = newChooseAccountIntent(null, null,
                new String[] {"com.google", "com.google.android.legacyimap"}, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }
    /*After manually selecting every app related account, I got its Account type using the code below*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
               Log.e("Account Name",data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                selected_account=data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (restore)
                     dialogButton.setText(selected_account);

                final stock_sym istok = new stock_sym(this);
                istok.setDbSync(1);
                Log.e("synced",String.valueOf(istok.isSync()));

                SharedPreferences sharedpreferences = getSharedPreferences("stockfolio", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedpreferences.edit();
                editor.putString("sync_account",data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                editor.commit();

                //all GOOD.. TIME TO upload data to cloud using EMAIL address

                String url="https://stockmate.gradelogics.com/api/syncdb";

                sharedpreferences = getSharedPreferences("stockfolio", Context.MODE_PRIVATE);
                String sync_account=sharedpreferences.getString("sync_account","-");

                if(!sync_account.equals("-") && !restore) {
                   // final stock_sym istok = new stock_sym(getApplicationContext());
                    if (istok.all_buy_history().size()>0)
                       new syncDB().execute(sync_account);
                }

            } else if (resultCode == RESULT_CANCELED) {
                //Toast.makeText(this, R.string.pick_account, Toast.LENGTH_LONG).show();
            }
        }
    }


    private class syncDB extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {
            String result="";
            String sync_account=params[0];
            try {

                final stock_sym istok = new stock_sym(getApplicationContext());
                Gson gson=new Gson();

                ArrayList<stock_sync> syncArray=new ArrayList<stock_sync>();


                for (stock_sym stok:istok.all_buy_history())
                {
                    stock_sync syn=new stock_sync();
                    syn.sym_name=stok.sym_name;
                    syn.buy_qty=stok.buy_qty;
                    syn.buy_price=stok.buy_price;
                    syn.buy_fee=stok.buy_fee;
                    syn.buy_date=stok.buy_date;
                    syn.buy_total=stok.buy_total;

                    syncArray.add(syn);
                }

                String sArray=gson.toJson(syncArray);
                JsonArray jsonArray = new JsonParser().parse(sArray).getAsJsonArray();

                Log.e("local url","https://stockmate.gradelogics.com/api/syncdb?sArray=" + sArray + "&email="+sync_account);
                URL url=new URL("https://stockmate.gradelogics.com/api/syncdb?sArray=" + sArray + "&email="+sync_account);
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

            Log.e("json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            Log.e("response",res);

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

        }
        return (super.onOptionsItemSelected(menuItem));
    }
}

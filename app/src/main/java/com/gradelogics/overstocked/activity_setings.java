package com.gradelogics.overstocked;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class activity_setings extends rootAnimator {


    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setings);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
          actionBar.setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#46B7F8"));
        }

        sharedPreferences = getSharedPreferences("stockfolio", Context.MODE_PRIVATE);
        boolean isLock=sharedPreferences.getBoolean("isLock",false);

        final stock_sym istok = new stock_sym(this);
        TextView optbackup=(TextView)findViewById(R.id.opt_backup);
        final TextView appinfo=(TextView)findViewById(R.id.opt_info) ;
        TextView contactus=(TextView)findViewById(R.id.opt_contact);
        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.stockmateja.com/contact";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        final TextView txtshare=(TextView)findViewById(R.id.opt_share) ;
        txtshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody="Hi,\n\nI'm using Stockmate.\n\nStockmate Portfolio app is a quick and convenient way to track and manage your JSE stock portfolio.\n\nGet it free on the <a href=\"https://play.google.com/store/apps/details?id=com.gradelogics.overstocked\">Google Play Store</a>";
                String shareSubject="Stockmate Stock Portfolio Manager: Android + iPhone";

                intent.putExtra(Intent.EXTRA_SUBJECT,shareSubject);
                intent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(intent,"Share using"));
            }
        });

        appinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),app_info.class);
                startActivity(intent);
            }
        });

        optbackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent (getApplicationContext(),setting_backup.class);
                startActivity(intent);
            }
        });

        Switch simpleSwitch = (Switch) findViewById(R.id.opt_switch);

        if (isLock) {
            simpleSwitch.setChecked(true);
        } else
            simpleSwitch.setChecked(false);

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {

                    Log.e("switch", "checked");
                   // istok.setIsLock(1);
                    SharedPreferences.Editor editor;
                    editor = sharedPreferences.edit();
                    editor.putBoolean("isLock",true);
                    editor.commit();

                } else {
                    SharedPreferences.Editor editor;
                    editor = sharedPreferences.edit();
                    editor.putBoolean("isLock",false);
                    editor.commit();
                    Log.e("switch", "ucheck");
                }
            }
        });

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

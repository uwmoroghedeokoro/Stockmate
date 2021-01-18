package com.gradelogics.overstocked;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;

public class app_info extends rootAnimator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("App Info");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#46B7F8"));
        }
        TextView txtversion=(TextView)findViewById(R.id.txt_version);
        TextView txtrights=(TextView)findViewById(R.id.txt_rights);

        Calendar calendar=Calendar.getInstance();
       // int year=Calendar.get(Calendar.YEAR);
        txtrights.setText("© 2019-" + calendar.get(Calendar.YEAR) + " DigitalStorm Designs Inc.");


        try {
            PackageInfo pInfo = getApplication().getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            txtversion.setText("Version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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

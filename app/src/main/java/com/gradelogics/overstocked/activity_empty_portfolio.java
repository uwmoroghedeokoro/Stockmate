package com.gradelogics.overstocked;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;

public class activity_empty_portfolio extends rootAnimator {

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        Button add_btn=(Button)findViewById(R.id.add_btn);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Stockmate");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
      //  actionBar.setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#46B7F8"));
        }

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activity_add_buy.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        stock_sym istok=new stock_sym(getApplicationContext());
        if (istok.my_symbols().size()>0) {
            // welcomelayout.setVisibility(View.VISIBLE);
            Intent intent=new Intent(getApplicationContext(),NewMain.class);
            startActivity(intent);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if (id==R.id.action_mysettings){
            Intent intent=new Intent(getApplicationContext(),activity_setings.class);
            Log.e("option","settings");
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}

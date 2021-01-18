package com.gradelogics.overstocked;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class activity_splash_3 extends rootAnimator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        final SharedPreferences sharedPreferences=getSharedPreferences("stockfolio",Context.MODE_PRIVATE);
        //String agreed=sharedPreferences.getString("agreed","0");

        Button btngo=(Button)findViewById(R.id.btn_go);
        btngo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();


               // editor.putString("agreed","1");
              //  editor.commit();

                Intent intent=new Intent(getApplicationContext(),splash4.class);
                startActivity(intent);
            }
        });
    }
}

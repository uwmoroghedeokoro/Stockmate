package com.gradelogics.overstocked;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

public class enter_passcode extends rootAnimator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_passcode);


        getWindow().setStatusBarColor(Color.parseColor("#46B7F8"));

        Button btn=(Button)findViewById(R.id.pass_code_btn);
        final EditText passcode=(EditText)findViewById(R.id.pass_code);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass_code=passcode.getText().toString();

                if(!pass_code.equals(""))
                {
                    stock_sym stk=new stock_sym(getApplicationContext());
                    if (stk.pass_code_true(pass_code)) {
                        SharedPreferences sharedPreferences=getSharedPreferences("stockfolio",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        Calendar calendar = Calendar.getInstance();
                        long timemilli=calendar.getTimeInMillis();

                        editor.putBoolean("islocked",false);
                        editor.commit();
                        finish();
                    }
                }
            }
        });
    }
}

package com.gradelogics.overstocked;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class privacy_activity extends rootAnimator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Privacy Settings");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
                    stk.save_pass_code(pass_code);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                // startActivityAfterCleanup(MainActivity.class);
                finish();
                return true;

        }
        return (super.onOptionsItemSelected(menuItem));
    }
}

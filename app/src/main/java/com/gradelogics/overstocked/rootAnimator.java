package com.gradelogics.overstocked;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gradelogics.overstocked.R;

public class rootAnimator extends AppCompatActivity {
    int onStartCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nolayout);
         onStartCount = 1;

        if (savedInstanceState == null) // 1st time
        {
            this.overridePendingTransition(R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left);
        } else // already created so reverse animation
        {
            onStartCount = 2;
        }
    }
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_right);

        } else if (onStartCount == 1) {
            onStartCount++;
           
        }

    }
}

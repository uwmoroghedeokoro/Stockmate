package com.gradelogics.overstocked;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.KEYGUARD_SERVICE;

public class PhoneUnlockReceiver extends BroadcastReceiver {

    SharedPreferences sharedpreferences;
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            //phone lock
            Log.e("phone status","LOCKED");
            sharedpreferences = context.getSharedPreferences("stockfolio", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedpreferences.edit();
            editor.putBoolean("islocked",true);
            editor.commit();

            Intent minintent=new Intent(context,NewMain.class);
           // context.startActivity(minintent);


        }else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            Log.e("phone status","UNLOCKED");
            sharedpreferences = context.getSharedPreferences("stockfolio", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedpreferences.edit();
            editor.putBoolean("islocked",false);
          //  editor.commit();
        }

    }
}

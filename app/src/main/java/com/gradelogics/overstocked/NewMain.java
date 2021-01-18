package com.gradelogics.overstocked;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewMain extends rootAnimator {

    private dbHelper db_helper;
    //   private SQLiteDatabase db;
    private RecyclerView listview_ticker;
    stock_sym stocki;
    LinearLayout welcomelayout;
    SharedPreferences sharedpreferences;
    MyListAdapter adapter;
    LinearLayout icofilter,filterlay;
    TextView filtertext;
    NotificationCompat.InboxStyle inboxStyle;
    PhoneUnlockReceiver mReceiver = new PhoneUnlockReceiver();
    float x1, x2, y1, y2=0;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        sharedpreferences = getSharedPreferences("stockfolio", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();
        editor.putLong("session_start",Calendar.getInstance().getTimeInMillis());
        editor.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

         viewPager = (ViewPager) findViewById(R.id.view_pager);
         tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);


      //  setSupportActionBar(toolbar);

       // actionBar.setTitle("Stockmate");

        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_UNLOCKED);
        registerReceiver(
                mReceiver,filter);

        SharedPreferences sharedPreferences = getSharedPreferences("stockfolio", Context.MODE_PRIVATE);
        String agreed = sharedPreferences.getString("agreed", "0");
        Log.e("agreed",agreed);
       // agreed="0";
        if (agreed.equals("0")) {
            Intent intent = new Intent(getApplicationContext(), activitysplash.class);
            startActivity(intent);
        }

else {
            Log.e("service state", String.valueOf(isMyServiceRunning(bg_service.class)));
            if (isMyServiceRunning(bg_service.class) == false) {
                startService(new Intent(NewMain.this, bg_service.class));
            }

            setupViewPager(viewPager);
        }
    }


    void alert_notify()
    {
        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)

        int currentHourIn12Format = rightNow.get(Calendar.HOUR);
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        Log.e("hr",String.valueOf(currentHourIn24Format));
        // if (currentHourIn24Format==17)
        {
            stock_sym istok = new stock_sym(getApplicationContext());
            Context cx=getApplicationContext();
            stock_sym.portfolio portfolio = istok.portfolio_position();
            NumberFormat format = NumberFormat.getCurrencyInstance();

            final String NOTIFICATION_CHANNEL_ID = "channel_id_3";
//Notification Channel ID passed as a parameter here will be ignored for all the Android versions below 8.0
            NotificationCompat.Builder builder = new NotificationCompat.Builder(cx, NOTIFICATION_CHANNEL_ID);
            builder.setContentTitle("Stock Price Alerts ");
            builder.setContentText("The below stocks require your attention ");
            builder.setSmallIcon(R.drawable.stockmate_sm);
            builder.setLargeIcon(BitmapFactory.decodeResource(cx.getResources(), R.drawable.stockmate));
            // builder.setStyle(new NotificationCompat.InboxStyle());
            builder.setStyle(inboxStyle);

            // Unique identifier for notification
            final int NOTIFICATION_ID = 103;
            //This is what will will issue the notification i.e.notification will be visible

            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

            Intent intent = new Intent(cx, alert_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(cx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//Following will set the tap action
            builder.setContentIntent(pendingIntent);

            Notification notification = builder.build();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(cx);


            // This is the Notification Channel ID. More about this in the next section
            //final String NOTIFICATION_CHANNEL_ID = "channel_id";
//User visible Channel Name
            final String CHANNEL_NAME = "Notification Channel";
// Importance applicable to all the notifications in this Channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//Notification channel should only be created for devices running Android 26

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);
                //Boolean value to set if lights are enabled for Notifications from this Channel
                notificationChannel.enableLights(true);
                //Boolean value to set if vibration are enabled for Notifications from this Channel
                notificationChannel.enableVibration(true);
                //Sets the color of Notification Light
                notificationChannel.setLightColor(Color.GREEN);
                //Set the vibration pattern for notifications. Pattern is in milliseconds with the format {delay,play,sleep,play,sleep...}
                notificationChannel.setVibrationPattern(new long[]{
                        500,
                        500,
                        500,
                        500,
                        500
                });
                //Sets whether notifications from these Channel should be visible on Lockscreen or not
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                NotificationManager notificationManager = (NotificationManager) cx.getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(notificationChannel);
                }


            }


            notificationManagerCompat.notify(NOTIFICATION_ID, notification);

            // Log.e("notify", "me");
        }
    }


    private boolean check_alerts(stock_sym symbolclass)
    {
        boolean callalert=false;
        try
        {
            NumberFormat format = NumberFormat.getCurrencyInstance();
            SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("stockfolio",Context.MODE_PRIVATE);


            stock_sym stockclass=new stock_sym(getApplicationContext());
            ArrayList<stock_sym.alertz>s_alerts = stockclass.my_alerts();
            // Log.e("alert sym",symbolclass.sym_name);
            for (stock_sym.alertz stockalert:s_alerts) {
                String stockid=sharedPreferences.getString(String.valueOf(stockalert.id),"0");
                if(stockid.equals("0"))
                {
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    //alert not yet met, let's check

                    if(stockalert.symbol.equals(symbolclass.sym_name))
                    {

                        if(stockalert.criteria.equals("Above")){
                            //price above
                            if (symbolclass.current_price>stockalert.price){
                                //price is above target price// call alert
                                inboxStyle.addLine(symbolclass.sym_name + " (" + format.format(symbolclass.current_price) + ") has gone above " + format.format(stockalert.price));
                                callalert=true;
                                Log.e("Stock  alert " + symbolclass.sym_name, "has gone above " + String.valueOf(stockalert.price));
                                editor.putString(String.valueOf(stockalert.id),"1");
                                editor.commit();
                            }
                        }else{
                            if (symbolclass.current_price<stockalert.price){
                                //price is above target price// call alert
                                inboxStyle.addLine(symbolclass.sym_name + " (" + format.format(symbolclass.current_price) + ") has fallen below " + format.format(stockalert.price));
                                callalert=true;
                                Log.e("Stock  alert " + symbolclass.sym_name, "has fallen below " + String.valueOf(stockalert.price));

                                editor.putString(String.valueOf(stockalert.id),"1");
                                editor.commit();
                            }
                        }
                    }
                }
            }


        }catch (Exception ex)
        {
            Log.e("Alertcheck Ex",ex.getMessage().toString());
        }
        finally {
            return callalert;
        }
    }

    private  void pass_code_check()
    {

        //check if session expired

        long last_session_start=sharedpreferences.getLong("session_start",0);

      //  sharedPreferences = getSharedPreferences("stockfolio", Context.MODE_PRIVATE);
        boolean isLock=sharedpreferences.getBoolean("isLock",false);

        boolean locked=sharedpreferences.getBoolean("islocked",false);
        stock_sym stk = new stock_sym(getApplicationContext());
        Log.e("lock",String.valueOf(locked));
        if(locked  && isLock) {

            //if (stk.pass_code_exist())
            {
                // Intent intent = new Intent(getApplicationContext(), enter_passcode.class);
                // startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

                    if (km.isKeyguardSecure()) {
                        Intent authIntent = km.createConfirmDeviceCredentialIntent("Stockmate Portfolio Protected", "Enter your phone's PIN to gain access to your portfolio");
                        startActivityForResult(authIntent, 1);
                    }
                }
            }
        }
    }

    public void onResume() {

        super.onResume();
        Log.e("StockD","back to Main");
        // new sync_7_days().execute("");
        //  new sync_symbols_price().execute("");

        stock_sym stk = new stock_sym(getApplicationContext());

        pass_code_check();
        boolean locked=sharedpreferences.getBoolean("islocked",false);
        if(stk.isLock()) {
            if (!locked) {

                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.putLong("session_start", Calendar.getInstance().getTimeInMillis());
                edit.commit();
             //   setupViewPager(viewPager);
                //load_portfolio();
                // load_all_syms();
                // buildGraph();
            }
        }else
        {
            SharedPreferences.Editor edit = sharedpreferences.edit();
            edit.putLong("session_start", Calendar.getInstance().getTimeInMillis());
            edit.commit();
//            setupViewPager(viewPager);
        }


        // new sync_symbols().execute("");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode ==1) {
            if (resultCode == RESULT_OK) {
                //do something you want when pass the security
                SharedPreferences.Editor editor=sharedpreferences.edit();
                editor.putBoolean("islocked",false);
                editor.commit();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.e("action",String.valueOf(id));
        //noinspection SimplifiableIfStatement
      if (id==R.id.action_mysettings){
            Intent intent=new Intent(getApplicationContext(),activity_setings.class);
            Log.e("option","settings");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainFragment(), "Portfolio");
        adapter.addFragment(new StockFragment(), "JSE Live");
        adapter.addFragment(new fragment_analysis(), "Analysis");
        adapter.addFragment(new PortfolioDiversityFragment(), "Diversity");
        adapter.addFragment(new alertsFragment(),"Alerts");
        viewPager.setAdapter(adapter);

     //   tabLayout.getTabAt(4).setIcon(R.drawable.icons8_alarm_24);

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {

            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}

package com.gradelogics.overstocked;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Alarm extends BroadcastReceiver
{
    Context cx;
    NotificationCompat.InboxStyle inboxStyle; //new NotificationCompat.InboxStyle()
    NumberFormat format = NumberFormat.getCurrencyInstance();
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        // PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        // wl.acquire();
          cx=context;
        inboxStyle= new NotificationCompat.InboxStyle();
        new sync_symbols_price().execute("");
        //   notify_me();
        // Put here YOUR code.
        //  notify_me(context);
        //Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example
        //  Log.e("alarm","ALRAM!!!");
        // wl.release();
    }

    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1200000, pi); // Millisec * Second * Minute
    }


    private class sync_symbols_price extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        boolean call_alerts=false;
        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,schterm="";
                int studentid=0;
               // SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
                Gson gson = new Gson();
              //  String json = prefs.getString("student_object", "");
                // student stuobj = gson.fromJson(json, student.class);
                Log.e("Alram trigger","sync price");

                URL url=new URL("https://stockmate.glazedev.com/api/stocks/current");
                // Log.e("url",url.toString());
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

            //  Log.e(" grades json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            // final ArrayList<transaction> dataSet=new ArrayList<transaction>();
            try {


                JSONArray jArray=new JSONArray(res);
               // SharedPreferences.Editor editor=getSharedPreferences("overstockd",MODE_PRIVATE).edit();
                stock_sym stockSym=new stock_sym(cx);
               // stockSym.purge_symbols();
                for (int x=0;x<jArray.length();x++)
                {
                    // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject jObj=jArray.getJSONObject(x);

                    String[]dateS=jObj.getString("import_date").split("\\s+");
                    String dateInString = jObj.getString("import_date");//;dateS[0];  // Start date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date c = Calendar.getInstance().getTime();
                    String date = sdf.format(c);
                   //  Log.e("alarm check: " + jObj.getString("symbol"),dateInString);

                    if (stockSym.getSymExist_withDate(jObj.getString("symbol"),dateInString) == 0) {
                        stockSym.sym_name = jObj.getString("symbol");
                        stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("last_trade_price")).floatValue();
                        stockSym.import_date=dateInString;
                        stockSym.add_sym_to_db_2(false);
                      //  Log.e("alarm check result","no match");
                        // stockSym.add_sym_to_db_simple(false);
                        // Log.e("stockd","add sym to db: " +  stockSym.sym_name + " date:" + dateInString);
                    }else
                    {
                        stockSym.sym_name = jObj.getString("symbol");
                        stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("last_trade_price")).floatValue();
                        //Log.e(stockSym.sym_name,String.valueOf(jObj.getDouble("last_trade_price")));
                        stockSym.import_date=dateInString;;
                        stockSym.add_sym_to_db_2(true);
                       // Log.e("alarm check result","found");
                        // stockSym.add_sym_to_db_simple(true);
                        // Log.e("stockd","update db: " +  stockSym.sym_name + " date:" + dateInString);
                    }


                    if (stockSym.getSymExist(jObj.getString("symbol")) == 0) {
                        stockSym.sym_name = jObj.getString("symbol");
                        stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("last_trade_price")).floatValue();
                        // stockSym.import_date=dateInString;
                        // stockSym.add_sym_to_db(false);
                        stockSym.add_sym_to_db_simple(false);
                        // Log.e("stockd","add sym to db: " +  stockSym.sym_name + " date:" + dateInString);
                    }else
                    {
                        stockSym.sym_name = jObj.getString("symbol");
                        stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("last_trade_price")).floatValue();
                        stockSym.import_date=dateInString;;
                        // stockSym.add_sym_to_db(true);
                        stockSym.add_sym_to_db_simple(true);
                        //  Log.e("stockd","update db: " +  stockSym.sym_name + " date:" + dateInString);
                    }


                    //check for price alerts
                   if (check_alerts(stockSym))
                       call_alerts=true;

                }
                // Log.e("sch json",schObject.toString());


            }catch (Exception e)
            {

            }finally {
                // loadUI();
                //check stock alerts
                notify_me();
                if (call_alerts)
                    alert_notify();;

            }
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
            stock_sym istok = new stock_sym(cx);
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
            SharedPreferences sharedPreferences=cx.getSharedPreferences("stockfolio",Context.MODE_PRIVATE);


            stock_sym stockclass=new stock_sym(cx);
            ArrayList<stock_sym.alertz>s_alerts = stockclass.my_alerts();

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
                                editor.putString(String.valueOf(stockalert.id),"1");
                                editor.commit();
                            }
                        }else{
                            if (symbolclass.current_price<stockalert.price){
                                //price is above target price// call alert
                                inboxStyle.addLine(symbolclass.sym_name + " (" + format.format(symbolclass.current_price) + ") has fallen below " + format.format(stockalert.price));
                                callalert=true;
                                editor.putString(String.valueOf(stockalert.id),"1");
                                editor.commit();
                            }
                        }
                    }
                }
            }


        }catch (Exception ex)
        {

        }
        finally {
            return callalert;
        }
    }

    void notify_me()
    {
        //check if close of day
        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
        int dayOfweek=rightNow.get(Calendar.DAY_OF_WEEK);

        int currentHourIn12Format = rightNow.get(Calendar.HOUR);
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

//Log.e("hr",String.valueOf(currentHourIn24Format));
        if (currentHourIn24Format==17 && (dayOfweek>1&& dayOfweek<7))
        {
            Log.e("notify","yes");


            SharedPreferences sharedpreferences = cx.getSharedPreferences("stockfolio", Context.MODE_PRIVATE);
            String sync_account=sharedpreferences.getString("sync_account","-");

            stock_sym istok = new stock_sym(cx);
            if(!sync_account.equals("-")) {
                if (istok.all_buy_history().size()>0)
                    new syncDB().execute(sync_account); //backup portfolio to cloud
            }


            stock_sym.portfolio portfolio = istok.portfolio_position();
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

            final String NOTIFICATION_CHANNEL_ID = "channel_id";
//Notification Channel ID passed as a parameter here will be ignored for all the Android versions below 8.0
            NotificationCompat.Builder builder = new NotificationCompat.Builder(cx, NOTIFICATION_CHANNEL_ID);
            builder.setContentTitle("Daily portfolio summary: " + date);
            builder.setContentText("Here is your summary at today's closing bell!");
            builder.setSmallIcon(R.drawable.stockmate_sm);
            builder.setLargeIcon(BitmapFactory.decodeResource(cx.getResources(), R.drawable.stockfolio_ico));
            builder.setStyle(new NotificationCompat.InboxStyle()
                    .addLine("Portfolio Value: " + format.format(portfolio.total_value))
                    .addLine("OVR Gain: " + format.format(portfolio.net_pos))
                    .addLine("Today: " + sharedpreferences.getString("daily_change","$0.00")));

            // Unique identifier for notification
            final int NOTIFICATION_ID = 101;
            //This is what will will issue the notification i.e.notification will be visible

            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

            Intent intent = new Intent(cx, NewMain.class);
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

            Log.e("notify", "me");
        }

    }
    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }


    public class syncDB extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {
            String result="";
            String sync_account=params[0];
            try {

                final stock_sym istok = new stock_sym(cx);
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

                //Log.e("local jsonarray",jsonArray.toString());
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

            // Log.e("json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            Log.e("Sync DB","Completed");

        }
    }
}

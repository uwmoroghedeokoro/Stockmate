package com.gradelogics.overstocked;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public  class stock_sym
{

    public String sym_name;
    public int buy_qty;
    public float buy_price;
    public float buy_fee;
    public long buy_date;
    public  float buy_total;
    public float current_price;
    public float change;
    public float high;
    public  float low;
    public  float change_perc;
   // public details sym_details;
    public  String import_date;
    public  int id;
    public boolean watch;
    public details sym_details=new details();


private Context conx;
    private dbHelper db_helper;
    private SQLiteDatabase db;

    public stock_sym(Context cx)
    {
        db_helper=new dbHelper(cx);
        conx=cx;

        db=db_helper.getWritableDatabase();

       // db.execSQL("drop table tbl_privacy");
        try {
            db.execSQL("alter table tbl_privacy ADD Column isSync INTEGER default 0;");
        }catch (Exception ex)
        {

        }

        try {
            db.execSQL("alter table tbl_privacy ADD Column isLock INTEGER default 0;");
        }catch (Exception ex)
        {

        }
      //  db.execSQL("create table tbl_sym_alert (symName text PRIMARY KEY, alert_value Float DEFAULT 0.00);");
       // db.execSQL("drop table tbl_sym_alert");
       // db.execSQL("create table tbl_sym_alert (ID INTEGER PRIMARY KEY AUTOINCREMENT,symName text, alert_value Float DEFAULT 0.00,alert_target text);");

        // db.execSQL("create table tbl_syms_simple (symName text, lastPrice Float DEFAULT 0.00);");

        sym_details=new details();
        try {
        // Log.e("TAG","finish class constructor");
        } catch (SQLiteException ex) {
            Log.w(TAG, "Altering tbl_syms_simple: " + ex.getMessage());
        }

    }

public stock_sym()
{

}

public  void clear_portfolio()
{
    Log.e("Clearing","Clear portfolio");
    db.execSQL("delete from tbl_stock_buys;");
}

    public void add_sym_alert(float alert_value,String target)
    {
        ContentValues values=new ContentValues();
        values.put("symName",sym_name);
        values.put("alert_value",alert_value);
        values.put("alert_target",target);

        try {
              db.replace("tbl_sym_alert", null,values);
              //  Log.e("insert replace tbl_syms", sym_name);

        }catch (Exception ex)
        {
            //Log.e("stockd - add sym to db",ex.getMessage());
            // db.close();;
        }
        finally {
            // db.close();
        }
    }


    public void save_pass_code(String pcode)
    {
        ContentValues values=new ContentValues();

        values.put("pass_code",md5(pcode));


        try {
            db.replace("tbl_privacy", null,values);
            //  Log.e("insert replace tbl_syms", sym_name);

        }catch (Exception ex)
        {
            //Log.e("stockd - add sym to db",ex.getMessage());
            // db.close();;
        }
        finally {
            // db.close();
        }
    }

    public void setDbSync(int opt)
    {
        // db = db_helper.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("isSync",opt);

        try {
            db.replace("tbl_privacy",null, values);
             Log.e("update privacy", String.valueOf(opt));

        }catch (Exception ex)
        {
            //Log.e("stockd - add sym to db",ex.getMessage());
            // db.close();;
        }
        finally {
            // db.close();
        }


    }

    public void setIsLock(int opt)
    {
        // db = db_helper.getWritableDatabase();

      //  ContentValues values=new ContentValues();
       // values.put("isLock",opt);

        try {
            db.execSQL("update tbl_privacy set isLock="+String.valueOf(opt));
          //  db.rawQuery("update tbl_privacy set isLock="+opt,null);
          //  Cursor c=db.rawQuery("update tbl_privacy set isLock=" + opt,null);
          //  c.moveToFirst();
          //  c.close();
            Log.e("update privacy", String.valueOf(opt));

        }catch (Exception ex)
        {
            Log.e("update lock",ex.getMessage());
            // db.close();;
        }
        finally {
            // db.close();
        }


    }
    public boolean isSync()
    {
        // db = db_helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        Cursor res=db.rawQuery("select isSync from tbl_privacy where isSync=1",null);
        res.moveToFirst();
        boolean syncdb=false;

        while(res.isAfterLast()==false)
        {

            syncdb=true;

            res.moveToNext();
        }

        res.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        //  db.close();

        return syncdb;
    }

    public boolean isLock()
    {
        // db = db_helper.getWritableDatabase();
        boolean syncdb=false;

        try {
            db.beginTransactionNonExclusive();
            Cursor res = db.rawQuery("select isLock from tbl_privacy where isLock=1", null);
            res.moveToFirst();

            while (res.isAfterLast() == false) {

                syncdb = true;

                res.moveToNext();
            }

            res.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            //  db.close();
        }catch (SQLiteException x)
        {

        }
        return syncdb;
    }

    public boolean pass_code_true(String pcode)
    {
        // db = db_helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        Cursor res=db.rawQuery("select pass_code from tbl_privacy where pass_code=?",new String[]{md5(pcode)});
        res.moveToFirst();
        boolean pass_code_true=false;

        while(res.isAfterLast()==false)
        {

            pass_code_true=true;

            res.moveToNext();
        }

        res.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        //  db.close();

        return pass_code_true;
    }

    public boolean pass_code_exist()
    {
        // db = db_helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        Cursor res=db.rawQuery("select pass_code from tbl_privacy",null);
        res.moveToFirst();
        boolean pcode=false;

        while(res.isAfterLast()==false)
        {

            pcode=true;

            res.moveToNext();
        }

        res.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        //  db.close();

        return pcode;
    }


    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void remove_buy_order(int rec)
    {
        db.delete("tbl_stock_buys", "ID=?",new String[]{String.valueOf(rec)});

        //syncDB
        SharedPreferences prefs=conx.getSharedPreferences("stockfolio",MODE_PRIVATE);
        String sync_account=prefs.getString("sync_account","-");

        stock_sym istok = new stock_sym(conx);
        if(!sync_account.equals("-")) {
              if (istok.all_buy_history().size()>0)
            new syncDB().execute(sync_account); //backup portfolio to cloud
        }
    }

    public void update_watcher(int opt)
    {
        try {
        ContentValues values=new ContentValues();
       // values.put("symName",sym_name);
        values.put("watch",1);
      //  values.put("import_date",import_date);

            db.execSQL("update tbl_syms_simple set watch="+ opt + " where symName=?",new String[]{sym_name});
       // c.moveToFirst();
      //  c.close();
        //  db.update("tbl_syms_simple", values, "symName=?", new String[]{sym_name});
        Log.e("update","update tbl_syms_simple set watch=1 where symName='BIL'");
        }catch (SQLiteException ex)
        {
            Log.e("error",ex.getMessage());
        }
    }

    public void add_sym_to_db(ArrayList<stock_sym> items)
    {

        //also fix sync price function in service and MAIN ACtivuy to batch update
       //SQLiteDatabase db2=db_helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {


            Log.e("action","start batch insert");

            for(stock_sym itm:items)
            {
                ContentValues values=new ContentValues();
                values.put("symName",itm.sym_name);
                values.put("lastPrice",itm.current_price);
                values.put("import_date",itm.import_date);

                ContentValues values_simple=new ContentValues();
                values_simple.put("symName",itm.sym_name);
                values_simple.put("lastPrice",itm.current_price);
              //  Log.e("import_date",itm.import_date);

                String[]dateS=itm.import_date.split("\\s+");
                String dateInString = dateS[0];  // Start date

                //   db.query()
                if (getSymExist_withDate(itm.sym_name,dateInString) == 0)  {
                   // db.insert("tbl_syms", null, values);
                    db.execSQL("INSERT INTO tbl_syms (symName,lastPrice,import_date) VALUES (?,?,?)",new String[]{itm.sym_name,String.valueOf(itm.current_price),itm.import_date});
                   // db.insert("tbl_syms_simple", null, values_simple);
                    Log.e("no date match", dateInString);
                }
                else {
                   // db.update("tbl_syms", values, "symName=? and import_date=?", new String[]{sym_name,import_date});
                    db.execSQL("update tbl_syms set lastPrice=" + itm.current_price + " where symName=? and date(import_date)=?", new String[]{itm.sym_name,dateS[0]});
                   // db.update("tbl_syms_simple", values_simple, "symName=?", new String[]{sym_name});
                    // Log.e("update tbl_syms", sym_name);
                }
            }

           // Log.e("stocks","about to insert sym");

          //  Log.e("action","END batch insert");
            db.setTransactionSuccessful();
        }catch (Exception ex)
        {
            Log.e("stockd - add sym to db",ex.getMessage());
           // db.close();;
        }
        finally {

            db.endTransaction();
           // db2.close();
        }
    }

    public void add_sym_to_db_2(boolean update)
    {
        ContentValues values=new ContentValues();
        values.put("symName",sym_name);
        values.put("lastPrice",current_price);
         values.put("import_date",import_date);

        String[]dateS=import_date.split("\\s+");

        db.beginTransactionNonExclusive();
        try {

            //Log.e("stocks","about to insert sym for datez " + import_date);

            //   db.query()
            if (!update) {
                db.insert("tbl_syms", null, values);
                //  Log.e("insert tbl_syms_simple", sym_name);
            }
            else {
                db.delete("tbl_syms", "symName=? and date(import_date)=?", new String[]{sym_name,dateS[0]});
                db.insert("tbl_syms", null, values);
              //  db.update("tbl_syms", values, "symName=? and date(import_date)=?", new String[]{sym_name,dateS[0]});
                //  Log.e("update tbl_syms_simple", sym_name);
            }

            db.setTransactionSuccessful();
        }catch (Exception ex)
        {
            //  Log.e("stockd - sym todbsimple",ex.getMessage());
            // db.close();;
        }
        finally {
            // db.close();
            db.endTransaction();
        }
    }


    public void purge_symbols()
    {
        ContentValues values=new ContentValues();
        values.put("symName",sym_name);
        values.put("lastPrice",current_price);
        // values.put("import_date",import_date);
        db.beginTransactionNonExclusive();
        try {

           // db.execSQL("delete from tbl_syms_simple");
            db.execSQL("delete from tbl_syms");
            Log.e("The purge","delete all syms");
            db.setTransactionSuccessful();
        }catch (Exception ex)
        {
            //  Log.e("stockd - sym todbsimple",ex.getMessage());
            // db.close();;
        }
        finally {
            // db.close();
            db.endTransaction();
        }
    }
    public void add_sym_to_db_simple(boolean update)
    {
        ContentValues values=new ContentValues();
        values.put("symName",sym_name);
        values.put("lastPrice",current_price);
       // values.put("import_date",import_date);
db.beginTransactionNonExclusive();
        try {
           // Log.e("stocks","about to insert sym");

            //   db.query()
            if (!update) {
                db.insert("tbl_syms_simple", null, values);
              //  Log.e("insert tbl_syms_simple", sym_name);
            }
            else {
                db.update("tbl_syms_simple", values, "symName=?", new String[]{sym_name});
              //  Log.e("update tbl_syms_simple", sym_name);
            }


         //   db.insert("tbl_syms_simple", null, values);
            db.setTransactionSuccessful();
        }catch (Exception ex)
        {
          //  Log.e("stockd - sym todbsimple",ex.getMessage());
            // db.close();;
        }
        finally {
            // db.close();
            db.endTransaction();
        }
    }

    public  class alertz
    {

        public int id;
        public String symbol,criteria;
        public float price;

        public alertz()
        {

        }

        public void del()
        {
            db.delete("tbl_sym_alert", "ID=?",new String[]{String.valueOf(id)});
        }
    }

public class portfolio
{
    public float total_value,perc_pos,net_pos,total_return,total_invest;
public long last_update;
    public portfolio()
    {

    }
}


public class details
{
    public int share_count;
    public float diversity,equity,avg_cost,total_return,current_price,total_invest,change_in_price;

    public details(){

    }

}

    public void record_sell(int units_sold)
    {

       int units_to_sell=units_sold;
       boolean sell_complete=false;
     //   db = db_helper.getWritableDatabase();
        db.beginTransactionNonExclusive();

        Cursor res=db.rawQuery("select ID,buyQty from tbl_stock_buys  where symName=? order by buyPrice ASC",new String[] {sym_name});
        res.moveToFirst();
        //Log.e("sql",datestring);

        while(res.isAfterLast()==false && units_to_sell>0)
        {
            int buyQty=res.getInt(res.getColumnIndex("buyQty"));
            if  (buyQty<units_to_sell)
            {
                //Update id set buyQty=0
                update_buy_order(res.getInt(res.getColumnIndex("ID")),0);
                units_to_sell=units_to_sell-buyQty;
            }
           else {
                buyQty=buyQty-units_to_sell;
                update_buy_order(res.getInt(res.getColumnIndex("ID")),buyQty);
              //  Log.e("sell order","Update " + buyQty);
                units_to_sell = 0;
            }

            res.moveToNext();
        }
        res.close();

        db.setTransactionSuccessful();
        db.endTransaction();
       // db.close();

        //sync_database
        SharedPreferences prefs=conx.getSharedPreferences("stockfolio",MODE_PRIVATE);
        String sync_account=prefs.getString("sync_account","-");

        stock_sym istok = new stock_sym(conx);
        if(!sync_account.equals("-")) {
            //  if (istok.all_buy_history().size()>0)
            new syncDB().execute(sync_account); //backup portfolio to cloud
        }

    }

    public void update_buy_order(int rID,int newBuyQty)
    {
        ContentValues values=new ContentValues();
        values.put("ID",rID);
        values.put("buyQty",newBuyQty);
       // values.put("import_date",import_date);
       // db = db_helper.getWritableDatabase();
        try {
            {
                db.update("tbl_stock_buys", values, "ID=?", new String[]{String.valueOf(rID)});
           }
        }catch (Exception ex)
        {
            // Log.e("stockd - add sym to db",ex.getMessage());
            // db.close();;
        }
        finally {
            // db.close();
        }
    }

public void get_details_byDate(String datestring)
    {
      //  db = db_helper.getWritableDatabase();

        db.beginTransactionNonExclusive();

        //Cursor res=db.rawQuery("select t2.lastPrice as current_price,sum(t1.buyQty*t2.lastPrice) as equity, sum(t1.buyQty) as shares,avg(t1.buyPrice) as avg_cost, (sum(t1.buyQty*t2.lastPrice) -sum((t1.buyQty*t1.buyPrice)+t1.buyFee)) as total_return, round(cast(sum(t1.buyQty) as float)/cast((select sum(t3.buyQty)  from tbl_stock_buys t3) as float),3 )*100 as diversity from tbl_stock_buys t1 inner join tbl_syms t2 on t1.symName=t2.symName where t1.symname=? and import_date=?",new String[] {sym_name,datestring});
        Cursor res=db.rawQuery("select t2.lastPrice as current_price,sum(t1.buyQty*t2.lastPrice) as equity, sum(t1.buyQty) as shares,avg(t1.buyPrice) as avg_cost, (sum(t1.buyQty*t2.lastPrice) -sum((t1.buyQty*t1.buyPrice)+t1.buyFee)) as total_return, round(cast(sum(t1.buyQty) as float)/cast((select sum(t3.buyQty)  from tbl_stock_buys t3) as float),3 )*100 as diversity from tbl_syms t2 left outer join tbl_stock_buys t1 on t1.symName=t2.symName  where t2.symname=? and date(import_date)=?",new String[] {sym_name,datestring});

        if (sym_name=="")
             res=db.rawQuery("select t2.lastPrice as current_price,sum(t1.buyQty*t2.lastPrice) as equity, sum(t1.buyQty) as shares,avg(t1.buyPrice) as avg_cost, (sum(t1.buyQty*t2.lastPrice) -sum((t1.buyQty*t1.buyPrice)+t1.buyFee)) as total_return, round(cast(sum(t1.buyQty) as float)/cast((select sum(t3.buyQty)  from tbl_stock_buys t3) as float),3 )*100 as diversity from tbl_stock_buys t1 inner join tbl_syms t2 on t1.symName=t2.symName where date(import_date)=?",new String[] {datestring});
        res.moveToFirst();
        //Log.e("get details date",sym_name);
        while(res.isAfterLast()==false)
        {
            // stock_sym sym=new stock_sym(conx);
            // sym.sym_name=res.getString(res.getColumnIndex("symName"));
            sym_details.equity=res.getFloat(res.getColumnIndex("equity"));
            sym_details.current_price=res.getFloat(res.getColumnIndex("current_price"));
            sym_details.diversity=res.getFloat(res.getColumnIndex("diversity"));
            sym_details.share_count=(res.getInt(res.getColumnIndex("shares")));
            sym_details.avg_cost=(res.getFloat(res.getColumnIndex("avg_cost")));
            sym_details.total_return=(res.getFloat(res.getColumnIndex("total_return")));
            Log.e("Return for " + datestring,String.valueOf(sym_details.total_return));
            // sym.change=Float.parseFloat("12.90");
            //m_symbols.add(sym);
            res.moveToNext();
        }
        res.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }

    public String last_update()
    {
       // db = db_helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        Cursor res=db.rawQuery("select import_date from tbl_syms order by import_date desc limit 1 ",null);
        res.moveToFirst();
         String lastUpdate="";

        while(res.isAfterLast()==false)
        {

            lastUpdate=res.getString(res.getColumnIndex("import_date"));

            res.moveToNext();
        }

        res.close();
        db.setTransactionSuccessful();
        db.endTransaction();
      //  db.close();

        return lastUpdate;
    }

    private class syncDB extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {
            String result="";
            String sync_account=params[0];
            try {

                final stock_sym istok = new stock_sym(conx);
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
                Log.e("sync url",url.toString());
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


    public void get_details()
{
    Cursor res=db.rawQuery("select cast(t2.lastPrice-t3.lastPrice as float) as change_in_price, t2.lastPrice as current_price,sum(t1.buyQty*t2.lastPrice) as equity,sum((t1.buyQty*t1.buyPrice)+t1.buyFee) as invested, sum(t1.buyQty) as shares,CASE WHEN sum(t1.buyQty>0) then (sum((t1.buyQty*t1.buyPrice)))/sum(t1.buyQty) else 0 END as avg_cost, (sum(t1.buyQty*t2.lastPrice) -sum((t1.buyQty*t1.buyPrice)+t1.buyFee)) as total_return, round(cast(sum(t1.buyQty) as float)/cast((select sum(t3.buyQty)  from tbl_stock_buys t3) as float),3 )*100 as diversity from tbl_stock_buys t1 inner join tbl_syms_simple t2 on t1.symName=t2.symName left outer join (select distinct symName,import_date,lastPrice from tbl_syms where symName=? and import_date< date('now','localtime') order by import_date DESC LIMIT 1) t3 on t1.symName=t3.symName  where t1.symName=?",new String[] {sym_name,sym_name});
    res.moveToFirst();

    while(res.isAfterLast()==false)
    {
        sym_details.equity=res.getFloat(res.getColumnIndex("equity"));
        sym_details.total_invest=res.getFloat(res.getColumnIndex("invested"));
        sym_details.current_price=res.getFloat(res.getColumnIndex("current_price"));
        sym_details.diversity=res.getFloat(res.getColumnIndex("diversity"));
        sym_details.share_count=(res.getInt(res.getColumnIndex("shares")));
        // Log.e( sym_name+ " equity" , String.valueOf(res.getInt(res.getColumnIndex("shares"))));
        sym_details.avg_cost=(res.getFloat(res.getColumnIndex("avg_cost")));
        sym_details.total_return=(res.getFloat(res.getColumnIndex("total_return")));
       /* if (res.isNull(res.getColumnIndex("change_in_price")))
            sym_details.change_in_price=0;
        else
            sym_details.change_in_price=(res.getFloat(res.getColumnIndex("change_in_price")));
*/
        res.moveToNext();
    }
    res.close();
}

    public void get_details_simulated(float sim_price)
    {
        Cursor res=db.rawQuery("select cast(t2.lastPrice-t3.lastPrice as float) as change_in_price, t2.lastPrice as current_price,sum(t1.buyQty*" + sim_price +") as equity,sum((t1.buyQty*t1.buyPrice)+t1.buyFee) as invested, sum(t1.buyQty) as shares,CASE WHEN sum(t1.buyQty>0) then (sum((t1.buyQty*t1.buyPrice)))/sum(t1.buyQty) else 0 END as avg_cost, (sum(t1.buyQty*" + sim_price + ") -sum((t1.buyQty*t1.buyPrice)+t1.buyFee)) as total_return, round(cast(sum(t1.buyQty) as float)/cast((select sum(t3.buyQty)  from tbl_stock_buys t3) as float),3 )*100 as diversity from tbl_stock_buys t1 inner join tbl_syms_simple t2 on t1.symName=t2.symName left outer join (select distinct symName,import_date,lastPrice from tbl_syms where symName=? and import_date< date('now','localtime') order by import_date DESC LIMIT 1) t3 on t1.symName=t3.symName  where t1.symName=?",new String[] {sym_name,sym_name});
        res.moveToFirst();

        while(res.isAfterLast()==false)
        {
            sym_details.equity=res.getFloat(res.getColumnIndex("equity"));
            sym_details.total_invest=res.getFloat(res.getColumnIndex("invested"));
            sym_details.current_price=res.getFloat(res.getColumnIndex("current_price"));
            sym_details.diversity=res.getFloat(res.getColumnIndex("diversity"));
            sym_details.share_count=(res.getInt(res.getColumnIndex("shares")));
            // Log.e( sym_name+ " equity" , String.valueOf(res.getInt(res.getColumnIndex("shares"))));
            sym_details.avg_cost=(res.getFloat(res.getColumnIndex("avg_cost")));
            sym_details.total_return=(res.getFloat(res.getColumnIndex("total_return")));
       /* if (res.isNull(res.getColumnIndex("change_in_price")))
            sym_details.change_in_price=0;
        else
            sym_details.change_in_price=(res.getFloat(res.getColumnIndex("change_in_price")));
*/
            res.moveToNext();
        }
        res.close();
    }


    public portfolio portfolio_position()
    {
        portfolio portfolio_pos=new portfolio();
        //db=db_helper.getWritableDatabase();
        //db = db_helper.getWritableDatabase();
       try {
           db.beginTransactionNonExclusive();

           Cursor res = db.rawQuery("select sum(t1.buyQty*t2.lastPrice) as total_value,sum((t1.buyQty*t1.buyPrice) + t1.buyFee) as total_invest,  round(((sum(t1.buyQty*t2.lastPrice) -sum((t1.buyQty*t1.buyPrice)+t1.buyFee))/sum((t1.buyQty*t1.buyPrice)+t1.buyFee) )*100,1) as pos_perc,round((sum(t1.buyQty*t2.lastPrice) -sum((t1.buyQty*t1.buyPrice)+t1.buyFee)),3) as net_pos  from tbl_stock_buys t1  inner join tbl_syms_simple t2 on t1.symName=t2.symName where t1.buyQty>0 ", null);
           res.moveToFirst();

           while (res.isAfterLast() == false) {
               // stock_sym sym=new stock_sym(conx);
               // sym.sym_name=res.getString(res.getColumnIndex("symName"));
               portfolio_pos.total_value = res.getFloat(res.getColumnIndex("total_value"));
               portfolio_pos.total_invest = res.getFloat(res.getColumnIndex("total_invest"));
               portfolio_pos.perc_pos = (res.getFloat(res.getColumnIndex("pos_perc")));
               portfolio_pos.net_pos = (res.getFloat(res.getColumnIndex("net_pos")));
               portfolio_pos.total_return = portfolio_pos.total_value - portfolio_pos.total_invest;
               // sym.change=Float.parseFloat("12.90");
               //m_symbols.add(sym);
               res.moveToNext();
           }

           res.close();
           db.setTransactionSuccessful();
           db.endTransaction();
           // db.close();
       }catch (SQLiteException ex)
       {

       }
        return portfolio_pos;
    }

    public stock_sym best_perform()
    {
        stock_sym symB=new stock_sym(conx);
        db = db_helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        Cursor res=db.rawQuery("select sum(t1.buyQty*t2.lastPrice) as total_value,  round(((sum(t1.buyQty*t2.lastPrice) -sum((t1.buyQty*t1.buyPrice)+t1.buyFee))/sum((t1.buyQty*t1.buyPrice)+t1.buyFee)  )*100,3) as pos_perc,(sum(t1.buyQty*t2.lastPrice) -sum((t1.buyQty*t1.buyPrice)+t1.buyFee)) as net_pos,t1.symName  from tbl_stock_buys t1 inner join tbl_syms_simple t2 on t1.symName=t2.symName group by t1.symName order by pos_perc desc limit 1",null);
        res.moveToFirst();

        while(res.isAfterLast()==false)
        {
            // stock_sym sym=new stock_sym(conx);
            // sym.sym_name=res.getString(res.getColumnIndex("symName"));
            symB.change=(res.getFloat(res.getColumnIndex("pos_perc")));
            symB.sym_name=res.getString(res.getColumnIndex("symName"));
            // sym.change=Float.parseFloat("12.90");
            //m_symbols.add(sym);
            res.moveToNext();
        }

        res.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return symB;
    }

    public stock_sym least_perform()
    {
        stock_sym symB=new stock_sym(conx);
        db = db_helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        Cursor res=db.rawQuery("select sum(t1.buyQty*t2.lastPrice) as total_value,  round(((sum(t1.buyQty*t2.lastPrice) -sum((t1.buyQty*t1.buyPrice)+t1.buyFee))/sum((t1.buyQty*t1.buyPrice)+t1.buyFee)  )*100,4) as pos_perc,(sum(t1.buyQty*t2.lastPrice) -sum((t1.buyQty*t1.buyPrice)+t1.buyFee)) as net_pos,t1.symName  from tbl_stock_buys t1 inner join tbl_syms_simple t2 on t1.symName=t2.symName group by t1.symName order by pos_perc asc limit 1",null);
        res.moveToFirst();

        while(res.isAfterLast()==false)
        {
            // stock_sym sym=new stock_sym(conx);
            // sym.sym_name=res.getString(res.getColumnIndex("symName"));
            symB.change=(res.getFloat(res.getColumnIndex("pos_perc")));
            symB.sym_name=res.getString(res.getColumnIndex("symName"));
            // sym.change=Float.parseFloat("12.90");
            //m_symbols.add(sym);
            res.moveToNext();
        }

        res.close();
        db.setTransactionSuccessful();
        db.endTransaction();
       // db.close();

        return symB;
    }

    public float sym_position()
    {
        float pos=0;

        Cursor res=db.rawQuery("select sum(buyQty) as total_shares,sum(t1.buyQty*t2.lastPrice) as total_value,t2.lastPrice,  round(((sum(t1.buyQty*t2.lastPrice) -sum((t1.buyQty*t1.buyPrice)+t1.buyFee))/sum((t1.buyQty*t1.buyPrice)+t1.buyFee) )*100,2) as pos_perc,(sum(t1.buyQty*t2.lastPrice) -sum(t1.buyTotal)) as net_pos,t1.symName  from tbl_stock_buys t1 inner join tbl_syms_simple t2 on t1.symName=t2.symName where t1.symName=? ",new String[] {sym_name});
        res.moveToFirst();

        while(res.isAfterLast()==false)
        {
           // stock_sym sym=new stock_sym(conx);
           // sym.sym_name=res.getString(res.getColumnIndex("symName"));
            pos=res.getFloat(res.getColumnIndex("pos_perc"));
            current_price=res.getFloat(res.getColumnIndex("lastPrice"));
            buy_qty=res.getInt(res.getColumnIndex("total_shares"));
           // sym.change=Float.parseFloat("12.90");
            //m_symbols.add(sym);
            res.moveToNext();
        }

        res.close();
        return pos;
    }


    private float get_prev_change()
    {
        float pc=0;
      // db.beginTransactionNonExclusive();
        Cursor res=db.rawQuery("select lastPrice as pc from tbl_syms where import_date< date('now','localtime') and symName=? order by import_date DESC LIMIT 1",new String[]{sym_name});
        res.moveToFirst();

        while(res.isAfterLast()==false)
        {
           pc= res.getFloat(res.getColumnIndex("pc"));
          // Log.e(sym_name + " prev",String.valueOf(pc));
            res.moveToNext();
        }

        res.close();
       // db.setTransactionSuccessful();
       // db.endTransaction();

        return pc;
    }


    public ArrayList<alertz> my_alerts()
    {
        ArrayList<alertz>m_alerts=new ArrayList<alertz>();
        // db = db_helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        Cursor res=db.rawQuery("select ID,symName,alert_value,alert_target from tbl_sym_alert order by ID DESC",null);
        res.moveToFirst();

        while(res.isAfterLast()==false)
        {
            {
               alertz alerter=new alertz();
               alerter.id=res.getInt(res.getColumnIndex("ID"));
                alerter.criteria=res.getString(res.getColumnIndex("alert_target"));
                alerter.price=res.getFloat(res.getColumnIndex("alert_value"));
                alerter.symbol=res.getString(res.getColumnIndex("symName"));

               m_alerts.add(alerter);
            }
            res.moveToNext();
        }

        res.close();

        db.setTransactionSuccessful();
        db.endTransaction();
        return m_alerts;
    }


    public ArrayList<stock_sym> all_symbols()
    {
        ArrayList<stock_sym>m_symbols=new ArrayList<stock_sym>();
       // db = db_helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        Cursor res=db.rawQuery("select symName,lastPrice,watch from tbl_syms_simple order by watch DESC,symName ASC",null);
        res.moveToFirst();

        while(res.isAfterLast()==false)
        {
            if ( res.getString(res.getColumnIndex("symName")) != null) {
                stock_sym sym = new stock_sym(conx);

                sym.sym_name = res.getString(res.getColumnIndex("symName"));
                sym.watch = (res.getInt(res.getColumnIndex("watch"))) == 1;
                sym.current_price = res.getFloat(res.getColumnIndex("lastPrice"));

                sym.get_details();
                if(sym.get_prev_change()>0)
                    sym.sym_details.change_in_price = sym.current_price - sym.get_prev_change();
                else
                    sym.sym_details.change_in_price = 0;
                // sym.buy_qty=sym.
                //Log.e(sym.sym_name, String.valueOf(sym.watch));
                // sym.change=((sym.sym_position()));
                m_symbols.add(sym);
            }
            res.moveToNext();
        }

        res.close();

        db.setTransactionSuccessful();
        db.endTransaction();
        return m_symbols;
    }

    public ArrayList<stock_sym> my_symbols()
    {
        ArrayList<stock_sym>m_symbols=new ArrayList<stock_sym>();
       // db = db_helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        Cursor res=db.rawQuery("select distinct symName from tbl_stock_buys where buyQty>0 order by symName ASC",null);
        res.moveToFirst();

        while(res.isAfterLast()==false)
        {
            if ( res.getString(res.getColumnIndex("symName")) != null) {
                stock_sym sym = new stock_sym(conx);
                sym.sym_name = res.getString(res.getColumnIndex("symName"));
                sym.get_details();
                if(sym.get_prev_change()>0)
                sym.sym_details.change_in_price = sym.sym_details.current_price - sym.get_prev_change();
                 else
                    sym.sym_details.change_in_price = 0;
                // sym.buy_qty=sym.
                // Log.e(sym.sym_name, String.valueOf(sym.sym_details.share_count));
                sym.change = ((sym.sym_position()));
                m_symbols.add(sym);
            }
            res.moveToNext();
        }

        res.close();
        db.setTransactionSuccessful();
        db.endTransaction();
       // db.close();

        return m_symbols;
    }


    public ArrayList<stock_sym> all_buy_history()
    {
        ArrayList<stock_sym>m_symbols=new ArrayList<stock_sym>();
        Cursor res=db.rawQuery("select  ID,symName,buyDate,buyQty,buyPrice,buyFee,buyTotal from tbl_stock_buys",null);
        res.moveToFirst();

        while(res.isAfterLast()==false)
        {
            stock_sym sym=new stock_sym(conx);
            sym.id=res.getInt(res.getColumnIndex("ID"));
            sym.sym_name=res.getString(res.getColumnIndex("symName"));
            sym.buy_price=res.getFloat(res.getColumnIndex("buyPrice"));
            sym.buy_fee=res.getFloat(res.getColumnIndex("buyFee"));
            sym.buy_total=res.getFloat(res.getColumnIndex("buyTotal"));
            sym.buy_qty=res.getInt(res.getColumnIndex("buyQty"));
            //  Log.e("buy qty",String.valueOf(sym.buy_qty));
            sym.buy_date=res.getLong(res.getColumnIndex("buyDate"));
            m_symbols.add(sym);
            res.moveToNext();
        }

        res.close();
        return m_symbols;
    }


    public ArrayList<stock_sym> buy_history()
    {
        ArrayList<stock_sym>m_symbols=new ArrayList<stock_sym>();
        Cursor res=db.rawQuery("select  ID,symName,buyDate,buyQty,buyPrice,buyFee,buyTotal from tbl_stock_buys where symName=?",new String[] {sym_name});
        res.moveToFirst();

        while(res.isAfterLast()==false)
        {
            stock_sym sym=new stock_sym(conx);
            sym.id=res.getInt(res.getColumnIndex("ID"));
            sym.sym_name=res.getString(res.getColumnIndex("symName"));
            sym.buy_price=res.getFloat(res.getColumnIndex("buyPrice"));
            sym.buy_fee=res.getFloat(res.getColumnIndex("buyFee"));
            sym.buy_total=res.getFloat(res.getColumnIndex("buyTotal"));
            sym.buy_qty=res.getInt(res.getColumnIndex("buyQty"));
          //  Log.e("buy qty",String.valueOf(sym.buy_qty));
            sym.buy_date=res.getLong(res.getColumnIndex("buyDate"));
            m_symbols.add(sym);
            res.moveToNext();
        }

        res.close();
        return m_symbols;
    }

    public void get_buy_rec(int rec)
    {
        //stock_sym m_symbols=new stock_sym();
        Cursor res=db.rawQuery("select  ID,symName,buyDate,buyQty,buyPrice,buyFee from tbl_stock_buys where ID=?",new String[] {String.valueOf(rec)});
        res.moveToFirst();

        while(res.isAfterLast()==false)
        {
            //stock_sym sym=new stock_sym(conx);
            id=res.getInt(res.getColumnIndex("ID"));
            sym_name=res.getString(res.getColumnIndex("symName"));
            buy_price=res.getFloat(res.getColumnIndex("buyPrice"));
            buy_fee=res.getFloat(res.getColumnIndex("buyFee"));
            buy_qty=res.getInt(res.getColumnIndex("buyQty"));
            //  Log.e("buy qty",String.valueOf(sym.buy_qty));
            buy_date=res.getLong(res.getColumnIndex("buyDate"));
          //  m_symbols.add(sym);
            res.moveToNext();
        }
res.close();
       // return m_symbols;
    }


    public void add_buy(int rec)
    {
        ContentValues values=new ContentValues();
        values.put("symName",sym_name);
        values.put("buyQty",buy_qty);
        values.put("buyPrice",buy_price);
        values.put("buyFee",buy_fee);
        values.put("buyDate",buy_date);
        values.put("buyTotal",buy_total);
      try {
         if (rec<0)
          db.insert("tbl_stock_buys", null, values);
         else
         {
             db.update("tbl_stock_buys", values, "ID=?", new String[]{String.valueOf(rec)});
         }
      }catch (Exception ex)
      {
        //  Log.d("stockd",ex.getMessage());
           // db.close();;
      }
      finally {

         // db.close();
          SharedPreferences prefs=conx.getSharedPreferences("stockfolio",MODE_PRIVATE);
          String sync_account=prefs.getString("sync_account","-");

          stock_sym istok = new stock_sym(conx);
          if(!sync_account.equals("-")) {
            //  if (istok.all_buy_history().size()>0)
                  new syncDB().execute(sync_account); //backup portfolio to cloud
          }
      }
    }




    public ArrayList get_symbols()
    {
        ArrayList<String> array_list=new ArrayList<String>();

       // db = db_helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        Cursor res=db.rawQuery("select * from tbl_syms_simple order by symName ASC",null);
        res.moveToFirst();
        while(res.isAfterLast()==false)
        {
            if ( res.getString(res.getColumnIndex("symName")) != null) {
                array_list.add(res.getString(res.getColumnIndex("symName")));
            }
            res.moveToNext();
        }

        res.close();
        db.setTransactionSuccessful();
        db.endTransaction();
      //  db.close();

        return array_list;
    }

    public int getSymExist(String sym)
    {
        Cursor c = null;
        try {
            db = db_helper.getWritableDatabase();
            String query = "select count(*) from tbl_syms_simple where symName = ?";
            c = db.rawQuery(query, new String[] {sym});
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        }
        finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
              //  db.close();
            }
        }
    }

    public int getSymExist_withDate(String sym,String imp_date)
    {
        Cursor c = null;
        try {
            db = db_helper.getReadableDatabase();
            String query = "select count(*) from tbl_syms where symName = ? and date(import_date)=?";
            String[]dateS=imp_date.split("\\s+");

            c = db.rawQuery(query, new String[] {sym,dateS[0]});
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        }
        finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                //  db.close();
             }
        }
    }

    public boolean is_alert_set(String sym)
    {
        Cursor c = null;
        try {
            db = db_helper.getReadableDatabase();
            String query = "select * from tbl_sym_alert where symName = ?";
            c = db.rawQuery(query, new String[] {sym});
            if (c.moveToFirst()) {
                return true;
            }
            return false;
        }
        finally {
            if (c != null) {
                c.close();
            }
             if (db != null) {
                //  db.close();
             }
        }
    }

   // long dateFromDatabase = getExistingDateFromSomewhere();
   // String dateToDisplay = getFormattedDate(dateFromDatabase, "yyyy-MM-dd");

    public String getFormattedDate()
    {
        String dateFormat="dd/MM/yyyy";
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(buy_date);
        return formatter.format(calendar.getTime());
    }

    public static long getTimeInMillis(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    public long getMilliFromDate(String dateFormat) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = formatter.parse(dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Today is " + date);
        return date.getTime();
    }

}

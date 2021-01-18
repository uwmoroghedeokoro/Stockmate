package com.gradelogics.overstocked;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;
import android.util.Log;

public class dbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "over_stockd.db";    // Database Name
    private static final String TABLE_NAME = "myTable";   // Table Name
    private static final int DATABASE_Version = 1;   // Database Version
    private static final String UID="_id";     // Column I (Primary Key)
    private static final String NAME = "Name";    //Column II
    private static final String MyPASSWORD= "Password";    // Column III
    private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
            " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+NAME+" VARCHAR(255) ,"+ MyPASSWORD+" VARCHAR(225));";
    private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
    private Context context;

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_Version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
           // Log.e("log","create");
            db.execSQL("create table tbl_stock_buys (ID INTEGER PRIMARY KEY AUTOINCREMENT,symName varchar(50),buyQty INTEGER, buyPrice FLOAT, buyFee FLOAT, buyDate BIGINT, buyTotal FLOAT);");
            db.execSQL("CREATE TABLE tbl_syms (symName text, lastPrice Float DEFAULT 0.00,import_date text);");
            db.execSQL("create table tbl_sym_alert (ID INTEGER PRIMARY KEY AUTOINCREMENT,symName text, alert_value Float DEFAULT 0.00,alert_target text);");
           // db.execSQL("drop table tbl_syms_simple");
            db.execSQL("create table tbl_syms_simple (symName text, lastPrice Float DEFAULT 0.00,watch int default 0);");
            db.execSQL("create table tbl_privacy (pass_code text DEFAULT '',isSync INTEGER DEFAULT 0,isLock INTEGER DEFAULT 0);");

        } catch (Exception e) {
           // Message.message(context,""+e);
        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

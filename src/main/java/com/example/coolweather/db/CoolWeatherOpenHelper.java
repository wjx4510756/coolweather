package com.example.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wjx4510756 on 2016/4/13.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    //Province建表语句
    public static final String CREATE_PROVINCE = "create table Province("
            + "_id integer primary key autoincrement,"
            + "province_name text,"
            + "province_code text)";
    //City 建表语句
    public static final String CREATE_CITY = "create table City("
            + "_id integer primary key autoincrement,"
            + "city_name text,"
            + "city_code text,"
            + "province_id integer)";

    //Country建表语句
    public static final String CREATE_COUNTRY = "create table Country(" +
            "_id integer primary key autoincrement," +
            "country_name text," +
            "country_code text," +
            "city_id integer)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

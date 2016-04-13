package com.example.coolweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.coolweather.db.CoolWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wjx4510756 on 2016/4/13.
 */
public class CoolWeatherDB {
    /*
    数据库基础信息
     */
    public static final String DB_NAME = "cool_weather";
    public static final int VERSION = 1;
    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;

    /*
    将构造方法私有化
     */
    private CoolWeatherDB(Context context) {

        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /*
    获取CoolWeatherDB实例，保证唯一性
     */
    public synchronized static CoolWeatherDB getCoolWeatherDB(Context context) {
        if (coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    /*
    将province信息保存到数据库
    */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }
    /*
    从数据库读取省份信息
     */

    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return list;
    }

    /*
    将City信息保存到数据库
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    /*
    从数据库读取指定省份下的所有城市信息
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return list;
    }

    /*
    将Country信息保存到数据库
     */
    public void saveCountry(Country country) {
        if (country != null) {
            ContentValues values = new ContentValues();
            values.put("country_name", country.getCountryName());
            values.put("country_code", country.getCountryCode());
            values.put("city_id", country.getCityId());
            db.insert("Country", null, values);
        }
    }

    /*
    从数据库读取指定城市下的所有县信息
     */
    public List<Country> loadCountries(int cityId) {
        List<Country> list = new ArrayList<>();
        Cursor cursor = db.query("Country", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Country country = new Country();
                country.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
                country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
                country.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(country);
            } while (cursor.moveToNext());
        }
            if (cursor != null)
                cursor.close();
        return list;
    }
}

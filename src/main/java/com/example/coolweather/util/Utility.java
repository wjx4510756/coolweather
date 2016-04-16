package com.example.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.coolweather.model.City;
import com.example.coolweather.model.CoolWeatherDB;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wjx4510756 on 2016/4/15.
 */
public class Utility {
    /*
    解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {

        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /*
    解析和处理服务器返回的市级数据
     */
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response))
        {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0){
                for (String c:allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return  true;
            }
        }
        return false;
    }

    /*
解析和处理服务器返回的县级数据
 */
    public synchronized static boolean handleCountriesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response))
        {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0){
                for (String c:allCounties) {
                    String[] array = c.split("\\|");
                    County country = new County();
                    country.setCountyCode(array[0]);
                    country.setCountyName(array[1]);
                    country.setCityId(cityId);
                    coolWeatherDB.saveCountry(country);
                }
                return  true;
            }
        }
        return false;
    }
    /*
    解析服务器返回的JSON数据，并将解析出来的数据存到本地
     */
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray info1 = jsonObject.optJSONArray("HeWeather data service 3.0");
            JSONObject info = info1.getJSONObject(0);

            JSONObject basic = info.getJSONObject("basic");
            String cityName = basic.getString("city");
            String weatherCode = basic.getString("id");

            JSONObject update = basic.getJSONObject("update");
            String publishTime = update.getString("loc");

            JSONArray dailyForecast = info.optJSONArray("daily_forecast");
            JSONObject today = dailyForecast.getJSONObject(0);
            JSONObject cond = today.getJSONObject("cond");
            String weatherDesp = cond.getString("txt_d");

            JSONObject tmp = today.getJSONObject("tmp");
            String temp1 = tmp.getString("min");
            String temp2 = tmp.getString("max");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    将服务器返回的数据存储到SharedPreferences文件中
     */
    private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = context.getSharedPreferences("weather",context.MODE_PRIVATE).edit();
        editor.putBoolean("city_selected",true)
                .putString("city_name",cityName)
                .putString("weather_code",weatherCode)
                .putString("temp1",temp1)
                .putString("temp2",temp2)
                .putString("weather_desp",weatherDesp)
                .putString("publish_time",publishTime)
                .putString("current_date",sdf.format(new Date()));
        editor.commit();

    }
}

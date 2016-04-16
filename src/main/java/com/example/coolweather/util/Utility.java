package com.example.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.coolweather.javaBean.WeatherInfoBean;
import com.example.coolweather.javaBean.City;
import com.example.coolweather.model.CoolWeatherDB;
import com.example.coolweather.javaBean.County;
import com.example.coolweather.javaBean.Province;

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
        WeatherInfoBean weatherInfoBean = new WeatherInfoBean();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray info1 = jsonObject.optJSONArray("HeWeather data service 3.0");
            JSONObject info = info1.getJSONObject(0);
            /**
             * 解析得到城市名、ID
             */
            JSONObject basic = info.getJSONObject("basic");
//            String cityName = basic.getString("city");
//            String weatherCode = basic.getString("id");
            weatherInfoBean.setCityName(basic.getString("city"));
            weatherInfoBean.setWeatherCode(basic.getString("id"));
            /**
             * 解析得到数据更新时间
             */
            JSONObject update = basic.getJSONObject("update");
//            String publishTime = update.getString("loc");
            weatherInfoBean.setPublishTime(update.getString("loc"));
            /**
             * 解析得到今天的天气状况
             */
            JSONArray dailyForecast = info.optJSONArray("daily_forecast");
            JSONObject today = dailyForecast.getJSONObject(0);
            JSONObject cond = today.getJSONObject("cond");
//            String weatherDesp = cond.getString("txt_d");
            weatherInfoBean.setWeatherDesp(cond.getString("txt_d"));
            /**
             * 解析得到当前温度
             */
            JSONObject now = info.getJSONObject("now");
//            String nowTemp = now.getString("tmp");
            weatherInfoBean.setNowTemp(now.getString("tmp"));
            /**
             * 解析得到当前风力等级及风向
             */
            JSONObject wind = now.getJSONObject("wind");
//            String sc = wind.getString("sc");
//            String dir = wind.getString("dir");
            weatherInfoBean.setSc(wind.getString("sc"));
            weatherInfoBean.setDir(wind.getString("dir"));
            /**
             * 解析得到当前的天气状况
             */
            JSONObject nowCond = now.getJSONObject("cond");
//            String nowWeather = nowCond.getString("txt");
            weatherInfoBean.setNowWeather(nowCond.getString("txt"));
            /**
             * 解析得到今天的温度min、max
             */
            JSONObject tmp = today.getJSONObject("tmp");
//            String temp1 = tmp.getString("min");
//            String temp2 = tmp.getString("max");
            weatherInfoBean.setTemp1(tmp.getString("min"));
            weatherInfoBean.setTemp2(tmp.getString("max"));

            saveWeatherInfo(context,weatherInfoBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    将服务器返回的数据存储到SharedPreferences文件中
     */
    private static void saveWeatherInfo(Context context,WeatherInfoBean weatherInfoBean) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = context.getSharedPreferences("weather",context.MODE_PRIVATE).edit();
        editor.putBoolean("city_selected",true)
                .putString("city_name",weatherInfoBean.getCityName())
                .putString("weather_code",weatherInfoBean.getWeatherCode())
                .putString("temp1",weatherInfoBean.getTemp1())
                .putString("temp2",weatherInfoBean.getTemp2())
                .putString("weather_desp",weatherInfoBean.getWeatherDesp())
                .putString("publish_time",weatherInfoBean.getPublishTime())
                .putString("now_temp",weatherInfoBean.getNowTemp())
                .putString("sc",weatherInfoBean.getSc())
                .putString("dir",weatherInfoBean.getDir())
                .putString("now_weather",weatherInfoBean.getNowWeather())
                .putString("current_date",sdf.format(new Date()));
        editor.commit();

    }
}

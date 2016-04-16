package com.example.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.coolweather.R;
import com.example.coolweather.service.AutoUpdateService;
import com.example.coolweather.util.HttpCallBackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

/**
 * Created by wjx4510756 on 2016/4/15.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    private LinearLayout weatherInfoLayout;
    private TextView cityName;
    private TextView publishTime;
    private TextView weatherDesp;
    private TextView temp1;
    private TextView temp2;
    private TextView currentData;
    private TextView loading;
    private Button switchCity;
    private Button refreshWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityName = (TextView) findViewById(R.id.city_name);
        publishTime = (TextView) findViewById(R.id.publish_text);
        weatherDesp = (TextView) findViewById(R.id.weather_desp);
        temp1 = (TextView) findViewById(R.id.temp1);
        temp2 = (TextView) findViewById(R.id.temp2);
        loading = (TextView) findViewById(R.id.loading);
        currentData = (TextView) findViewById(R.id.current_data);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)){
            loading.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else
            showWeather();
    }

    /*
    查询县级代号所对应的天气代号
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address,"countyCode");
    }

    /*
    查询天气代号对应的天气
     */
    private void queryWeatherInfo(String weatherCode){
        String address = "https://api.heweather.com/x3/weather?cityid=CN"+weatherCode+"&key=ed1a7af38aab4f7b9978f8d62cf84bd5";
        queryFromServer(address,"weatherCode");
    }
    /*
    根据传入的地址和类型去向服务器查询天气代号或者天气信息
     */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                if (type.equals("countyCode")){
                    //从服务器返回的数据中解析出天气代号
                    if (!TextUtils.isEmpty(response)){
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2){
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if (type.equals("weatherCode")){
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.setText("同步失败");
                    }
                });
            }
        });
    }

    /*
    从SharedPreferences文件中读取存储的天气信息，并显示到界面上
     */
    private void showWeather() {
        SharedPreferences preferences = getSharedPreferences("weather",MODE_PRIVATE);
        cityName.setText(preferences.getString("city_name",""));
        temp1.setText(preferences.getString("temp1","")+"℃");
        temp2.setText(preferences.getString("temp2","")+"℃");
        weatherDesp.setText(preferences.getString("weather_desp",""));
        publishTime.setText(preferences.getString("publish_time","")+"发布");
        currentData.setText(preferences.getString("current_date",""));
        loading.setVisibility(View.INVISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityName.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                loading.setVisibility(View.VISIBLE);
                loading.setText("正在跳转...");
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                loading.setVisibility(View.VISIBLE);
                loading.setText("同步中...");
                SharedPreferences preferences = getSharedPreferences("weather",MODE_PRIVATE);
                String weatherCode= preferences.getString("weather_code","");
                if (!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
}

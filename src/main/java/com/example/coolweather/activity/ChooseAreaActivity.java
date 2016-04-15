package com.example.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.R;
import com.example.coolweather.model.City;
import com.example.coolweather.model.CoolWeatherDB;
import com.example.coolweather.model.Country;
import com.example.coolweather.model.Province;
import com.example.coolweather.util.HttpCallBackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wjx4510756 on 2016/4/15.
 */
public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> datalist = new ArrayList<>();
    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<Country> countryList;
    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //选中的级别
    private int currentLevel;

    public ChooseAreaActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalist);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCountries();
                }
            }
        });
        queryProvinces();
    }

    /*
     查询全国所有的省，优先从数据库查询，如果没有就去服务器上查询
    */
    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            datalist.clear();
            for (Province province : provinceList) {
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else
            queryFromServer(null, "province");
    }

    /*
    查询选中省内所有的市，优先从数据库查询，如果没有就去服务器上查询
   */
    private void queryCities() {
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            datalist.clear();
            for (City city : cityList) {
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else
            queryFromServer(selectedProvince.getProvinceCode(), "city");
    }

    /*
        查询选中市内所有的县，优先从数据库查询，如果没有就去服务器上查询
       */
    private void queryCountries() {
        countryList = coolWeatherDB.loadCountries(selectedCity.getId());
        if (countryList.size() > 0) {
            datalist.clear();
            for (Country country : countryList) {
                datalist.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTRY;
        } else
            queryFromServer(selectedCity.getCityCode(), "country");
    }

    /*
    根据输入的代号和类型从服务器查询市县数据
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else
            address = "http://www.weather.com.cn/data/list3/city.xml";
        showProgress();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if (type.equals("province"))
                    result = Utility.handleProvincesResponse(coolWeatherDB, response);
                else if (type.equals("city"))
                    result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
                else if (type.equals("country"))
                    result = Utility.handleCountriesResponse(coolWeatherDB, response, selectedCity.getId());
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (type.equals("province"))
                                queryProvinces();
                            else if (type.equals("city"))
                                queryCities();
                            else if (type.equals("country"))
                                queryCountries();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    //关闭进度对话框
    private void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    //显示进度对话框
    private void showProgress() {
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Load...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /*
    捕获Back按键，根据当前级别判断是否退出或返回
     */

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTRY)
            queryCities();
        else if (currentLevel == LEVEL_CITY)
            queryProvinces();
        else
            finish();
    }
}

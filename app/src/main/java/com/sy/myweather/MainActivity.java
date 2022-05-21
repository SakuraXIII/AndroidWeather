package com.sy.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.sy.myweather.data.FutureWeather;
import com.sy.myweather.data.Province;
import com.sy.myweather.data.RealWeather;
import com.sy.myweather.data.Weather;
import com.sy.myweather.util.Util;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int num = 0;
    public static String city = "";

    public void setCity(String city) {
        MainActivity.city = city;
        ((TextView) this.findViewById(R.id.city)).setText(city);
    }

    public static final String key = "";
    public static final String cityListURL = "http://apis.juhe.cn/simpleWeather/cityList?key=" + key;
    public static final String weatherByCityURL = "http://apis.juhe.cn/simpleWeather/query?key=" + key + "&city=";
    public static final String lifeByCityURL = "http://apis.juhe.cn/simpleWeather/life?key=" + key + "&city=";
    private final Map<String, Integer> weatherToIcon = new HashMap<String, Integer>() {{
        put("晴", R.string.sun);
        put("阴", R.string.cloudy);
        put("小雨", R.string.rain);
        put("小到中雨", R.string.rain);
        put("中雨", R.string.rain2);
        put("中到大雨", R.string.rain2);
        put("大雨", R.string.rain2);
        put("大到暴雨", R.string.rain2);
        put("阵雨", R.string.sun_rain);
        put("雷阵雨", R.string.heavy_rain);
        put("多云", R.string.cloudy_sun);
        put("小雪", R.string.snowy);
        put("中雪", R.string.snowy);
        put("大雪", R.string.snowy);
        put("霜", R.string.snow);
    }};


    private final ArrayList<String> provinceList = new ArrayList<>();
    private final ArrayList<ArrayList<String>> cityList = new ArrayList<>();
    private final ArrayList<ArrayList<ArrayList<String>>> districtList = new ArrayList<>();

    private RealWeather realWeather = null;
    private final ArrayList<FutureWeather> futureWeathers = new ArrayList<>();
    private SharedPreferences sharedPre = null;


    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg != null) {
                switch (msg.what) {
                    case MyThread.JSONSTR:
                        saveWeaToSharedPre((String) msg.obj);
                        break;
                    case MyThread.CITYLIST:
                        handleCityData((ArrayList<Province>) msg.obj);
                        showModifyCityAlert();
                        break;
                    case MyThread.WEATHER:
                        handleWeatherData((ArrayList<Weather>) msg.obj);
                        updateRealInfo();
                        insertFutureInfo();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "发生了一些问题 " + msg.obj, Toast.LENGTH_SHORT).show();
                        readWeaFromSharedPre();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        sharedPre = getSharedPreferences("weather_info", MODE_PRIVATE);
        MainActivity.city = sharedPre.getString("city", MainActivity.city);
        init();
        findViewById(R.id.edit_icon).setOnClickListener(this);
        findViewById(R.id.future_state).setOnClickListener(this);
    }

    private void init() {
        new MyThread(handler).getWeatherByCity();
    }

    private void handleWeatherData(ArrayList<Weather> data) {
        realWeather = (RealWeather) data.get(0);
        futureWeathers.clear();
        for (int i = 1; i < data.size(); i++) {
            futureWeathers.add((FutureWeather) data.get(i));
        }
    }

    private void handleCityData(ArrayList<Province> citysList) {
        for (int i = 0; i < citysList.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Citys_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
            provinceList.add(citysList.get(i).getProvince());
            for (int c = 0; c < citysList.get(i).getCitys().size(); c++) {//遍历该省份的所有城市
                String CityName = citysList.get(i).getCitys().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (citysList.get(i).getCitys().get(c).getDistricts() == null
                        || citysList.get(i).getCitys().get(c).getDistricts().size() == 0) {
                    City_AreaList.add("");
                } else {
                    City_AreaList.addAll(citysList.get(i).getCitys().get(c).getDistricts());
                }
                Citys_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            cityList.add(CityList);
            districtList.add(Citys_AreaList);
        }
    }

    private void updateRealInfo() {
        ((TextView) findViewById(R.id.city)).setText(city);
        ((TextView) findViewById(R.id.weather_temperature)).setText(realWeather.getTemperature());
        ((TextView) findViewById(R.id.weather_info)).setText(realWeather.getInfo());
        ((FontIconView) findViewById(R.id.weather_icon)).setText(weatherToIcon.get(realWeather.getInfo()));
        ((TextView) findViewById(R.id.weather_aqi)).setText(realWeather.getAqi());
        ((TextView) findViewById(R.id.weather_humidity)).setText(realWeather.getHumidity() + " RH%");
    }

    private void insertFutureInfo() {
        LinearLayout futureWeatherLayout = (LinearLayout) findViewById(R.id.future_weather);
        futureWeatherLayout.removeAllViews();
        for (int i = 0; i < futureWeathers.size(); i++) {
            FutureWeather future = (FutureWeather) futureWeathers.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.lv_item, futureWeatherLayout, false);
            String day = future.getDate().split("-")[2] + "日";
            ((TextView) view.findViewById(R.id.future_date)).setText(day);
            ((TextView) view.findViewById(R.id.future_direct)).setText(future.getDirect());
            ((TextView) view.findViewById(R.id.future_temperature)).setText(future.getTemperature());
            futureWeatherLayout.addView(view);
        }
    }

    private void readWeaFromSharedPre() {
        String weather = sharedPre.getString("weather", null);
        if (weather == null) {
            Toast.makeText(this, "sharedPre无数据", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Log.i("TAG", weather);
            ArrayList<Weather> weatherArrayList = Util.getWeather(weather);
            handleWeatherData(weatherArrayList);
            updateRealInfo();
            insertFutureInfo();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("TAG", "JSON格式化异常");
        }
    }

    private void saveWeaToSharedPre(String json) {
        SharedPreferences.Editor editor = sharedPre.edit();
        editor.putString("weather", json);
        editor.apply();
    }

    private void showModifyCityAlert() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            //返回的分别是三个级别的选中位置
//            String tx = provinceList.get(options1)
//                    + cityList.get(options1).get(options2)
//                    + districtList.get(options1).get(options2).get(options3);
            String tx = districtList.get(options1).get(options2).get(options3);
            this.setCity(tx);
            init();
            sharedPre.edit().putString("city",MainActivity.city).apply();
        }).build();
        pvOptions.setPicker(provinceList, cityList, districtList);
        pvOptions.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_icon:
                new MyThread(handler).getCityListByFile(this);
                break;

            case R.id.future_state:
                Intent intent = new Intent(this, FutureChart.class);
                intent.putExtra("weather", futureWeathers);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (num == 1) {
            super.onBackPressed();
            num = 0;
            finish();
            return;
        } else {
            Toast.makeText(this, "二次确认再退出", Toast.LENGTH_SHORT).show();
            num++;
            handler.postDelayed(() -> num = 0, 1000);
        }
    }
}
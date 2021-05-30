package com.sy.myweather.util;

import android.accounts.NetworkErrorException;

import com.sy.myweather.data.City;
import com.sy.myweather.data.FutureWeather;
import com.sy.myweather.data.Province;
import com.sy.myweather.data.RealWeather;
import com.sy.myweather.data.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * @program: My Weather
 * @description: 工具类
 * @author: SY
 * @create: 2021-05-28 11:54
 **/
public class Util {
    public static String getRequest(String url) throws NetworkErrorException {
        String json = "";
        try {
            InputStream inputStream = new URL(url).openStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //读取缓存
            byte[] buffer = new byte[2048];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, length);//写入输出流
            }
            inputStream.close();
            json = new String(bos.toByteArray(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (json.length() == 0) {
            throw new NetworkErrorException("接收json为空");
        }
        return json;
    }

    public static ArrayList<Province> getCityList(String json) throws JSONException {
        ArrayList<Province> provinces = new ArrayList<>();
        JSONObject cityObject = new JSONObject(json);
        JSONArray cityArr = cityObject.getJSONArray("result");
        for (int i = 0; i < cityArr.length(); i++) {
            JSONObject cityItem = (JSONObject) cityArr.get(i);
            String province = cityItem.getString("province");
            String city = cityItem.getString("city");
            String district = cityItem.getString("district");
            boolean isNew = true;
            for (Province provinceData : provinces) {
                if (provinceData.getProvince().equals(province)) {
                    for (City citysData : provinceData.getCitys()) {
                        if (citysData.getName().equals(city)) {
                            //添加新的地区
                            citysData.getDistricts().add(district);
                            isNew = false;
                            break;
                        }
                    }
                    //添加新的城市
                    if (isNew) {
                        provinceData.getCitys().add(new City(city, district));
                        isNew = false;
                    }
                }
            }
            //添加新的省份
            if (isNew) provinces.add(new Province(province, new City(city, district)));
        }
        return provinces;
    }


    public static ArrayList<Weather> getWeather(String json) throws JSONException {
        ArrayList<Weather> weatherList = new ArrayList<>();
        JSONObject object = new JSONObject(json);
        JSONObject weatherObject = object.getJSONObject("result");
        JSONObject realtime = weatherObject.getJSONObject("realtime");
        String temperature = realtime.getString("temperature");
        String humidity = realtime.getString("humidity");
        String info = realtime.getString("info");
        String direct = realtime.getString("direct");
        String power = realtime.getString("power");
        String aqi = realtime.getString("aqi");
        weatherList.add(new RealWeather(temperature, humidity, info, direct, power, aqi));
        JSONArray futureArr = weatherObject.getJSONArray("future");
        for (int i = 0; i < futureArr.length(); i++) {
            JSONObject future = (JSONObject) futureArr.get(i);
            String date = future.getString("date");
            String temperature1 = future.getString("temperature");
            String weather = future.getString("weather");
            String direct1 = future.getString("direct");
            weatherList.add(new FutureWeather(temperature1, weather, direct1, date));
        }
        return weatherList;
    }


}

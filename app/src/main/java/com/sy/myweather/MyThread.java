package com.sy.myweather;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.sy.myweather.data.Province;
import com.sy.myweather.data.Weather;
import com.sy.myweather.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @program: My Weather
 * @description: 异步线程类
 * @author: SY
 * @create: 2021-05-28 13:56
 **/
class MyThread {

    private Handler handler;
    final static int ERROR = 999;
    final static int CITYLIST = 1;
    final static int WEATHER = 2;
    final static int JSONSTR = 0;


    public MyThread(Handler handler) {
        this.handler = handler;
    }

    public void getCityListByFile(Context context) {
        new Thread(() -> {
            try {
                InputStream stream = context.getAssets().open("cityList.json");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                //读取缓存
                byte[] buffer = new byte[2048];
                int length = 0;
                while ((length = stream.read(buffer)) != -1) {
                    bos.write(buffer, 0, length);//写入输出流
                }
                stream.close();
                String json = new String(bos.toByteArray(), "utf-8");
                ArrayList<Province> provincesList = Util.getCityList(json);
                sendMessage(CITYLIST, provincesList);
            } catch (FileNotFoundException e) {
                getCityListByNet();
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(ERROR);
            }
        }).start();
    }

    public void getCityListByNet() {
        new Thread(() -> {
            try {
                String json = Util.getRequest(MainActivity.cityListURL);
                ArrayList<Province> cityList = Util.getCityList(json);
                sendMessage(CITYLIST, cityList);
            } catch (NetworkErrorException e) {
                e.printStackTrace();
                handler.sendMessage(handler.obtainMessage(ERROR, "网络异常"));
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(ERROR);
            }
        }).start();
    }

    public void getWeatherByCity() {
        new Thread(() -> {
            try {
                String json = Util.getRequest(MainActivity.weatherByCityURL + MainActivity.city);
                ArrayList<Weather> weatherList = Util.getWeather(json);
                sendMessage(WEATHER, weatherList);
                sendMessage(JSONSTR, json);
            } catch (NetworkErrorException e) {
                e.printStackTrace();
                handler.sendMessage(handler.obtainMessage(ERROR, "网络异常"));
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(ERROR);
            }
        }).start();
    }

    public void sendMessage(int what, Object object) {
        Message message = handler.obtainMessage();
        message.what = what;
        message.obj = object;
        handler.sendMessage(message);
    }

}

package com.sy.myweather.data;

import java.io.Serializable;

/**
 * @program: My Weather
 * @description: 天气状况基类
 * @author: SY
 * @create: 2021-05-28 15:26
 **/
public class Weather implements Serializable {
    private String temperature;
    private String info;
    private String direct;

    public Weather(String temperature, String info, String direct) {
        this.temperature = temperature;
        this.info = info;
        this.direct = direct;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "temperature='" + temperature + '\'' +
                ", info='" + info + '\'' +
                ", direct='" + direct + '\'' +
                '}';
    }
}

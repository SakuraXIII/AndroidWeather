package com.sy.myweather.data;

/**
 * @program: My Weather
 * @description: 未来天气状况类
 * @author: SY
 * @create: 2021-05-28 15:22
 **/
public class FutureWeather extends Weather {
    private String date;

    public FutureWeather(String temperature, String info, String direct, String date) {
        super(temperature, info, direct);
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "FutureWeather{" +
                "date='" + date + '\'' +
                ", temperature='" + super.getTemperature() + '\'' +
                ", info='" + super.getInfo() + '\'' +
                ", direct='" + super.getDirect() + '\'' +
                '}';
    }
}

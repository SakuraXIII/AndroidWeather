package com.sy.myweather.data;

/**
 * @program: My Weather
 * @description: 天气数据类
 * @author: SY
 * @create: 2021-05-28 15:14
 **/
public class RealWeather extends Weather {
    private String humidity; //湿度
    private String power; //风力
    private String aqi; //空气质量指数

    public RealWeather(String temperature, String humidity, String info, String direct, String power, String aqi) {
        super(temperature, info, direct);
        this.humidity = humidity;
        this.power = power;
        this.aqi = aqi;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "temperature='" + super.getTemperature() + '\'' +
                ", humidity='" + humidity + '\'' +
                ", info='" + super.getInfo() + '\'' +
                ", direct='" + super.getDirect() + '\'' +
                ", power='" + power + '\'' +
                ", aqi='" + aqi + '\'' +
                '}';
    }
}

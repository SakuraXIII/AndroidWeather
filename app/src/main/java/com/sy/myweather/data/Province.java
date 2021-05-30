package com.sy.myweather.data;

import java.util.ArrayList;

/**
 * @program: My Weather
 * @description: 省份类
 * @author: SY
 * @create: 2021-05-28 11:27
 **/
public class Province {

    private String province;
    private ArrayList<City> citys = new ArrayList<>();

    public Province(String province, City city) {
        this.province = province;
        this.citys.add(city);
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public ArrayList<City> getCitys() {
        return citys;
    }

    public void setCitys(ArrayList<City> citys) {
        this.citys = citys;
    }

    @Override
    public String toString() {
        return "Province{" +
                "province='" + province + '\'' +
                ", citys=" + citys +
                '}';
    }
}

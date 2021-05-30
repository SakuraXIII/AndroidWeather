package com.sy.myweather.data;

import java.util.ArrayList;

/**
 * @program: My Weather
 * @description: 城市类
 * @author: SY
 * @create: 2021-05-30 00:26
 **/
public class City {
    private String name;
    private ArrayList<String> districts = new ArrayList<>();

    public City(String name, String districts) {
        this.name = name;
        this.districts.add(districts);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getDistricts() {
        return districts;
    }

    public void setDistricts(ArrayList<String> districts) {
        this.districts = districts;
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", districts=" + districts +
                '}';
    }
}

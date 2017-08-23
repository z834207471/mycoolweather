package com.mycool.gary.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Gary on 2017/8/22.
 */

public class City extends DataSupport {
    private int id;
    private String CityName;
    private int CityId;
    private String ProvinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public int getCityId() {
        return CityId;
    }

    public void setCityId(int cityId) {
        CityId = cityId;
    }

    public String getProvinceId() {
        return ProvinceId;
    }

    public void setProvinceId(String provinceId) {
        ProvinceId = provinceId;
    }
}

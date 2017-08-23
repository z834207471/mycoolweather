package com.mycool.gary.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Gary on 2017/8/22.
 */

public class County extends DataSupport {
    private int id;
    private String CountyName;
    private String WeatherId;
    private String CityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return CountyName;
    }

    public void setCountyName(String countyName) {
        CountyName = countyName;
    }

    public String getWeatherId() {
        return WeatherId;
    }

    public void setWeatherId(String weatherId) {
        WeatherId = weatherId;
    }

    public String getCityId() {
        return CityId;
    }

    public void setCityId(String cityId) {
        CityId = cityId;
    }
}

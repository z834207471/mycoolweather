package com.mycool.gary.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Gary on 2017/8/22.
 */

public class Province extends DataSupport {
    private int id;
    private int ProvinceId;
    private String ProvinceName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceId() {
        return ProvinceId;
    }

    public void setProvinceId(int provinceId) {
        ProvinceId = provinceId;
    }

    public String getProvinceName() {
        return ProvinceName;
    }

    public void setProvinceName(String provinceName) {
        ProvinceName = provinceName;
    }
}

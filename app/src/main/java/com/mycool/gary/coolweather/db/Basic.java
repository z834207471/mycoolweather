package com.mycool.gary.coolweather.db;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Gary on 2017/8/22.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}

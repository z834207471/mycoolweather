package com.mycool.gary.coolweather.db;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gary on 2017/8/22.
 */

public class Aqi {
    @SerializedName("city")
    public AqiCity aqicity;
    public class AqiCity{
        public String aqi;
        public String pm25;

    }
}

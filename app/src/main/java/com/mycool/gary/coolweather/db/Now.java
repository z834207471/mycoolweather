package com.mycool.gary.coolweather.db;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gary on 2017/8/22.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    public Cond cond;
    public class Cond{
        @SerializedName("txt")
        public String info;
    }
}

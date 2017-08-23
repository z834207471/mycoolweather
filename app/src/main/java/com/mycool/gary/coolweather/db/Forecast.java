package com.mycool.gary.coolweather.db;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gary on 2017/8/22.
 */

public class Forecast {
    public String date;
    public Cond cond;
    public Tmp tmp;
    public class Cond{
        @SerializedName("txt_d")
        public String txt;
    }
    public class Tmp{
        public String max;
        public String min;
    }
}

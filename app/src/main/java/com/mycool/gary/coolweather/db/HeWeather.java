package com.mycool.gary.coolweather.db;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Gary on 2017/8/22.
 */

public class HeWeather {
    public String status;
    public Basic basic;
    public Aqi aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecasts;
}

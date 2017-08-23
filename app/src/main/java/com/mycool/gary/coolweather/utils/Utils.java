package com.mycool.gary.coolweather.utils;

import android.system.ErrnoException;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mycool.gary.coolweather.db.City;
import com.mycool.gary.coolweather.db.County;
import com.mycool.gary.coolweather.db.HeWeather;
import com.mycool.gary.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gary on 2017/8/22.
 */

public class Utils {
    public static boolean handlProvince(String respones){
        if (!TextUtils.isEmpty(respones)){
            try {
                JSONArray allProvinces = new JSONArray(respones);
                for (int i = 0;i < allProvinces.length();i++){
                    JSONObject queryProvince = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceId(queryProvince.getInt("id"));
                    province.setProvinceName(queryProvince.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handlCity(String respones,String provinceId){
        if (!TextUtils.isEmpty(respones)){
            try {
                JSONArray allCities = new JSONArray(respones);
                for (int i = 0;i < allCities.length();i++){
                    JSONObject queryCity = allCities.getJSONObject(i);
                    City city = new City();
                    city.setProvinceId(provinceId);
                    city.setCityId(queryCity.getInt("id"));
                    city.setCityName(queryCity.getString("name"));
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handlCounty(String respones,String cityId){
        if (!TextUtils.isEmpty(respones)){
            try {
                JSONArray allCounties = new JSONArray(respones);
                for (int i = 0;i < allCounties.length();i++){
                    JSONObject queryCounty = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyName(queryCounty.getString("name"));
                    county.setWeatherId(queryCounty.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static HeWeather handlerWeather(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getString(0).toString();
          //  Log.e("grgrgr", weatherContent);
            return new Gson().fromJson(weatherContent,HeWeather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

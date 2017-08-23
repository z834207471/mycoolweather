package com.mycool.gary.coolweather.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Gary on 2017/8/22.
 */

public class HttpUtil {
    public static void sendHttp(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}

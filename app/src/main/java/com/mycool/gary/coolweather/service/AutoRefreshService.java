package com.mycool.gary.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.mycool.gary.coolweather.db.HeWeather;
import com.mycool.gary.coolweather.utils.HttpUtil;
import com.mycool.gary.coolweather.utils.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Gary on 2017/8/23.
 */

public class AutoRefreshService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updatePic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;
        long trigger = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this,AutoRefreshService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,trigger,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updatePic() {
        String picUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendHttp(picUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String picResponse = response.body().string();
                if (picResponse != null){
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(AutoRefreshService.this).edit();
                    editor.putString("picpath",picResponse);
                    editor.apply();
                }

            }
        });
    }

    private void updateWeather() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
        final String weatherContent = spf.getString("weather",null);
        if (weatherContent != null){
            final HeWeather weather = Utils.handlerWeather(weatherContent);
            String weatherId = weather.basic.weatherId;
            String WeatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId + "&key=cb3a680a9cc34849bd2f4e32bc8ff81f";
            HttpUtil.sendHttp(WeatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String weatherResponse = response.body().string();
                    HeWeather weather = Utils.handlerWeather(weatherResponse);
                    if (weather.status.equals("ok") && weather != null){
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(AutoRefreshService.this).edit();
                        editor.putString("weather",weatherResponse);
                        editor.apply();
                    }

                }
            });

        }

    }
}

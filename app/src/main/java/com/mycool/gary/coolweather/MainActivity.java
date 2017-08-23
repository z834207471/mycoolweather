package com.mycool.gary.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mycool.gary.coolweather.activity.WeatherActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherInfo = spf.getString("weather",null);
//        if (weatherInfo != null){
//            Intent intent = new Intent(this, WeatherActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }
}

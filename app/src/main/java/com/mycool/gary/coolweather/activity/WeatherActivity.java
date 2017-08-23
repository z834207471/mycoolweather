package com.mycool.gary.coolweather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.Until;
import com.mycool.gary.coolweather.R;
import com.mycool.gary.coolweather.db.Forecast;
import com.mycool.gary.coolweather.db.HeWeather;
import com.mycool.gary.coolweather.service.AutoRefreshService;
import com.mycool.gary.coolweather.utils.HttpUtil;
import com.mycool.gary.coolweather.utils.Utils;

import org.w3c.dom.Comment;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    public SwipeRefreshLayout swipRefresh;
    private Button btn_chose;
    public DrawerLayout drawerLayout;
    public String WeatherUrl;
    private TextView cityTitle;
    private TextView cityUpdate;
    private TextView tmpContent;
    private TextView weatherContent;
    private LinearLayout forcastLayout;
    private TextView aqiTxt;
    private TextView pm25Txt;
    private TextView comTxt;
    private TextView carTxt;
    private TextView sportTxt;
    private ProgressDialog progressDialog;
    private ImageView picBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initView(); //初始化控件
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherDefaultInfo = spf.getString("weather", null);
        String picpath = spf.getString("picpath", null);
        if (weatherDefaultInfo != null && picpath != null) {
            HeWeather Defaultweather = Utils.handlerWeather(weatherDefaultInfo);
            WeatherUrl = Defaultweather.basic.weatherId;
            showWeather(Defaultweather);
            Glide.with(this).load(picpath).into(picBg);
        } else {
            forcastLayout.setVisibility(View.INVISIBLE);
            String weatherId = getIntent().getStringExtra("weatherid");
            Log.e("grgrgr", weatherId);
            queryWeatherFromServer(WeatherUrl);
        }
    }


    public void queryWeatherFromServer(String url) {
        showProgressDialog();
        WeatherUrl = "http://guolin.tech/api/weather?cityid="
                + url + "&key=cb3a680a9cc34849bd2f4e32bc8ff81f";
        HttpUtil.sendHttp(WeatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(WeatherActivity.this, "获取天气数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                final HeWeather weatherTxt = Utils.handlerWeather(str);
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        if (weatherTxt.status.equals("ok")) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather", str);
                            editor.apply();
                            showWeather(weatherTxt);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气数据失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
        HttpUtil.sendHttp("http://guolin.tech/api/bing_pic", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                swipRefresh.setRefreshing(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String path = response.body().string();
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        if (path != null) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("picpath",path);
                            editor.apply();
                            Glide.with(WeatherActivity.this).load(path).into(picBg);
                            swipRefresh.setRefreshing(false);
                        }
                    }
                });

            }
        });
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(WeatherActivity.this);
            progressDialog.setMessage("正在加载天气");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void showWeather(HeWeather weather) {
        cityTitle.setText(weather.basic.cityName);
        cityUpdate.setText(weather.basic.update.updateTime);
        tmpContent.setText(weather.now.temperature);
        weatherContent.setText(weather.now.cond.info);
        try {
            aqiTxt.setText(weather.aqi.aqicity.aqi);
            pm25Txt.setText(weather.aqi.aqicity.pm25);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "获取AQI失败", Toast.LENGTH_SHORT).show();
            aqiTxt.setText("");
            pm25Txt.setText("");
        }
        comTxt.setText(weather.suggestion.comf.txt);
        carTxt.setText(weather.suggestion.cw.txt);
        sportTxt.setText(weather.suggestion.sport.txt);
        forcastLayout.removeAllViews();
        for (Forecast forecast : weather.forecasts) {
            View view = LayoutInflater.from(WeatherActivity.this)
                    .inflate(R.layout.forcast_item, forcastLayout, false);
            ((TextView) view.findViewById(R.id.forcast_date)).setText(forecast.date);
            ((TextView) view.findViewById(R.id.forcast_info)).setText(forecast.cond.txt);
            ((TextView) view.findViewById(R.id.forcast_max)).setText(forecast.tmp.max);
            ((TextView) view.findViewById(R.id.forcast_min)).setText(forecast.tmp.min);
            forcastLayout.addView(view);
        }
        forcastLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoRefreshService.class);
        startService(intent);
    }

    private void initView() {
        swipRefresh = (SwipeRefreshLayout) findViewById(R.id.swip_refresh);
        swipRefresh.setColorSchemeResources(R.color.colorPrimary);
        cityTitle = (TextView) findViewById(R.id.city_title);
        cityUpdate = (TextView) findViewById(R.id.city_update);
        tmpContent = (TextView) findViewById(R.id.tmp_content);
        weatherContent = (TextView) findViewById(R.id.weather_content);
        forcastLayout = (LinearLayout) findViewById(R.id.forcast_layout);
        aqiTxt = (TextView) findViewById(R.id.aqi_txt);
        pm25Txt = (TextView) findViewById(R.id.pm25_txt);
        comTxt = (TextView) findViewById(R.id.com_txt);
        carTxt = (TextView) findViewById(R.id.car_txt);
        sportTxt = (TextView) findViewById(R.id.sport_txt);
        picBg = (ImageView) findViewById(R.id.pic_bg);
        btn_chose = (Button) findViewById(R.id.btn_chose);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btn_chose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        swipRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryWeatherFromServer(WeatherUrl);
            }
        });
    }
}

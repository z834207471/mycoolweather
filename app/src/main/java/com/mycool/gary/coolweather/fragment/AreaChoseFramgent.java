package com.mycool.gary.coolweather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mycool.gary.coolweather.MainActivity;
import com.mycool.gary.coolweather.R;
import com.mycool.gary.coolweather.activity.WeatherActivity;
import com.mycool.gary.coolweather.db.City;
import com.mycool.gary.coolweather.db.County;
import com.mycool.gary.coolweather.db.Province;
import com.mycool.gary.coolweather.utils.HttpUtil;
import com.mycool.gary.coolweather.utils.Utils;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Gary on 2017/8/22.
 */

public class AreaChoseFramgent extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private int current_level;
    private ListView listView;
    private TextView titleName;
    private Button btnBack;
    private ArrayAdapter adapter;
    private List<String> dateList;
    private List<Province> allProvinces;
    private List<City> allCities;
    private List<County> allCounties;
    private ProgressDialog progressDialog;
    private Province selectProvince;
    private City selectCity;
    private County selectCounty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.chose_area, container, false);
        titleName = view.findViewById(R.id.title_name);
        btnBack = view.findViewById(R.id.btn_back);
        listView = view.findViewById(R.id.listview);
        dateList = new ArrayList<>();
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, dateList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (current_level == LEVEL_PROVINCE) {
                    selectProvince = allProvinces.get(i);
                    queryCities();
                } else if (current_level == LEVEL_CITY) {
                    selectCity = allCities.get(i);
                    queryCounties();
                } else if (current_level == LEVEL_COUNTY) {
                    selectCounty = allCounties.get(i);
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weatherid", selectCounty.getWeatherId());
                        startActivity(intent);
                        getActivity().finish();
                    }
                    if (getActivity() instanceof  WeatherActivity){
                          WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipRefresh.setRefreshing(true);
                        activity.queryWeatherFromServer(selectCounty.getWeatherId());
                    }
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_level == LEVEL_COUNTY) {
                    queryCities();
                } else if (current_level == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces() {
        titleName.setText("中国");
        btnBack.setVisibility(View.GONE);
        allProvinces = DataSupport.findAll(Province.class);
        if (allProvinces.size() > 0) {
            dateList.clear();
            for (Province province : allProvinces) {
                dateList.add(province.getProvinceName() + "");
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    private void queryCities() {
        titleName.setText(selectProvince.getProvinceName());
        btnBack.setVisibility(View.VISIBLE);
        allCities = DataSupport.where("provinceid = ?", String.valueOf(selectProvince.getId())).find(City.class);
        if (allCities.size() > 0) {
            dateList.clear();
            for (City city : allCities) {
                dateList.add(city.getCityName() + "");
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level = LEVEL_CITY;
        } else {
            String address = "http://guolin.tech/api/china/" + selectProvince.getProvinceId();
            queryFromServer(address, "city");
        }
    }

    private void queryCounties() {
        titleName.setText(selectCity.getCityName());
        btnBack.setVisibility(View.VISIBLE);
        allCounties = DataSupport.where("cityid = ?", String.valueOf(selectCity.getId())).find(County.class);
        if (allCounties.size() > 0) {
            dateList.clear();
            for (County county : allCounties) {
                dateList.add(county.getCountyName() + "");
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level = LEVEL_COUNTY;
        } else {
            String address = "http://guolin.tech/api/china/" + selectProvince.getProvinceId() + "/" + selectCity.getCityId();
            queryFromServer(address, "county");
        }
    }

    private void queryFromServer(final String address, final String type) {
        showProgressDialog();
        HttpUtil.sendHttp(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                boolean reques = false;
                if (type.equals("province")) {
                    reques = Utils.handlProvince(str);
                } else if (type.equals("city")) {
                    reques = Utils.handlCity(str, selectProvince.getId() + "");
                } else if (type.equals("county")) {
                    reques = Utils.handlCounty(str, selectCity.getId() + "");
                }
                if (reques) {
                    getActivity().runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (type.equals("province")) {
                                queryProvinces();
                            } else if (type.equals("city")) {
                                queryCities();
                            } else if (type.equals("county")) {
                                queryCounties();
                            }
                        }
                    });
                }

            }
        });


    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("马上就可以看到你的城市了奥……");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        progressDialog.dismiss();
    }
}

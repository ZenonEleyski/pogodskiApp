package com.projects.pogodski;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

public class WeatherFragment extends Fragment {
    private TextView mainTemperature;
    private TextView mainLocation;
    private TextView mainWeatherCodeText;
    private ImageView mainWeatherCodeIcon;
    private android.widget.ImageButton reload;
    private View view;
    private TextView loading;
    private WeatherDay weatherDay;
    private MyLocation myLocation;
    private static final String WTH_TAG = "weatherfragmentlog";


    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST=1001;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.weather_fragment, container, false);

        reload = view.findViewById(R.id.reload_btn);
        mainTemperature = view.findViewById(R.id.big_temp_on_main_fragment);
        mainLocation = view.findViewById(R.id.text_location);
        mainWeatherCodeText = view.findViewById(R.id.weather_code_text);
        mainWeatherCodeIcon = view.findViewById(R.id.weather_now);
        loading = view.findViewById(R.id.loading);
        myLocation = MyLocation.getInstance();
        weatherDay = new WeatherDay();
        loading.setText("ЗАГРУЗКA ДАННЫХ..");




        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());



        reload.setOnClickListener(v->{
            v.animate().rotationBy(180).setDuration(300).start();
            getLocation(myLocation);
        });

        return view;

    }














    public void update(MyLocation location, WeatherDay weatherDay){

        Log.i(WTH_TAG, "Процесс обновления погоды запущен");

        myLocation.updateLocation(location, new MyLocation.LocationUpdateListener(){
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.i(WTH_TAG, "Геопозиция успешно определена: " + location.getCity());
                    });
                }
                weatherDay.updateWeather(weatherDay, location, new WeatherDay.WeatherUpdateListener() {
                    @Override
                    public void onSuccess() {
                        if (getActivity()!=null){
                            getActivity().runOnUiThread(()->{
                                mainLocation.setText(location.getCity());
                                mainTemperature.setText(String.valueOf(weatherDay.getNowTemperature())+"°");
                                updateWeatherCodeIconAndText(weatherDay);
                                loading.setText("");

                                RecyclerView recyclerView = view.findViewById(R.id.recycleview_weather);
                                Integer[] temperatures = weatherDay.getTemperatures();
                                Integer[] codes = weatherDay.getWeatherCodes();



                                Log.i(WTH_TAG, "Успешная загрузка погоды на экран!!!!!!!" +
                                        "\n{ Температура="+String.valueOf(weatherDay.getNowTemperature())+"°"+" }" +
                                        "\n{ Город="+location.getCity()+" }");
                            });
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if(getActivity()!=null){
                            getActivity().runOnUiThread(()->{
                                Snackbar.make(view, "ошибка: "+error, Snackbar.LENGTH_SHORT).show();
                                Log.e(WTH_TAG, "Ошбика загрузки данных на экран [ "+error+" ]");
                            });
                        }

                    }
                });
            }

            @Override
            public void onError(String error) {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(()->{
                        Snackbar.make(view, "ошибка: "+error, Snackbar.LENGTH_SHORT).show();
                        Log.e(WTH_TAG, "Ошбика загрузки данных на экран [ "+error+" ]");
                    });
                }
            }});
    }
    public void updateWeatherCodeIconAndText(WeatherDay weatherDay){
        int weatherCode = weatherDay.getNowWeatherCode();
        Log.i(WTH_TAG, "Код погоды - "+weatherCode);
        if (weatherCode == 0 || weatherCode == 1) {
            mainWeatherCodeIcon.setImageResource(R.drawable.sun);
            mainWeatherCodeText.setText(R.string.sun);
        } else if (weatherCode == 2) {
            mainWeatherCodeIcon.setImageResource(R.drawable.clouds_sun);
            mainWeatherCodeText.setText(R.string.cloudy_sun);
        } else if (weatherCode == 3) {
            mainWeatherCodeIcon.setImageResource(R.drawable.cloud);
            mainWeatherCodeText.setText(R.string.cloudy);
        } else if (weatherCode == 61 || weatherCode == 63 || weatherCode == 65 ||
                weatherCode == 66 || weatherCode == 67 || weatherCode == 80 ||
                weatherCode == 81 || weatherCode == 82 || weatherCode == 85 ||
                weatherCode == 86 || weatherCode == 95 || weatherCode == 96 ||
                weatherCode == 99) {
            mainWeatherCodeIcon.setImageResource(R.drawable.cloud_rain);
            mainWeatherCodeText.setText(R.string.rain);
        } else if (weatherCode == 71 || weatherCode == 73 || weatherCode == 75 ||
                weatherCode == 77) {
            mainWeatherCodeIcon.setImageResource(R.drawable.cloud_snow);
            mainWeatherCodeText.setText(R.string.snow);
        } else {
            mainWeatherCodeIcon.setImageResource(R.drawable.clouds_sun);
            mainWeatherCodeText.setText(R.string.hz);
        }
    }

    private void requestLocationPermission(){
        if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            resultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }else{
            getLocation(myLocation);
        }
    }
    private final ActivityResultLauncher<String> resultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),isGranted->{
        if (isGranted){
            getLocation(myLocation);
        }
    });


    private void getLocation(MyLocation myLocation){
        Log.i(WTH_TAG, "Процесс получения координат из геолокации пошел");
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
            Log.i(WTH_TAG, "Разрешение не дано");
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(ContextCompat.getMainExecutor(requireContext()), location -> {
            if (location!=null){
                double lat=location.getLatitude();
                double lon = location.getLongitude();
                myLocation.setLongitude(lon);
                myLocation.setLatitude(lat);
                Log.i(WTH_TAG, "Новые координаты установлены");
            }
            update(myLocation, weatherDay);
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (myLocation == null) return;
        if (myLocation.isManualLocation()) {
            update(myLocation, weatherDay);
            myLocation.resetManualLocation();
        }else {
                requestLocationPermission();
            }
    }
}
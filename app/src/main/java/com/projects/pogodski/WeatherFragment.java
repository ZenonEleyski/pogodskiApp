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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.projects.pogodski.manager.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class WeatherFragment extends Fragment {
    private TextView mainTemperature;
    private TextView mainLocation;
    private TextView mainWeatherCodeText;
    private ImageView mainWeatherCodeIcon;
    private android.widget.ImageButton reload;
    private TextView loading;
    private static final String LOG_TAG = "weatherfragmentlog";
    private PrefManager prefManager;
    private ActivityResultLauncher<String> locationPermissionLauncher;
    private Day day;
    private ArrayList<String> dailyTemperatures;
    private ArrayList<String> dailyCodes;
    private ArrayList<String> dailyDates;
    private RecyclerView recyclerViewDaily;
    private RecyclerView recyclerViewPerHour;
    private DailyAdapter dailyAdapter;
    private PerHourAdapter perHourAdapter;
    Semaphore semaphore;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weather_fragment, container, false);

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        day = new Day();
        dailyTemperatures = new ArrayList<>();
        dailyCodes = new ArrayList<>();
        dailyDates = new ArrayList<>();

        prefManager = new PrefManager(requireActivity());
        reload = view.findViewById(R.id.reload_btn);
        mainTemperature = view.findViewById(R.id.big_temp_on_main_fragment);
        mainLocation = view.findViewById(R.id.text_location);
        mainWeatherCodeText = view.findViewById(R.id.weather_code_text);
        mainWeatherCodeIcon = view.findViewById(R.id.weather_now);
        loading = view.findViewById(R.id.loading);
        loading.setText("ЗАГРУЗКA ДАННЫХ..");

        recyclerViewDaily = view.findViewById(R.id.recycleview_weather_week);
        recyclerViewDaily.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        LinkedList<Day> dayDaily = new LinkedList<>();
        dailyAdapter = new DailyAdapter(dayDaily);
        recyclerViewDaily.setAdapter(dailyAdapter);

        recyclerViewPerHour = view.findViewById(R.id.recycleview_weather_hour);
        recyclerViewPerHour.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        LinkedList<Day> dayPerHour = new LinkedList<>();
        perHourAdapter = new PerHourAdapter(dayPerHour);
        recyclerViewPerHour.setAdapter(perHourAdapter);

        if (prefManager.getGeoMethod()==null){
            prefManager.setGeoMethod("geolocation");
        }
        semaphore = new Semaphore(0);

        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        updateInfo();
                    }else {
                        setDefaultLocation();
                        updateInfo();
                    }
                }
        );
        setPermission();




        reload.setOnClickListener(v->{
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            v.animate().rotationBy(180).setDuration(300).start();
            updateInfo();
        });

    }

    private void setPermission () {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }


    private void updateInfo(){
        dailyTemperatures.clear();
        dailyCodes.clear();
        Log.i(LOG_TAG, "обновление...");
        semaphore.drainPermits();
        if (prefManager.getGeoMethod().equals("geolocation")){
            getGPS();
        }else{
            semaphore.release();
        }

        Thread th = new Thread(()->{
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                loadLastLocation();
            }
            if (day.getLongitude()==null || day.getLatitude()==null || day.getLongitude().equals("0") || day.getLatitude().equals("0")){setDefaultLocation();}

            StringBuilder responseBody = new StringBuilder();
            try {
                URL url = new URL("https://api.open-meteo.com/v1/forecast?latitude="+day.getLatitude()+"&longitude="+day.getLongitude()+"&daily=weather_code,temperature_2m_max&hourly=temperature_2m,weather_code&current=temperature_2m,weather_code&timezone=Europe%2FMoscow");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Log.i(LOG_TAG, "итоговая ссылка=" + url);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBody.append(line);
                        }
                        reader.close();
                    } catch (IOException err) {Log.e(LOG_TAG, "err="+err.getMessage());loadLastLocation();}//error
                }else{Log.e(LOG_TAG, "err=");loadLastLocation();}//error
            }catch (Exception err){Log.e(LOG_TAG, "err="+err.getMessage());loadLastLocation();}//error

            try {
                JSONObject json = new JSONObject(String.valueOf(responseBody));
                JSONObject dailyJson = json.getJSONObject("daily");
                for (int i = 0; i < dailyJson.getJSONArray("weather_code").length(); i++) {
                    dailyCodes.add(dailyJson.getJSONArray("weather_code").getString(i));
                }
                for (int i = 0; i < dailyJson.getJSONArray("temperature_2m_max").length(); i++) {
                    dailyTemperatures.add(dailyJson.getJSONArray("temperature_2m_max").getString(i));
                }
                for (int i = 0; i < dailyJson.getJSONArray("time").length(); i++) {
                    dailyDates.add(dailyJson.getJSONArray("time").getString(i));
                }

                JSONObject currentJson = json.getJSONObject("current");
                day.setTemperature(String.valueOf(currentJson.getInt("temperature_2m")));
                day.setWeatherCode(currentJson.getString("weather_code"));

                JSONObject perHourJson = json.getJSONObject("hourly");
                ArrayList<String> codes = new ArrayList<>();
                for (int i = 0; i < 24; i++) {
                    codes.add(perHourJson.getJSONArray("weather_code").getString(i));
                }
                ArrayList<String> temps = new ArrayList<>();
                for (int i = 0; i < 24; i++) {
                    temps.add(perHourJson.getJSONArray("temperature_2m").getString(i));
                }
                day.setWeatherCodePerHour(codes);
                day.setTemperaturePerHour(temps);
                Log.i(LOG_TAG,"загружено (в час)="+day.getWeatherCodePerHour());
            } catch (JSONException e) {}

            StringBuilder responseBodyCity = new StringBuilder();
            try{
                URL url = new URL("https://api.bigdatacloud.net/data/reverse-geocode?latitude=" + day.getLatitude() + "&longitude=" + day.getLongitude() + "&localityLanguage=ru&key=bdc_d1fe4960dff048cfa6d9706c7541096d");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBodyCity.append(line);
                        }
                        reader.close();
                    } catch (IOException err) {Log.e(LOG_TAG, "err="+err.getMessage());loadLastLocation();}//error
                }else{
                    Log.e(LOG_TAG, "err resCode="+responseCode);
                    loadLastLocation();}//error
            }catch (Exception err){Log.e(LOG_TAG, "err="+err.getMessage());}

            try {
                JSONObject json = new JSONObject(String.valueOf(responseBodyCity));
                day.setCity(json.getString("city"));
                Log.i(LOG_TAG, "город="+json.getString("city"));
            } catch (JSONException e) {
                loadLastLocation();
                Log.e(LOG_TAG, "err="+e.getMessage());}//error

            saveLastLocation();
            Log.i(LOG_TAG, "текущие данные геолокации");
            Log.i(LOG_TAG, "д="+day.getLongitude());
            Log.i(LOG_TAG, "ш="+day.getLatitude());
            Log.i(LOG_TAG, "код="+day.getWeatherCode());



            LinkedList<Day> dayDaily = new LinkedList<>();
            for (int i=0; i<7;i++){
                Day newDay = new Day(dailyTemperatures.get(i), dailyCodes.get(i), dailyDates.get(i));
                dayDaily.add(newDay);
            }
            LinkedList<Day> dayPerHour = new LinkedList<>();
            for (int i=0; i<24;i++){
                Day newDay = new Day(day.getTemperaturePerHour().get(i), day.getWeatherCodePerHour().get(i), null);
                dayPerHour.add(newDay);
            }


            requireActivity().runOnUiThread(()->{
                updateUI();
                loading.setText("");
                dailyAdapter.update(dayDaily);
                perHourAdapter.update(dayPerHour);
            });
        });
        th.start();


    }
    public void updateUI(){
        int weatherCode = Integer.parseInt(day.getWeatherCode());
        setIcons(weatherCode, mainWeatherCodeIcon, mainWeatherCodeText);

        mainTemperature.setText(String.valueOf(day.getTemperature())+"°C");
        mainLocation.setText(String.valueOf(day.getCity()));
    }
    private void setDefaultLocation(){
        Log.i(LOG_TAG, "поставлена дефолт локация");
        day.setCity("дефолт");
        day.setLongitude("-118");
        day.setLatitude("34");
    }
    private void getGPS(){
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setDefaultLocation();
            Log.i(LOG_TAG, "разрешение не дано, принялись дефолт координаты");
            semaphore.release();
        }else{
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    day.setLatitude(String.valueOf(location.getLatitude()));
                    day.setLongitude(String.valueOf(location.getLongitude()));
                    Log.i(LOG_TAG, "получены координаты из GPS д="+location.getLongitude()+"ш="+location.getLatitude());
                }else{
                    Log.e(LOG_TAG, "координаты не получены, загруженные последние координаты из памяти");
                    loadLastLocation();
                }
                semaphore.release();
            }).addOnFailureListener(e -> {
                loadLastLocation();
                semaphore.release();
            });
        }
    }
    private void saveLastLocation(){
        prefManager.setTemperature(day.getTemperature());
        prefManager.setLatitude(day.getLatitude());
        prefManager.setLongitude(day.getLongitude());
        prefManager.setCity(day.getCity());
        prefManager.setCode(day.getWeatherCode());

        Log.i(LOG_TAG, "сохранены данные геолокации ш="+prefManager.getLatitude()+" д="+prefManager.getLongitude());
    }
    private void loadLastLocation(){
        Log.i(LOG_TAG, "загружены последние координаты");
        if (prefManager.getLatitude()!=null) {
            day.setTemperature(prefManager.getTemperature());
            day.setLatitude(prefManager.getLatitude());
            day.setLongitude(prefManager.getLongitude());
            day.setCity(prefManager.getCity());
            day.setWeatherCode(prefManager.getCode());
        }else{
            setDefaultLocation();
        }
    }

    private void setIcons(int weatherCode, ImageView weatherCodeIcon, TextView weatherCodeText){
        if (weatherCode == 0 || weatherCode == 1) {
            weatherCodeIcon.setImageResource(R.drawable.sun);
            weatherCodeText.setText(R.string.sun);
        } else if (weatherCode == 2) {
            weatherCodeIcon.setImageResource(R.drawable.clouds_sun);
            weatherCodeText.setText(R.string.cloudy_sun);
        } else if (weatherCode == 3) {
            weatherCodeIcon.setImageResource(R.drawable.cloud);
            weatherCodeText.setText(R.string.cloudy);
        } else if (weatherCode == 61 || weatherCode == 63 || weatherCode == 65 ||
                weatherCode == 66 || weatherCode == 67 || weatherCode == 80 ||
                weatherCode == 81 || weatherCode == 82 || weatherCode == 85 ||
                weatherCode == 86 || weatherCode == 95 || weatherCode == 96 ||
                weatherCode == 99 || weatherCode == 53) {
            weatherCodeIcon.setImageResource(R.drawable.cloud_rain);
            weatherCodeText.setText(R.string.rain);
        } else if (weatherCode == 71 || weatherCode == 73 || weatherCode == 75 ||
                weatherCode == 77) {
            weatherCodeIcon.setImageResource(R.drawable.cloud_snow);
            weatherCodeText.setText(R.string.snow);
        } else {
            weatherCodeIcon.setImageResource(R.drawable.clouds_sun);
            weatherCodeText.setText(R.string.hz);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        updateInfo();
    }
}
package com.projects.pogodski;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyLocation implements LocationUtils {

    double longitude;
    private boolean manualLocation = false;
    double latitude;
    String city;
    private final static String LOG_TAG = "locationlog";
    LocationUpdateListener locationUpdateListener;
    private static MyLocation instance;
    public static MyLocation getInstance() {
        if (instance == null) {
            instance = new MyLocation();
        }
        return instance;
    }private MyLocation() {}

    public interface LocationUpdateListener {
        void onSuccess();
        void onError(String error);
    }
    @Override
    public void updateLocation(MyLocation location, LocationUpdateListener locationUpdateListener){
        if (location.getLatitude()==0 && location.getLongitude()==0) {
            location.setLongitude(37.6);
            location.setLatitude(55.75);

        }getCityFromInternet(latitude, longitude, locationUpdateListener);

    }

    public void getCityFromInternet(double lat, double lon, LocationUpdateListener listener){
        new Thread(()->{
            Log.i(LOG_TAG, "Начался процесс получения города...");
            try{
                HttpURLConnection connection = (HttpURLConnection) new URL("https://api-bdc.net/data/reverse-geocode?latitude="+lat+"&longitude="+lon+"&localityLanguage=ru&key=bdc_d1fe4960dff048cfa6d9706c7541096d").openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode==200){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    reader.close();
                    Log.i(LOG_TAG, "Ответ успешно получен!!!");
                    Log.i(LOG_TAG, "Начинается парсинг...");
                    try {
                        JSONObject mainJson = new JSONObject(response.toString());
                        city = mainJson.getString("city").toUpperCase();
                        Log.i(LOG_TAG, "Парсинг закончился успешно, город="+city);

                        if (listener != null) {
                            new android.os.Handler(android.os.Looper.getMainLooper()).post(listener::onSuccess);
                        }

                    }catch (JSONException err){
                        Log.e(LOG_TAG, "Ошибка с парсингом данных месторасположения [ "+err.getMessage()+" ]");

                        if (listener != null) {
                            new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                                    listener.onError("Ошибка: " + err.getMessage()));
                        }

                    }
                }else{

                    Log.e(LOG_TAG, "Ошибка с получением данных месторасположения");
                    if (listener != null) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                                listener.onError("Ошибка с получением данных месторасположения"));
                    }

                }

            }catch (Exception err){
                Log.e(LOG_TAG, "Ошибка с получением данных месторасположения [ "+err.getMessage()+" ]");
                if (listener != null) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                            listener.onError("Ошибка: " + err.getMessage())
                    );
                }
            }
        }).start();
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double lon) {
        longitude = lon;
        Log.i(LOG_TAG, "Долгота была изменена на "+lon);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double lat) {
        latitude = lat;
        Log.i(LOG_TAG, "Широта была изменена на "+lat);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public void setLocationUpdateListener(LocationUpdateListener listener) {
        this.locationUpdateListener = listener;
    }

    public boolean isManualLocation() {
        return manualLocation;
    }

    public void setManualLocation(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
        this.manualLocation = true;
    }
    public void resetManualLocation() {
        manualLocation = false;
    }
}
package com.projects.pogodski;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class WeatherDay implements WeatherUtils{

    Integer[] weatherCodes;
    Integer[] temperatures;
    String[] dates;

    int nowWeatherCode;
    int nowTemperature;

    public interface WeatherUpdateListener {
        void onSuccess();
        void onError(String error);
    }

    @Override
    public void updateWeather(WeatherDay days, MyLocation location, WeatherUpdateListener weatherlistener) {
        String UPD_LOG = "updateWeatherLog";

        location.updateLocation(location, new MyLocation.LocationUpdateListener() {
            @Override
            public void onSuccess() {
                Log.i(UPD_LOG, "Геопозиция получена, город: " + location.getCity());
                //запрос

                new Thread(() -> {
                    try {

                        String lo = Double.toString(location.getLongitude());
                        String la = Double.toString(location.getLatitude());
                        Log.i(UPD_LOG, "Начался процесс получения погодных данных...");
                        HttpURLConnection connection = (HttpURLConnection) new URL("https://api.open-meteo.com/v1/forecast?latitude=" + la + "&longitude=" + lo + "&daily=weather_code,temperature_2m_max&current=temperature_2m,weather_code&timezone=auto").openConnection();
                        connection.setRequestMethod("GET");
                        int responseCode = connection.getResponseCode();
                        if (responseCode == 200) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String line;
                            StringBuilder response = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            reader.close();
                            Log.i(UPD_LOG, "Ответ успешно получен!!!");

                            try {
                                Log.i(UPD_LOG, "Начинается парсинг...");
                                JSONObject mainJson = new JSONObject(response.toString());
                                JSONObject dailyWeather = mainJson.getJSONObject("daily");
                                JSONArray datesArray = dailyWeather.getJSONArray("time");
                                JSONArray codesArray = dailyWeather.getJSONArray("weather_code");
                                JSONArray tempsArray = dailyWeather.getJSONArray("temperature_2m_max");
                                String[] times = new String[datesArray.length()];
                                Integer[] codes = new Integer[codesArray.length()];
                                Integer[] temps = new Integer[tempsArray.length()];
                                for (int i = 0; i < datesArray.length(); i++) {
                                    times[i] = datesArray.getString(i);
                                    codes[i] = codesArray.getInt(i);
                                    temps[i] = tempsArray.getInt(i);
                                }
                                days.setDates(times);
                                days.setWeatherCodes(codes);
                                days.setTemperatures(temps);

                                JSONObject currentWeather = mainJson.getJSONObject("current");
                                nowTemperature = currentWeather.getInt("temperature_2m");
                                nowWeatherCode = currentWeather.getInt("weather_code");

                                Log.i(UPD_LOG, "Успешный парсинг!!");

                                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                                    if (weatherlistener != null) {
                                        weatherlistener.onSuccess();
                                    }
                                });


                            } catch (Exception err) {
                                Log.e(UPD_LOG, "Ошибка с парсингом данных [ " + err.getMessage() + " ]");
                                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                                    if (weatherlistener != null)
                                        weatherlistener.onError("Ошибка парсинга: " + err.getMessage());
                                });
                            }


                        } else {
                            Log.e(UPD_LOG, "Ошибка с передачей данных данных [ ]");
                            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                                if (weatherlistener != null)
                                    weatherlistener.onError("Ошибка с передачей данных данных");
                            });
                        }
                        connection.disconnect();
                    } catch (IOException err) {
                        Log.e(UPD_LOG, "Ошибка при обновлении погоды [ " + err.getMessage() + " ]");
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                            if (weatherlistener != null)
                                weatherlistener.onError("Ошибка при обновлении погоды: " + err.getMessage());
                        });
                    }
                }).start();
            }
            @Override
            public void onError(String error) {
                Log.e(UPD_LOG, "Ошибка геокодирования: " + error);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    if (weatherlistener != null) {
                        weatherlistener.onError("Не удалось определить город: " + error);
                    }
                });
            }
        });
        }




    public Integer[] getWeatherCodes() {
        return weatherCodes;
    }

    public void setWeatherCodes(Integer[] weatherCodes) {
        this.weatherCodes = weatherCodes;
    }

    public Integer[] getTemperatures() {
        return temperatures;
    }

    public void setTemperatures(Integer[] temperatures) {
        this.temperatures = temperatures;
    }

    public String[] getDates() {
        return dates;
    }

    public void setDates(String[] dates) {
        this.dates = dates;
    }

    public int getTemperatures(int index){
        return temperatures[index];
    }

    public int getWeatherCodes(int index){
        return weatherCodes[index];
    }

    public int getNowWeatherCode() {
        return nowWeatherCode;
    }

    public void setNowWeatherCode(int nowWeatherCode) {
        this.nowWeatherCode = nowWeatherCode;
    }

    public int getNowTemperature() {
        return nowTemperature;
    }

    public void setNowTemperature(int nowTemperature) {
        this.nowTemperature = nowTemperature;
    }
}

package com.projects.pogodski;

public interface WeatherCallback {
    void onSuccess(String temperature);
    void onError(String error);
}

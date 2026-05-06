package com.projects.pogodski;


import java.util.ArrayList;

public interface WeatherUtils {

    void updateWeather(WeatherDay day, MyLocation location, WeatherDay.WeatherUpdateListener listener);




}
package com.projects.pogodski;

import java.util.ArrayList;

public class Day {
    String latitude;
    String longitude;
    String temperature;
    String weatherCode;
    ArrayList<String> temperaturePerHour;
    ArrayList<String> weatherCodePerHour;
    String city;
    String date;

    public Day() {
    }

    public Day(String temperature, String weatherCode, String date) {
        this.temperature = temperature;
        this.weatherCode = weatherCode;
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public ArrayList<String> getTemperaturePerHour() {
        return temperaturePerHour;
    }

    public void setTemperaturePerHour(ArrayList<String> temperaturePerHour) {
        this.temperaturePerHour = temperaturePerHour;
    }

    public String getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }

    public ArrayList<String> getWeatherCodePerHour() {
        return weatherCodePerHour;
    }

    public void setWeatherCodePerHour(ArrayList<String> weatherCodePerHour) {
        this.weatherCodePerHour = weatherCodePerHour;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

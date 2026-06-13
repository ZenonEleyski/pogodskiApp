package com.projects.pogodski.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ScrollView;

public class PrefManager {
    private SharedPreferences pref;
    public PrefManager(Context context){
        this.pref = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public void setLatitude (String latitude){
        SharedPreferences.Editor editor = this.pref.edit();
        editor.putString("latitude",latitude);
        editor.apply();
    }
    public String getLatitude (){
        return this.pref.getString("latitude", null);
    }
    public void setLongitude (String longitude){
        SharedPreferences.Editor editor = this.pref.edit();
        editor.putString("longitude",longitude);
        editor.apply();
    }
    public String getLongitude (){
        return this.pref.getString("longitude", null);
    }
    public void setGeoMethod (String method){
        SharedPreferences.Editor editor = this.pref.edit();
        editor.putString("method",method);
        editor.apply();
    }
    public String getGeoMethod (){
        return this.pref.getString("method", null);
    }
    public void setTemperature (String temperature){
        SharedPreferences.Editor editor = this.pref.edit();
        editor.putString("temperature",temperature);
        editor.apply();
    }
    public String getTemperature (){
        return this.pref.getString("temperature", null);
    }
    public void setCity (String city){
        SharedPreferences.Editor editor = this.pref.edit();
        editor.putString("city",city);
        editor.apply();
    }
    public String getCity (){
        return this.pref.getString("city", null);
    }
    public void setCode (String code){
        SharedPreferences.Editor editor = this.pref.edit();
        editor.putString("code",code);
        editor.apply();
    }
    public String getCode (){
        return this.pref.getString("code", null);
    }


}

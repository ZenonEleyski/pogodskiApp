package com.projects.pogodski;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class InfoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View viewfrag = inflater.inflate(R.layout.info_fragment, container, false);

        MaterialSwitch sw = viewfrag.findViewById(R.id.switch_theme);

        SharedPreferences preferences = requireContext().getSharedPreferences("settings", 0);;
        boolean isDark = preferences.getBoolean("dark_mode", false);
        sw.setChecked(isDark);
        AppCompatDelegate.setDefaultNightMode(isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);


        sw.setOnCheckedChangeListener((buttonView, isChecked)->{
            preferences.edit()
                    .putBoolean("dark_mode", isChecked)
                    .apply();
            if (isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });


        return viewfrag;


    }

}

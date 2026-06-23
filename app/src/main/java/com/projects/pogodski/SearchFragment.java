package com.projects.pogodski;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.projects.pogodski.manager.PrefManager;

public class SearchFragment extends Fragment {

    LinearLayout msc;
    LinearLayout stv;
    LinearLayout geo;
    LinearLayout kzn;
    LinearLayout spb;
    LinearLayout dbi;
    LinearLayout rtv;
    LinearLayout klg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        msc = view.findViewById(R.id.moscowbtn);
        stv = view.findViewById(R.id.stavropolbtn);
        geo = view.findViewById(R.id.geobtn);
        kzn = view.findViewById(R.id.kazanbtn);
        spb = view.findViewById(R.id.stpetersburgbtn);
        dbi = view.findViewById(R.id.dubaibtn);
        klg = view.findViewById(R.id.kaliningradbtn);
        rtv = view.findViewById(R.id.rostowbtn);

        PrefManager prefManager = new PrefManager(requireActivity());

        msc.setOnClickListener(v -> {
            prefManager.setGeoMethod("Moscow");
            replaceWithWeatherFragment();
        });

        stv.setOnClickListener(v -> {
            prefManager.setGeoMethod("Stavropol");
            replaceWithWeatherFragment();
        });

        geo.setOnClickListener(v -> {
            prefManager.setGeoMethod("geolocation");
            replaceWithWeatherFragment();
        });

        kzn.setOnClickListener(v -> {
            prefManager.setGeoMethod("Kazan");
            replaceWithWeatherFragment();
        });

        spb.setOnClickListener(v -> {
            prefManager.setGeoMethod("StPetersburg");
            replaceWithWeatherFragment();
        });

        dbi.setOnClickListener(v -> {
            prefManager.setGeoMethod("Dubai");
            replaceWithWeatherFragment();
        });

        klg.setOnClickListener(v -> {
            prefManager.setGeoMethod("Kaliningrad");
            replaceWithWeatherFragment();
        });

        rtv.setOnClickListener(v -> {
            prefManager.setGeoMethod("Rostov");
            replaceWithWeatherFragment();
        });

        return view;
    }
    private void replaceWithWeatherFragment() {
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.main_container);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.action_citySelection_to_weather);
        }
    }
}

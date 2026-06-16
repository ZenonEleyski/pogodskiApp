package com.projects.pogodski;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.projects.pogodski.manager.PrefManager;

public class SearchFragment extends Fragment {

    Button msc;
    Button stv;
    Button geo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        msc = view.findViewById(R.id.moscowbtn);
        stv = view.findViewById(R.id.stavbtn);
        geo = view.findViewById(R.id.geobtn);

        PrefManager prefManager = new PrefManager(requireActivity());

        msc.setOnClickListener(v -> prefManager.setGeoMethod("Moscow"));
        stv.setOnClickListener(v -> prefManager.setGeoMethod("Stavropol"));
        geo.setOnClickListener(v -> prefManager.setGeoMethod("geolocation"));





        return view;
    }
}

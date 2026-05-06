package com.projects.pogodski;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchFragment extends Fragment {

    Button msc;
    Button stv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        msc = view.findViewById(R.id.moscowbtn);
        stv = view.findViewById(R.id.stavbtn);

        MyLocation location = MyLocation.getInstance();
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_menu);

        msc.setOnClickListener(v -> {
            MyLocation.getInstance().setManualLocation(55.7, 37.6);
            bottomNavigationView.setSelectedItemId(R.id.weatherfragment);
        });

        stv.setOnClickListener(v -> {
            MyLocation.getInstance().setManualLocation(45, 42);
            bottomNavigationView.setSelectedItemId(R.id.weatherfragment);
        });



        return view;
    }
}

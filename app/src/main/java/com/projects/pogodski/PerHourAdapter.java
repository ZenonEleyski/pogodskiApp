package com.projects.pogodski;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class PerHourAdapter extends RecyclerView.Adapter<PerHourAdapter.ViewHolder>{
    private final LinkedList<Day> days;

    public PerHourAdapter(LinkedList<Day> days) {
        this.days = days;
    }
    public void update(LinkedList<Day> days) {
        this.days.clear();
        this.days.addAll(days);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_day,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Day day = days.get(position);
        int weatherCode = Integer.parseInt(day.getWeatherCode());
        setIcons(weatherCode, holder.icon);
        double temp = Double.parseDouble(day.getTemperature());
        int intTemp = (int) temp;
        holder.temperature.setText(String.valueOf(intTemp)+"°");
        if(position<10){
            holder.time.setText("0"+position+":00");
        }else{
            holder.time.setText(position+":00");
        }

    }
    @Override
    public int getItemCount() {
        return days.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView temperature;
        private final ImageView icon;
        private final TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            temperature = itemView.findViewById(R.id.temperature);
            icon = itemView.findViewById(R.id.icon);
            time = itemView.findViewById(R.id.time);
        }
    }
    private void setIcons(int weatherCode, ImageView weatherCodeIcon){
        if (weatherCode == 0 || weatherCode == 1) {
            weatherCodeIcon.setImageResource(R.drawable.sun);
        } else if (weatherCode == 2) {
            weatherCodeIcon.setImageResource(R.drawable.clouds_sun);
        } else if (weatherCode == 3) {
            weatherCodeIcon.setImageResource(R.drawable.cloud);
        } else if (weatherCode == 61 || weatherCode == 63 || weatherCode == 65 ||
                weatherCode == 66 || weatherCode == 67 || weatherCode == 80 ||
                weatherCode == 81 || weatherCode == 82 || weatherCode == 85 ||
                weatherCode == 86 || weatherCode == 95 || weatherCode == 96 ||
                weatherCode == 99 || weatherCode == 53) {
            weatherCodeIcon.setImageResource(R.drawable.cloud_rain);
        } else if (weatherCode == 71 || weatherCode == 73 || weatherCode == 75 ||
                weatherCode == 77) {
            weatherCodeIcon.setImageResource(R.drawable.cloud_snow);
        } else {
            weatherCodeIcon.setImageResource(R.drawable.clouds_sun);
        }
    }
}

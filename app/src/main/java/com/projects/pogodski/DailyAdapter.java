package com.projects.pogodski;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder>{
    private final LinkedList<Day> days;
    private static final String LOG_TAG = "weatherfragmentlog";

    public DailyAdapter(LinkedList<Day> days) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_day,parent,false);
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

        String date = day.getDate();
        String[] parts = date.split("-");
        String dayForTv = parts[2];
        String month = parts[1];
        switch (month) {
            case "01":
                holder.date.setText(dayForTv + " Янв");
                break;
            case "02":
                holder.date.setText(dayForTv + " Фев");
                break;
            case "03":
                holder.date.setText(dayForTv + " Мар");
                break;
            case "04":
                holder.date.setText(dayForTv + " Апр");
                break;
            case "05":
                holder.date.setText(dayForTv + " Май");
                break;
            case "06":
                holder.date.setText(dayForTv + " Июн");
                break;
            case "07":
                holder.date.setText(dayForTv + " Июл");
                break;
            case "08":
                holder.date.setText(dayForTv + " Авг");
                break;
            case "09":
                holder.date.setText(dayForTv + " Сен");
                break;
            case "10":
                holder.date.setText(dayForTv + " Окт");
                break;
            case "11":
                holder.date.setText(dayForTv + " Ноя.");
                break;
            case "12":
                holder.date.setText(dayForTv + " Дек");
                break;
            default:
                holder.date.setText(dayForTv + " ???");
                break;
        }

    }
    @Override
    public int getItemCount() {
        return days.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView temperature;
        private final ImageView icon;
        private final TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            temperature = itemView.findViewById(R.id.temperature);
            icon = itemView.findViewById(R.id.icon);
            date = itemView.findViewById(R.id.date);
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

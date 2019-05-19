package com.adesh.weather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.adesh.weather.R;

import java.util.LinkedHashMap;


/**
 * Created by adesh on 17-Aug-18.
 */

public class weatherAdapter extends RecyclerView.Adapter<weatherAdapter.MyViewHolder> {
    //ArrayList<WeatherData> wdata
    LinkedHashMap<String, String> wdata;

    //
    public weatherAdapter(LinkedHashMap<String, String> WeatherData) {
        this.wdata = WeatherData;
    }

    @Override
    public weatherAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weatheradapterlayout, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(weatherAdapter.MyViewHolder holder, int position) {
        //WeatherData WeatherData = wdata.get(position);
        String Keyval = wdata.keySet().toArray()[position].toString();
        if (wdata.get(Keyval) != null) {
            if (!wdata.get(Keyval).isEmpty()) {
                Object Wval = wdata.get(Keyval);
                holder.title.setText(Keyval);
                holder.disc.setText(Wval.toString());
            }
        }
    }

    @Override
    public int getItemCount() {
        return wdata.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, disc;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.rv_title);
            disc = view.findViewById(R.id.rv_value);
        }
    }
}

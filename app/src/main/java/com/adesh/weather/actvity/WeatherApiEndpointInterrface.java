package com.adesh.weather.actvity;

import com.adesh.weather.model.weatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Adesh on 24-Oct-18.
 */

public interface WeatherApiEndpointInterrface {
    // Request method and URL specified in the annotation
    //api.openweathermap.org/data/2.5/weather?lat=35&lon=139&units=metric
    @GET("weather")
    Call<weatherData> getData(@Query("lat") String lat, @Query("lon") String lon, @Query("units") String Tunits, @Query("APPID") String appId);

    @GET("weather")
    Call<weatherData> getData(@Query("q") String CityName, @Query("units") String Tunits, @Query("APPID") String appId);
}
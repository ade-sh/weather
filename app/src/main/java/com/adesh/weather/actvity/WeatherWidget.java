package com.adesh.weather.actvity;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.adesh.weather.R;
import com.adesh.weather.model.weatherData;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setTextViewText(R.id.tvTempWid, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);

        final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        WeatherApiEndpointInterrface apiService =
                retrofit.create(WeatherApiEndpointInterrface.class);
        Call<weatherData> call = apiService.getData("London", "metric", "d98e73113db03e38da802c724582ff29");
        call.enqueue(new Callback<weatherData>() {
            @Override
            public void onResponse(Call<weatherData> call, Response<weatherData> response) {
                //int statusCode = response.code();
                weatherData data = response.body();
                //Log.i("Status Code", statusCode + "");
                //Log.i("Data", data.getSys().getCountry());
                views.setTextViewText(R.id.tvTempWid, data.getMain().getTemp() + "°C ");
                views.setTextViewText(R.id.tvCloudWid, data.getWeather().get(0).getDescription());
            }

            @Override
            public void onFailure(Call<weatherData> call, Throwable t) {
                // Log error here since request failed
                Log.e("prepareData", "on Failure" + t.getLocalizedMessage() + t.getStackTrace());

            }
        });
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


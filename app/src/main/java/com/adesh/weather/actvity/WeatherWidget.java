package com.adesh.weather.actvity;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;

import com.adesh.weather.R;
import com.adesh.weather.model.weatherData;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {
    RemoteViews views;
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setTextViewText(R.id.tvTempWid, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

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
                setImgRes(data.getWeather().get(0).getIcon());
            }

            @Override
            public void onFailure(Call<weatherData> call, Throwable t) {
                // Log error here since request failed
                Log.e("prepareData", "on Failure" + t.getLocalizedMessage() + t.getStackTrace());

            }
        });
    }

    private void setImgRes(String icon) {

        final String BASE_URL = "http://api.openweathermap.org/img/w/" + icon + ".png";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                Log.e(TAG, "onFailure: Well Fuck");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                Log.e(TAG, "onResponse: I am Here");
                InputStream ins = Objects.requireNonNull(response.body()).byteStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(ins);
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
                Log.d(TAG, "onResponse: " + bmp);
                views.setImageViewBitmap(R.id.ivwid, bmp);
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


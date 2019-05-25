package com.adesh.weather.actvity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adesh.weather.R;
import com.adesh.weather.adapter.weatherAdapter;
import com.adesh.weather.model.weatherData;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static LinkedHashMap<String, String> wdata;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    weatherData data;
    private String myCity;
    private ImageView headertImg;
    private weatherAdapter wAdapter;
    private RecyclerView rvWeather;
    private ImageButton btn_ref;
    private AppBarLayout appbar;

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvWeather = findViewById(R.id.rv_main_view);
        wdata = new LinkedHashMap<String, String>();

        appbar = findViewById(R.id.appbar);
        appbar.getLayoutParams().height = (int) (getScreenHeight(MainActivity.this) * 0.80);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1
            );
        }
        headertImg = findViewById(R.id.header);
        wAdapter = new weatherAdapter(wdata);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
        rvWeather.setLayoutManager(llm);
        rvWeather.setAdapter(wAdapter);
        //Setting toolbar name
        toolbar = findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Weather");

        setTBColor(R.drawable.ic_launcher);

        btn_ref = findViewById(R.id.btn_refresh);
        btn_ref.setOnClickListener(v -> {
            Toast t = Toast.makeText(getApplicationContext(), "Refreshing", Toast.LENGTH_SHORT);
            t.show();
            prepareData();
        });
        prepareData();
    }

    public void prepareData() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        WeatherApiEndpointInterrface apiService =
                retrofit.create(WeatherApiEndpointInterrface.class);
        LocationTracker gpsTrac = new LocationTracker(this);
        getCity(Double.parseDouble(gpsTrac.getLatitude()), Double.parseDouble(gpsTrac.getLongitude()));
        Call<weatherData> call = apiService.getData(gpsTrac.getLatitude() + "", gpsTrac.getLongitude() + "", "metric", "d98e73113db03e38da802c724582ff29");

        call.enqueue(new Callback<weatherData>() {
            @Override
            public void onResponse(Call<weatherData> call, Response<weatherData> response) {
                //int statusCode = response.code();
                data = response.body();
                //Log.i("Status Code", statusCode + "");
                //Log.i("Data", data.getSys().getCountry());
                collapsingToolbar.setTitle(data.getMain().getTemp() + "°C  " + data.getWeather().get(0).getDescription());
                convertToHM();

            }

            @Override
            public void onFailure(Call<weatherData> call, Throwable t) {
                // Log error here since request failed
                Log.e("prepareData", "on Failure" + t.getLocalizedMessage() + t.getStackTrace());

            }
        });
    }

    private void convertToHM() {
        String vis = "hello";
        //vis=data.getVisibility().toString();
        wdata.put("Temparature", data.getMain().getTemp().toString() + "°C  ");
        wdata.put("Pressure", data.getMain().getPressure() + "");
        wdata.put("Humidity", data.getMain().getHumidity().toString());
        wdata.put("Min Temp", data.getMain().getTempMin().toString() + "°C  ");
        wdata.put("Max Temp", data.getMain().getTempMax().toString() + "°C  ");
        wdata.put("Visibility", vis);
        wdata.put("Wind Speed", data.getWind().getSpeed().toString());
        wdata.put("Wind Deg", data.getWind().getDeg().toString());
        wdata.put("Clouds All", data.getClouds().getAll().toString());
        java.util.Date dtime = new java.util.Date((long) data.getDt() * 1000);
        wdata.put("Date Time", dtime.toString());
        java.util.Date Suntime = new java.util.Date((long) data.getSys().getSunrise() * 1000);
        wdata.put("Sunrise", Suntime.toString());
        java.util.Date Sunsetime = new java.util.Date((long) data.getSys().getSunset() * 1000);
        wdata.put("Sunset", Sunsetime.toString());
        wdata.put("Country", data.getSys().getCountry());
        wdata.put("City", data.getName());
        wdata.put("Current City", myCity);
        wAdapter.notifyDataSetChanged();
        //TODO:Dynamically add back img via res
        int[] imagesToShow = {R.drawable.ic_launcher, R.drawable.clouds};
        setWeatherImage(imagesToShow, 0);
    }

    private void setWeatherImage(final int[] images, int ImageIndex) {
        int fadeInDuration = 500; // Configure time values here
        int timeBetween = 3000;
        int fadeOutDuration = 1000;

        headertImg.setVisibility(View.VISIBLE);    //Visible or invisible by default - this will apply when the animation ends
        headertImg.setImageResource(images[ImageIndex]);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOut.setStartOffset(fadeInDuration + timeBetween);
        fadeOut.setDuration(fadeOutDuration);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(0);
        headertImg.setAnimation(animation);
        setTBColor(R.drawable.clouds);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (images.length - 1 > ImageIndex) {
                    setWeatherImage(images, ImageIndex + 1); //Calls itself until it gets to the end of the array
                }
            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }
        });

    }

    private void getCity(Double latitude, Double longitude) {

        new Thread(() -> {

            String fnialAddress = "LOL";
            Geocoder geoCoder = new Geocoder(MainActivity.this, Locale.getDefault()); //it is Geocoder
            StringBuilder builder = new StringBuilder();
            try {
                List<Address> address = geoCoder.getFromLocation(latitude, longitude, 1);
                if (address.size() > 0) {
                    int maxLines = address.get(0).getMaxAddressLineIndex();
                    for (int i = 0; i < maxLines; i++) {
                        String addressStr = address.get(0).getAddressLine(i);
                        builder.append(addressStr);
                        builder.append(" ");
                    }
                }
                fnialAddress = builder.toString(); //This is the complete address.
            } catch (
                    IOException e) {
                e.printStackTrace();
            } catch (
                    NullPointerException e) {
                e.printStackTrace();
            }
            myCity = fnialAddress;
            wdata.put("Current City", fnialAddress);
        }).start();
        wAdapter.notifyDataSetChanged();
        Toast toast = Toast.makeText(getApplicationContext(), "Local City " + myCity, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void setTBColor(int res) {
        // Setting toolbarcolor by palette
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                res);
        Palette.from(bitmap).generate(palette -> {
            int mutedColor = palette.getMutedColor(R.attr.colorPrimary);
            collapsingToolbar.setContentScrimColor(mutedColor);

        });
    }
}
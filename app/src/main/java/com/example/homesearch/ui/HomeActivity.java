package com.example.homesearch.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import com.example.homesearch.R;

public class HomeActivity extends AppCompatActivity {

    private static final String FILENAME = "last-homesearch";

    private TextView mTextViewDescriptionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ActionBar actionBar = getSupportActionBar();
        Log.v(Config.LOG_TAG, actionBar != null ? "actionBar not null" : "actionBar null");
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mTextViewDescriptionName = findViewById(R.id.texte_logement1);

        JSONObject response = readFromCache();
        if (response != null) {
            updateUI(response);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getCurrentWeather(String zipCode) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://fablab.mthiolat.fr/link/get_apparts.php";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        storeInCache(response);
                        updateUI(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void updateUI(JSONObject response) {
        try {
            String name = response.getString("name");
            JSONObject DescriptionAppart = response.getJSONObject("DescriptionAppart");
            JSONArray weather = response.getJSONArray("weather");
            JSONObject firstWeather = weather.getJSONObject(0);
            String icon = firstWeather.getString("icon");
            mTextViewDescriptionName.setText(DescriptionAppart);
            //Picasso.get().load("https://openweathermap.org/img/wn/" + icon + "@2x.png").into(mImageViewWeatherCondition);
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    private void storeInCache(JSONObject response) {
        File file = new File(getCacheDir(), FILENAME);
        String sResponse = response.toString();
        try (FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            fos.write(sResponse.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject readFromCache() {
        String contents;
        JSONObject response = null;
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILENAME);
            if (fis == null) {
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        } finally {
            contents = stringBuilder.toString();
        }
        try {
            response = new JSONObject(contents);
        }catch (JSONException err){
            Log.d("Error", err.toString());
        }
        return response;
    }
}
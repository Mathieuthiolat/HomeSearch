package com.example.homesearch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.homesearch.ui.Config;
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
import java.util.List;

import com.example.homesearch.R;

public class HomeActivity extends AppCompatActivity {

    private static final String FILENAME = "last-homesearch";

    private TextView mTextViewDescriptionName;

    private LinearLayout mlistAppart;

    private String DescriptionAppart;
    private String NomAppart;
    private String ImageAppart_1;
    private String DisponibiliteAppart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mlistAppart = (LinearLayout) findViewById(R.id.appart_liste);

        ActionBar actionBar = getSupportActionBar();
        Log.v(Config.LOG_TAG, actionBar != null ? "actionBar not null" : "actionBar null");
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //JSONArray response = readFromCache();
        //getCurrentWeather();
        //if (response != null) {
        //updateUI(response);
        //}

        mTextViewDescriptionName = findViewById(R.id.texte_logement1);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void getAppart() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://fablab.mthiolat.fr/link/get_apparts.php";

        JsonArrayRequest JsonArrayRequest = new JsonArrayRequest (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println(response);
                try {
                    mlistAppart.removeAllViews();
                    for(int i=0; response.length() > i ;i++) {
                        JSONObject firstEntry = response.getJSONObject(i);
                        //DescriptionAppart = firstEntry.getString("DescriptionAppart");
                        NomAppart = firstEntry.getString("NomAppart");
                        //ImageAppart_1 = firstEntry.getString("ImageAppart_1");
                        //DisponibiliteAppart = firstEntry.getString("DisponibiliteAppart");
                        createIU();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                error.printStackTrace();
            }
        });

        queue.add(JsonArrayRequest);
    }

    public void createIU(){
        TextView mtextViewTitle = new TextView(new ContextThemeWrapper(HomeActivity.this, R.style.CustomKeyTextAppartementName));
        mtextViewTitle.setText(NomAppart);

        //com.google.android.material.switchmaterial.SwitchMaterial mkeyValeur = new com.google.android.material.switchmaterial.SwitchMaterial(new ContextThemeWrapper(HomeActivity.this, R.style.CustomOpenAppartement));
        //mkeyValeur.setText(ValeurClee);

        LinearLayout mtextHorizontal = new LinearLayout(new ContextThemeWrapper(HomeActivity.this, R.style.CustomLinearLayoutKey));
        mtextHorizontal.setOrientation(LinearLayout.HORIZONTAL);

        mtextHorizontal.addView(mtextViewTitle);
        //mtextHorizontal.addView(mkeyValeur);


//        mtextHorizontal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                /*Intent intent = new Intent(ProfileActivity.this, DetailsJob.class);
//                Bundle y = new Bundle();
//                y.putString("key", postID);
//                intent.putExtras(y);
//                startActivity(intent);
//                finish();*/
//            }
//        });
        mlistAppart.addView(mtextHorizontal);
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

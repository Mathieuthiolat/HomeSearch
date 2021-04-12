package com.example.homesearch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class DetailAppartActivity extends AppCompatActivity {

    private Integer UserId;
    private Integer Appart_Id ;
    private Integer Appart_Proprio ;
    private String Appart_Nom ;
    private String Appart_Desc ;
    private String Appart_Adress;
    private String Appart_Cat ;
    private String Appart_Capa ;
    private String Appart_Img ;

    private TextView mTextAppart_Nom ;
    private TextView mTextAppart_Desc ;
    private TextView mTextAppart_Adress;
    private TextView mTextAppart_Cat ;
    private TextView mTextAppart_Capa ;
    private ImageView mTextAppart_Img ;

    private ImageButton navExplore;
    private ImageButton navAccount;

    private Button btnReservation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_appart);

//        /** Action bar bugé **/
//        Intent intent = getIntent();
//        String message = intent.getStringExtra(MainActivity.MESSAGE_KEY);
//
//        ActionBar actionBar = getSupportActionBar();
//        Log.v(Config.LOG_TAG, actionBar != null ? "actionBar not null" : "actionBar null");
//        if (actionBar != null)
//        {
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        //Les listener pour changer d'activité
        navExplore = findViewById(R.id.page_explore);
        navExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailAppartActivity.this, MainActivity.class));
            }
        });

        navAccount = findViewById(R.id.page_account);
        navAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailAppartActivity.this, ProfileActivity.class));
            }
        });


        btnReservation = findViewById(R.id.detail_appart_btnReserver);
        btnReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject currentUser = readFromCache();
                if (currentUser != null) {
                    try {
                        UserId = currentUser.getInt("user_id");
                        createKey();

                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }else{
                    com.google.android.material.textfield.TextInputLayout searcheBar = findViewById(R.id.home_search_bar);
                    RelativeLayout contextView = findViewById(R.id.detail_appart_page);
                    Snackbar.make(searcheBar, R.string.ajoutsClee_OK, Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getColor(R.color.warning))
                            .setAnchorView(contextView)
                            .show();
                }
            }

        });


        Bundle y = getIntent().getExtras();
        String value = "-1";
        value = y.getString("id_appart");
        ApiProcess(value);

    }
    /** Create clee **/
    private void createKey(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://fablab.mthiolat.fr/link/add_reservation.php?user_id="+UserId+"&fiche_appart_id="+Appart_Id+"&role_cle=locataire";
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    com.google.android.material.textfield.TextInputLayout searcheBar = findViewById(R.id.home_search_bar);
                    RelativeLayout contextView = findViewById(R.id.detail_appart_page);
                    try {
                        if(response.getBoolean("succes")){
                            Snackbar.make(contextView, R.string.ajoutsClee_OK, Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(getColor(R.color.succes))
                                    .setAnchorView(searcheBar)
                                    .show();

                        }
                    } catch (Exception e) {
                        e.getStackTrace();
                        Snackbar.make(contextView, R.string.ajoutsClee_Fail, Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getColor(R.color.warning))
                                .setAnchorView(searcheBar)
                                .show();
                    }


                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.toString());
                }
            });
        queue.add(JsonObjectRequest);

    }

    private JSONObject readFromCache() {
        String contents;
        JSONObject response = null;
        FileInputStream fis = null;
        try {
            fis = openFileInput("user_info");
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

    private void ApiProcess(String appartId){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://fablab.mthiolat.fr/link/get_single_appart.php?appart_Id="+appartId;
        JsonArrayRequest JsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject firstEntry = response.getJSONObject(0);

                            Appart_Id = firstEntry.getInt("Id_appart");
                            Appart_Nom = firstEntry.getString("NomAppart");
                            Appart_Desc = firstEntry.getString("DescriptionAppart");
                            Appart_Adress = firstEntry.getString("AdresseAppart");
                            Appart_Cat = firstEntry.getString("CategorieAppart");
                            Appart_Capa = firstEntry.getString("CapaciteAppart");
                            Appart_Img = firstEntry.getString("ImageAppart_1");
                            updateUI();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
                );
        queue.add(JsonArrayRequest);
    }

    private void updateUI() {
        try {
            mTextAppart_Img = findViewById(R.id.detail_appart_img);
            Picasso.get().load(Appart_Img).into(mTextAppart_Img);

            mTextAppart_Nom = findViewById(R.id.detail_appart_titre);
            mTextAppart_Nom.setText(Appart_Nom);

            mTextAppart_Desc = findViewById(R.id.detail_appart_desc);
            mTextAppart_Desc.setText(Appart_Desc);

            mTextAppart_Adress = findViewById(R.id.detail_appart_address);
            mTextAppart_Adress.setText(Appart_Adress);

            mTextAppart_Cat = findViewById(R.id.detail_appart_cat);
            mTextAppart_Cat.setText(Appart_Cat);

            mTextAppart_Capa = findViewById(R.id.detail_appart_capa);
            mTextAppart_Capa.setText(Appart_Capa);

        } catch (Exception e) {
            e.getStackTrace();
        }
    }
//    /** Bouton retours en arriere **/
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                // API 5+ solution
//                finish();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

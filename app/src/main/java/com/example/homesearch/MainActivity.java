package com.example.homesearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
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
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private static final String FILENAME = "last-homesearch";

    private LinearLayout mlistAppart;

    private String DescriptionAppart;
    private String NomAppart;
    private String ImageAppart_1;
    private String DisponibiliteAppart;
    private String Id_appart;
    public static String MESSAGE_KEY = "hello";

    private ImageButton navExplore;
    private ImageButton navAppart;
    private ImageButton navAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mlistAppart = (LinearLayout) findViewById(R.id.appart_liste);

        getAppart();


        //Les listener pour changer d'activité
        navExplore = findViewById(R.id.page_explore);
        navExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });
//        navAppart = findViewById(R.id.page_appart);
//        navAppart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //startActivity(new Intent(ProfileActivity.this, AppartActivity.class));
//            }
//        });
        navAccount = findViewById(R.id.page_account);
        navAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

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
                        NomAppart = firstEntry.getString("NomAppart");
                        DescriptionAppart = firstEntry.getString("DescriptionAppart");
                        ImageAppart_1 = firstEntry.getString("ImageAppart_1");
                        Id_appart = firstEntry.getString("Id_appart");
                        //DisponibiliteAppart = firstEntry.getString("DisponibiliteAppart");
                        createIU(Id_appart);
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

    public void createIU(String appartId){

        TextView mtextViewTitle = new TextView(new ContextThemeWrapper(MainActivity.this, R.style.CustomTextAppartementTitle));
        mtextViewTitle.setText(NomAppart);

        TextView mtextViewDescription = new TextView(new ContextThemeWrapper(MainActivity.this, R.style.CustomTextAppartementDescription));
        mtextViewDescription.setText(DescriptionAppart);

        //com.google.android.material.switchmaterial.SwitchMaterial mkeyValeur = new com.google.android.material.switchmaterial.SwitchMaterial(new ContextThemeWrapper(HomeActivity.this, R.style.CustomOpenAppartement));
        //mkeyValeur.setText(ValeurClee);

        LinearLayout mtextHorizontal = new LinearLayout(new ContextThemeWrapper(MainActivity.this, R.style.CustomLinearLayoutHorizontal));
        mtextHorizontal.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout mtextVertical = new LinearLayout(new ContextThemeWrapper(MainActivity.this, R.style.CustomLinearLayoutHorizontal));
        mtextVertical.setOrientation(LinearLayout.VERTICAL);

        //Button midappart = new Button(new ContextThemeWrapper(HomeActivity.this, R.style.CustomTextAppartementDescription));
        mtextHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, DetailAppartActivity.class);
                Bundle y = new Bundle();
                y.putString("id_appart", appartId);
                intent.putExtras(y);
                startActivity(intent);
                finish();
            }
        });

        if(ImageAppart_1 != null && !ImageAppart_1.equals("")){
            ImageView imageappart = new ImageView(new ContextThemeWrapper(MainActivity.this, R.style.CustomKeyTextAppartementName));
            Picasso.get().load(ImageAppart_1).into(imageappart);
            mtextHorizontal.addView(imageappart);
        }

        mtextVertical.addView(mtextViewTitle);
        mtextVertical.addView(mtextViewDescription);
        //mtextVertical.addView(midappart);
        mtextHorizontal.addView(mtextVertical);
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
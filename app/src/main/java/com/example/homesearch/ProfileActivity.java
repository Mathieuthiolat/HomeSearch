package com.example.homesearch;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

public class ProfileActivity extends AppCompatActivity {

    private static final String FILENAME = "user_info";

    private ImageButton navExplore;
    private ImageButton navAppart;
    private ImageButton navAccount;

    private Button mButtonSubmitConnexion;
    private Button mButtonSubmitInscription;

    private ImageButton bouttonDeconnexion;
    private ImageButton bouttonEdition;

    private TextView userName;

    private LinearLayout accountExtraBtn;
    private LinearLayout profileLayout;
    private LinearLayout formConnexion;

    private EditText mTextInputUserName;
    private EditText mTextInputPassword;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        formConnexion = findViewById(R.id.account_form_connexion);

        JSONObject oldResponse = readFromCache();
        if (oldResponse != null) {
            processeResponse(oldResponse);
        }

        System.out.println(readFromCache());

        mButtonSubmitConnexion = findViewById(R.id.btn_connexion);
        mButtonSubmitConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                tryConnexion();
            }
        });
        mButtonSubmitInscription = findViewById(R.id.btn_inscription);
        mButtonSubmitInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                tryInscription();
            }
        });

        bouttonDeconnexion = findViewById(R.id.logout_btn);
        bouttonDeconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deconnexion();
            }
        });

        //Les listener pour changer d'activité
        navExplore = findViewById(R.id.page_explore);
        navExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
        navAppart = findViewById(R.id.page_appart);
        navAppart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(ProfileActivity.this, AppartActivity.class));
            }
        });
        navAccount = findViewById(R.id.page_account);
        navAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
            }
        });
    }

    public void tryInscription(){
        mTextInputUserName = findViewById(R.id.user_name);
        String sUserName = mTextInputUserName.getText().toString();

        mTextInputPassword = findViewById(R.id.user_passwordConfirm);
        String passWord = md5(mTextInputPassword.getText().toString());

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://fablab.mthiolat.fr/link/try_inscription.php?user_nom="+sUserName+"&user_mdp="+passWord;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                RelativeLayout contextView = findViewById(R.id.main_page_acccount);

                try {
                    if(response.getBoolean("succes")){
                        Snackbar.make(contextView, response.getString("error"), Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getColor(R.color.warning))
                                .setAnchorView(formConnexion)
                                .show();
                    }else{
                        Snackbar.make(contextView, R.string.inscription_OK, Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getColor(R.color.succes))
                                .setAnchorView(formConnexion)
                                .show();
                        processeResponse(response);

                    }
                } catch (Exception e) {
                    e.getStackTrace();
                    Snackbar.make(contextView, R.string.inscription_Fail, Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getColor(R.color.warning))
                            .setAnchorView(formConnexion)
                            .show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                error.printStackTrace();
                RelativeLayout contextView = findViewById(R.id.main_page_acccount);
                Snackbar.make(contextView, R.string.inscription_Fail, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getColor(R.color.warning))
                        .setAnchorView(formConnexion)
                        .show();
            }
        });

        queue.add(jsonObjectRequest);
    }

    public void tryConnexion(){
        mTextInputUserName = findViewById(R.id.user_name);
        String sUserName = mTextInputUserName.getText().toString();

        mTextInputPassword = findViewById(R.id.user_passwordConfirm);
        String passWord = md5(mTextInputPassword.getText().toString());

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://fablab.mthiolat.fr/link/check_connexion.php?user_nom="+sUserName+"&user_mdp="+passWord;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                processeResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                error.printStackTrace();
                notConnectedUiUpdate();
            }
        });

        queue.add(jsonObjectRequest);
    }
    public void deconnexion() {
        File file = new File(getCacheDir(), FILENAME);
        deleteFile(FILENAME);
        profileLayout = findViewById(R.id.basic_user_info);
        profileLayout.setVisibility(View.INVISIBLE);

        mTextInputUserName = findViewById(R.id.user_name);
        mTextInputUserName.setText("");

        mTextInputPassword = findViewById(R.id.user_passwordConfirm);
        mTextInputPassword.setText("");

        accountExtraBtn = findViewById(R.id.account_extra_btn);
        accountExtraBtn.setVisibility(View.INVISIBLE);

        formConnexion.setVisibility(View.VISIBLE);
    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void processeResponse(JSONObject response) {
        RelativeLayout contextView = findViewById(R.id.main_page_acccount);
        if(response != null){
            try {
                storeInCache(response);
                String user_nom = response.getString("user_nom");
                updateUI(user_nom);
                Snackbar.make(contextView, R.string.connexion_OK, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getColor(R.color.succes))
                        .setAnchorView(formConnexion)
                        .show();

            } catch (Exception e) {
                e.getStackTrace();
                notConnectedUiUpdate();
            }
        }else{
            notConnectedUiUpdate();
        }

    }

    private void notConnectedUiUpdate() {
        RelativeLayout contextView = findViewById(R.id.main_page_acccount);
        Snackbar.make(contextView, R.string.connexion_Fail, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.warning))
                .setAnchorView(formConnexion)
                .show();
    }

    private void updateUI(String nom_user) {
        try {
            profileLayout = findViewById(R.id.basic_user_info);
            profileLayout.setVisibility(View.VISIBLE);

            accountExtraBtn = findViewById(R.id.account_extra_btn);
            accountExtraBtn.setVisibility(View.VISIBLE);

            formConnexion = findViewById(R.id.account_form_connexion);
            formConnexion.setVisibility(View.INVISIBLE);
            userName = findViewById(R.id.profil_userName);
            userName.setText(nom_user);
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

    private static final String md5(final String password) {
        try {

            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}

package com.example.homesearch;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProfileActivity extends AppCompatActivity {

    public static final String FILENAME = "user_info";
    private Integer UserId;
    private String UserPass;
    private String NomAppart;
    private String ValeurClee;
    private String NomUser;
    private String RoleClee;

    private ImageButton navExplore;
    private ImageButton navAppart;
    private ImageButton navAccount;

    private Button mButtonSubmitConnexion;
    private Button mButtonSubmitInscription;

    private ImageButton bouttonDeconnexion;
    private ImageButton bouttonBluetooth;

    private TextView userName;

    private LinearLayout layoutConnected;
    private LinearLayout accountExtraBtn;
    private LinearLayout profileLayout;
    private LinearLayout formConnexion;
    private LinearLayout mlistKey ;

    private EditText mTextInputUserName;
    private EditText mTextInputPassword;

    private BluetoothAdapter mBlueAdapter;
    TextView mPairedTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        formConnexion = findViewById(R.id.account_form_connexion);

        JSONObject oldResponse = readFromCache();
        if (oldResponse != null) {
            processeResponse(oldResponse);
        }
        mlistKey = (LinearLayout) findViewById(R.id.appart_key_liste);

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

        //Bouton bluetooth
        bouttonBluetooth = findViewById(R.id.bluetoothActive);
        bouttonBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
                //startActivity(new Intent(ProfileActivity.this, GetBluetooth.class));
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
                        Snackbar.make(contextView, R.string.inscription_OK, Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getColor(R.color.succes))
                                .setAnchorView(formConnexion)
                                .show();
                        processeResponse(response);

                    }else{
                        Snackbar.make(contextView, response.getString("error"), Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getColor(R.color.warning))
                                .setAnchorView(formConnexion)
                                .show();

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

        RelativeLayout contextView = findViewById(R.id.main_page_acccount);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://fablab.mthiolat.fr/link/check_connexion.php?user_nom="+sUserName+"&user_mdp="+passWord;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                Snackbar.make(contextView, R.string.connexion_OK, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getColor(R.color.succes))
                        .setAnchorView(formConnexion)
                        .show();
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
    public void getKey() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://fablab.mthiolat.fr/link/get_own_key.php?user_id="+UserId+"&user_mdp="+UserPass;

        JsonArrayRequest JsonArrayRequest = new JsonArrayRequest (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println(response);
                try {
                    mlistKey.removeAllViews();
                    for(int i=0; response.length() > i ;i++) {
                        JSONObject firstEntry = response.getJSONObject(i);
                        String IdAppart = firstEntry.getString("IdAppart");
                        NomUser = firstEntry.getString("NomUser");
                        NomAppart = firstEntry.getString("NomAppart");
                        RoleClee = firstEntry.getString("RoleClee");
                        ValeurClee = firstEntry.getString("ValeurClee");
                        createIU(IdAppart);
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
    public void deleteKey(String IdAppart) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://fablab.mthiolat.fr/link/delete_reservation.php?user_id="+UserId+"&fiche_appart_id="+IdAppart;
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        com.google.android.material.textfield.TextInputLayout searcheBar = findViewById(R.id.home_search_bar);
                        RelativeLayout contextView = findViewById(R.id.main_page_acccount);
                        try {
                            if(response.getBoolean("succes")){
                                Snackbar.make(contextView, R.string.supprClee_OK, Snackbar.LENGTH_SHORT)
                                        .setBackgroundTint(getColor(R.color.succes))
                                        .setAnchorView(formConnexion)
                                        .show();
                                getKey();
                            }
                        } catch (Exception e) {
                            e.getStackTrace();
                            Snackbar.make(contextView, R.string.supprClee_Fail, Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(getColor(R.color.warning))
                                    .setAnchorView(formConnexion)
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
    public void createIU(String IdAppart){
        TextView mtextViewTitle = new TextView(ProfileActivity.this, null ,0 , R.style.CustomKeyTextAppartementName);
        mtextViewTitle.setText(NomAppart);

        Button mDeleteButton = new Button(ProfileActivity.this, null ,0 ,R.style.CustomDeleteKey);
        mDeleteButton.setText("Supprimer");
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                deleteKey(IdAppart);
            }
        });

        com.google.android.material.switchmaterial.SwitchMaterial mkeyValeur = new com.google.android.material.switchmaterial.SwitchMaterial(new ContextThemeWrapper(ProfileActivity.this, R.style.CustomOpenAppartement));

        LinearLayout mtextHorizontal = new LinearLayout(ProfileActivity.this, null, 0, R.style.CustomLinearLayoutKey);
        mtextHorizontal.setOrientation(LinearLayout.HORIZONTAL);

        mtextHorizontal.addView(mtextViewTitle);
        mtextHorizontal.addView(mkeyValeur);
        mtextHorizontal.addView(mDeleteButton);

        mlistKey.addView(mtextHorizontal);
    }


    public void deconnexion() {
        File file = new File(getCacheDir(), FILENAME);
        deleteFile(FILENAME);

        mTextInputUserName = findViewById(R.id.user_name);
        mTextInputUserName.setText("");

        mTextInputPassword = findViewById(R.id.user_passwordConfirm);
        mTextInputPassword.setText("");

        accountExtraBtn = findViewById(R.id.account_extra_btn);
        accountExtraBtn.setVisibility(View.INVISIBLE);

        layoutConnected = findViewById(R.id.connected_layout);
        layoutConnected.setVisibility(View.INVISIBLE);

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
        if(response != null){
            try {
                storeInCache(response);
                UserId = response.getInt("user_id");
                NomUser = response.getString("user_nom");
                UserPass = response.getString("user_mdp");
                getKey();
                updateUI(NomUser);

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
            layoutConnected = findViewById(R.id.connected_layout);
            layoutConnected.setVisibility(View.VISIBLE);

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

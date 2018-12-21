package com.victor.oprica.quyzygy20;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private EditText et_email, et_password;
    private CheckBox cb_rememberme;
    private String email, password;
    private Button btn_login;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login = (Button) findViewById(R.id.btn_login);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        cb_rememberme = (CheckBox) findViewById(R.id.cb_rememberuser);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        readLoginData();

    }

        public void readLoginData (){ //verifica daca a fost bifat remember me si actioneaza ca atare
            saveLogin = loginPreferences.getBoolean("saveLogin", false);
            if (saveLogin == true) {
                et_email.setText(loginPreferences.getString("username", ""));
                et_password.setText(loginPreferences.getString("password", ""));
                cb_rememberme.setChecked(true);
            }
        }

        public void navigateToSignup (View view){ //trimite spre view-ul de sign up
            Intent explicitIntent = new Intent(MainActivity.this, SignupActivity.class);
            startActivityForResult(explicitIntent, 1);
        }

        public void checkuser (View view) throws JSONException, NoSuchAlgorithmException { // metoda ce va fi apelata inainte de save login pt a verifica daca userul si parola se afla in bd
            if (et_email.getText().toString().isEmpty()){ // verificam in baza de date daca exista conturile
                et_email.setText("");
                et_email.setHint("Insert username");
                //Toast.makeText(getApplicationContext(),"Insert username.", Toast.LENGTH_LONG).show();
            }
            else if(et_password.getText().toString().isEmpty()){
                et_password.setText("");
                et_password.setHint("Insert password");
                //Toast.makeText(getApplicationContext(),"Insert password.", Toast.LENGTH_LONG).show();
            }
            else if(true){ //conditie de verificare daca datele coincid cu ce e in BD
                saveLogin(view);
                login();
            }
            else{
                Toast.makeText(getApplicationContext(),"Incorrect username or password.", Toast.LENGTH_LONG).show();
            }
        }


        public void saveLogin(View view) { //salveaza local userul si parola daca a fost bifat remember me
            if (view == btn_login) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_email.getWindowToken(), 0);

                email = et_email.getText().toString();
                password = et_password.getText().toString();

                if (cb_rememberme.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("username", email);
                    loginPrefsEditor.putString("password", password);
                    loginPrefsEditor.commit();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }
            }
        }

        public void login() throws NoSuchAlgorithmException { //metoda ce va trimite spre activitatea urmatoare de intrare in "camera"



            String loginhash = Sha.hash256(et_password.getText().toString());

            try {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String URL = "http://quyzygy.us/login?email=" + et_email.getText().toString() + "&passwordHash=" + loginhash;

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                        String[] temp = response.split("\"");

                        loginPrefsEditor.putString("sk", temp[3]);
                        loginPrefsEditor.commit();

                        if (true){
                            Intent explicitIntent = new Intent(MainActivity.this, ProffesorBoardActivity.class);
                            startActivityForResult(explicitIntent, 1);
                        }else {
                            Intent explicitIntent = new Intent(MainActivity.this, EnterRoomActivity.class);
                            startActivityForResult(explicitIntent, 1);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }
                };

                requestQueue.add(stringRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


}

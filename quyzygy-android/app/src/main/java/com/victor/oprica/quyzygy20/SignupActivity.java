package com.victor.oprica.quyzygy20;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText ed_email, ed_firstName, ed_lastName, ed_pw, ed_pw2;
    RadioGroup rg_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ed_email = (EditText)findViewById(R.id.et_email);
        ed_firstName = (EditText)findViewById(R.id.et_firstName);
        ed_lastName = (EditText)findViewById(R.id.et_lastName);
        ed_pw = (EditText)findViewById(R.id.et_password);
        ed_pw2 = (EditText) findViewById(R.id.et_password2);
        rg_type = (RadioGroup)findViewById(R.id.rg_accountType);

    }

    public void navigateToMainActivity(View view){
        Intent explicitIntent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(explicitIntent);
    }

    public void register(View view) throws JSONException {

        if(ed_email.getText().toString().isEmpty()){
            ed_email.setText("");
            ed_email.setHint("Username is empty");
        }
        else if (ed_email.getText().toString().length() < 6){
            ed_email.setText("");
            ed_email.setHint("Username too short");
        }
        else if (ed_email.getText().toString().length() >32){
            ed_email.setText("");
            ed_email.setHint("Username too long");
        }
        else if (ed_email.getText().toString().contains(" ")){
            ed_email.setText("");
            ed_email.setHint("No space allowed");
        }
        else if (ed_pw.getText().toString().isEmpty()){
            ed_pw.setText("");
            ed_pw.setHint("Password is empty");
        }
        else if (ed_pw.getText().toString().length() < 6){
            ed_pw.setText("");
            ed_pw.setHint("Pasword too short");
        }
        else if (ed_pw.getText().toString().length() >32){
            ed_pw.setText("");
            ed_pw.setHint("Password too long");
        }
        else if (ed_pw.getText().toString().contains(" ")){
            ed_pw.setText("");
            ed_pw.setHint("No space allowed");
        }
        else if (!ed_pw2.getText().toString().equals(ed_pw.getText().toString())){
            ed_pw2.setText("");
            ed_pw2.setHint("Password missmatch");
            //Toast.makeText(getApplicationContext(),"Password missmatch", Toast.LENGTH_SHORT).show();
        }
        else {
            //save to DB



            String url = "https://webtech-opricavictor.c9users.io:8080/register";

//create post data as JSONObject - if your are using JSONArrayRequest use obviously an JSONArray :)
            JSONObject jsonBody = new JSONObject("{\"firstName\":\"" + ed_firstName.getText() +
                                                 "\",\"lastName\":\"" + ed_lastName.getText() +
                                                 "\",\"email\":\"" + ed_email.getText() +
                                                 "\",\"passwordHash\":\"" + ed_pw.getText() +
                                                 "\",\"userType\":\"" + ((RadioButton)findViewById(rg_type.getCheckedRadioButtonId())).getText().toString() + "\"}");


//request a json object response
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    //now handle the response
                    Toast.makeText(SignupActivity.this, "response", Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    //handle the error
                    Toast.makeText(SignupActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();

                }
            }) {    //this is the part, that adds the header to the request
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    //params.put("x-vacationtoken", "secret_token");
                    params.put("content-type", "application/json");
                    return params;
                }
            };

// Add the request to the queue
            Volley.newRequestQueue(this).add(jsonRequest);


        }
    }


}

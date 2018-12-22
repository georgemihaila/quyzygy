package com.victor.oprica.quyzygy20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.security.*;
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

    public void register(View view) throws JSONException, NoSuchAlgorithmException {

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

            String url = "http://quyzygy.us/register";

            String hash = Sha.hash256(ed_pw.getText().toString());

            try {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String URL = "http://quyzygy.us/register";
                final String requestBody = "{\"firstName\":\"" + ed_firstName.getText().toString() +
                        "\",\"lastName\":\"" + ed_lastName.getText().toString() +
                        "\",\"email\":\"" + ed_email.getText().toString() +
                        "\",\"passwordHash\":\"" + hash +
                        "\",\"userType\":\"" + ((RadioButton)findViewById(rg_type.getCheckedRadioButtonId())).getText().toString() + "\"}";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEY", response);
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

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String responseString = "";
                        if (response != null) {
                            responseString = String.valueOf(response.statusCode);
                            // can get more details such as response.headers
                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };

                requestQueue.add(stringRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }






        }
    }


}




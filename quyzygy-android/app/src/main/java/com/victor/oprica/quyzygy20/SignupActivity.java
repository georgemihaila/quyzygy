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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class SignupActivity extends AppCompatActivity {

    EditText ed_username, ed_pw, ed_pw2;
    RadioGroup rg_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ed_username = (EditText)findViewById(R.id.et_username);
        ed_pw = (EditText)findViewById(R.id.et_password);
        ed_pw2 = (EditText) findViewById(R.id.et_password2);
        rg_type = (RadioGroup)findViewById(R.id.rg_accountType);

    }

    public void navigateToMainActivity(View view){
        Intent explicitIntent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(explicitIntent);
    }

    public void register(View view){

        if(ed_username.getText().toString().isEmpty()){
            ed_username.setText("");
            ed_username.setHint("Username is empty");
        }
        else if (ed_username.getText().toString().length() < 6){
            ed_username.setText("");
            ed_username.setHint("Username too short");
        }
        else if (ed_username.getText().toString().length() >32){
            ed_username.setText("");
            ed_username.setHint("Username too long");
        }
        else if (ed_username.getText().toString().contains(" ")){
            ed_username.setText("");
            ed_username.setHint("No space allowed");
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
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://192.168.0.103/SignUp?username=" + ed_username.getText() + "&password=" + ed_pw.getText() + "&usertype=" + (((RadioButton)findViewById(rg_type.getCheckedRadioButtonId())).getText().toString());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String type = ((RadioButton)findViewById(rg_type.getCheckedRadioButtonId())).getText().toString();

                            Toast.makeText(getApplicationContext(),ed_username.getText().toString() + " " + ed_pw.getText().toString() + " " + type , Toast.LENGTH_LONG).show();

                            navigateToMainActivity(getCurrentFocus());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
            });
            queue.add(stringRequest);


        }
    }
}

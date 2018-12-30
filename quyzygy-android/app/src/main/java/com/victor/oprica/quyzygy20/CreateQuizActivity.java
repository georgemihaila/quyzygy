package com.victor.oprica.quyzygy20;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;

public class CreateQuizActivity extends AppCompatActivity {

    private LinearLayout parentLinearLayout;
    SharedPreferences loginPreferences;
    EditText et_quizName, et_duration, et_courseName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
    }
    public void onAddQuestion(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.view_question, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
    }

    public void onDeleteQuestion(View v) {
        parentLinearLayout.removeView((View) v.getParent().getParent());
    }


    public void saveQuiz (View v){



        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://quyzygy.us/createQuiz?sk=" + loginPreferences.getString("sk", "");
            et_quizName = (EditText)findViewById(R.id.et_quizName);
            et_courseName = (EditText)findViewById(R.id.et_courseName);

            final String requestBody = "{" + "\"ID\":\"" + 0 + "\"" +
                    "\"Author\":\"" + loginPreferences.getString("username","").toString() +
                    "\",\"CourseName\":\"" + et_courseName.getText().toString() +
                    "\",\"Questions\":\"" + 10000001 + "\"}";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    Toast.makeText(getApplicationContext(), "It worked", Toast.LENGTH_SHORT).show();
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
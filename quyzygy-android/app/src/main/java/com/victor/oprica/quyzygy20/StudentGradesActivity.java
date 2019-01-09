package com.victor.oprica.quyzygy20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.victor.oprica.quyzygy20.entities.LoginResult;
import com.victor.oprica.quyzygy20.entities.QuizResult;

import java.util.ArrayList;

public class StudentGradesActivity extends AppCompatActivity {
    private SharedPreferences keyPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_grades);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            keyPreferences = getSharedPreferences("keyPrefs", MODE_PRIVATE);
            String URL = "http://quyzygy.us/myGrades?sk=" + keyPreferences.getString("sk", "0");

            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    QuizResult[] results = new Gson().fromJson(response, QuizResult[].class);
                    ArrayList<String> arrayList = new ArrayList<>();
                    ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_activated_1, arrayList);
                    ListView listView = (ListView)findViewById(R.id.gradesListView);
                    listView.setAdapter(stringArrayAdapter);
                    for(int i = 0; i < results.length; i++){
                        String s = results[i].Date.toString();
                        s += "\nQuiz #" + results[i].QuizID;
                        s += "\nPoints: " + results[i].Value;
                        arrayList.add(s);
                        stringArrayAdapter.notifyDataSetChanged();
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

    public void navigateToBoard(View view) {
        Intent explicitIntent = new Intent(StudentGradesActivity.this, EnterRoomActivity.class);
        startActivity(explicitIntent);
    }
}

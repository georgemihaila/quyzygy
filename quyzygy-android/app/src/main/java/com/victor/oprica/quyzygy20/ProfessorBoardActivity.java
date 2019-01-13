package com.victor.oprica.quyzygy20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.victor.oprica.quyzygy20.entities.Quiz;
import com.victor.oprica.quyzygy20.entities.QuizResult;

import java.util.ArrayList;

public class ProfessorBoardActivity extends AppCompatActivity {

    ArrayList<String> arrayList;
    ArrayAdapter<String> stringArrayAdapter;
    private SharedPreferences keyPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_board);
        arrayList = new ArrayList<>();
        stringArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_activated_1, arrayList);
        ListView listView = (ListView)findViewById(R.id.quizzes_ListView);
        listView.setAdapter(stringArrayAdapter);
        arrayList.add("Loading...");
        stringArrayAdapter.notifyDataSetChanged();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            keyPreferences = getSharedPreferences("keyPrefs", MODE_PRIVATE);
            String URL = "http://quyzygy.us/myQuizzes?sk=" + keyPreferences.getString("sk", "0");

            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    arrayList.clear();
                    stringArrayAdapter.notifyDataSetChanged();
                    Quiz[] results = new Gson().fromJson(response, Quiz[].class);
                    if (results.length == 0){
                        arrayList.add("You have no quizzes.");
                        stringArrayAdapter.notifyDataSetChanged();
                    }
                    else {
                        for (int i = 0; i < results.length; i++) {
                            String s = "Quiz #" + results[i].ID;
                            s += "\nName: \"" + results[i].QuizName + "\"";
                            s += "\nCourse: \"" + results[i].CourseName + "\"";
                            s += "\nDuration: " + results[i].Duration / 60 + " minutes";
                            arrayList.add(s);
                            stringArrayAdapter.notifyDataSetChanged();
                        }
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

    public void navigateToCreateQuiz(View view){
        Intent explicitIntent = new Intent(ProfessorBoardActivity.this, CreateQuizActivity.class);
        startActivity(explicitIntent);
    }

    public void navigateToMainActivity(View view){
        Intent explicitIntent = new Intent(ProfessorBoardActivity.this, MainActivity.class);
        startActivity(explicitIntent);
    }
}

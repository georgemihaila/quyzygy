package com.victor.oprica.quyzygy20;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StudentGradesActivity extends AppCompatActivity {
    private SharedPreferences keyPreferences;
    private QuizResult[] quizResults;
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
                    quizResults = results;
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
        ((Button)findViewById(R.id.exportBtn)).setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String q = "";
                for (int i = 0; i < quizResults.length; i++){
                    String s = quizResults[i].Date.toString();
                    s += "\nQuiz #" + quizResults[i].QuizID;
                    s += "\nPoints: " + quizResults[i].Value;
                    q += s + "\n\n";
                }
                generateNoteOnSD(getApplicationContext(), "results.txt", q);
            }
        });
    }

    public void navigateToBoard(View view) {
        Intent explicitIntent = new Intent(StudentGradesActivity.this, EnterRoomActivity.class);
        startActivity(explicitIntent);
    }
    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Documents");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved to Documents/" + sFileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Please enable storage permissions for the application.", Toast.LENGTH_LONG).show();
        }
    }
}

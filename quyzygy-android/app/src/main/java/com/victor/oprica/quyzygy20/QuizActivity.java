package com.victor.oprica.quyzygy20;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.victor.oprica.quyzygy20.entities.Identity;
import com.victor.oprica.quyzygy20.entities.LoginResult;
import com.victor.oprica.quyzygy20.entities.Question;
import com.victor.oprica.quyzygy20.entities.WSCPWQ;
import com.victor.oprica.quyzygy20.entities.WebSocketClientPacket;
import com.victor.oprica.quyzygy20.entities.WebSocketServerPacket;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class QuizActivity extends AppCompatActivity {

    Button answer1, answer2, answer3, answer4;

    TextView tv_time, question;
    private SharedPreferences keyPreferences;
    private Questions mQuestions = new Questions();
    private String mAnswer;
    private int mScore = 0;
    private int mQuestionsLength = mQuestions.mQuestions.length;
    ProgressBar pb_answers, pb_time;
    LinearLayout answersLayout;
    int index;
    private long quiz_time = 1200 * 1000; //cand vom lua din bd se va inmulti cu 1000
    private Identity identity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        index = 0;
        keyPreferences = getSharedPreferences("keyPrefs", MODE_PRIVATE);
        identity = new Identity();
        identity.WSID = keyPreferences.getString("wsid", "");
        identity.AccessCode = Integer.parseInt(keyPreferences.getString("acc", ""));
        identity.SecretKey = keyPreferences.getString("sk", "");
        pb_answers = (ProgressBar) findViewById(R.id.pb_answers);
        pb_answers.setMax(mQuestionsLength);
        pb_time = (ProgressBar) findViewById(R.id.pb_time);
        pb_time.setMax((int)quiz_time - 1000);
        pb_time.setProgress((int)quiz_time - 1000);
        answersLayout = (LinearLayout)findViewById(R.id.answers_Layout);
        question = (TextView) findViewById(R.id.question);

        tv_time = (TextView) findViewById(R.id.tv_time);

        new CountDownTimer(quiz_time, 50) {

            public void onTick(long millisUntilFinished) {
                tv_time.setText("Time remaining: " +
                        millisUntilFinished / 3600000 + " hours " +
                        millisUntilFinished % 3600000 / 60000 + " minutes " +
                        millisUntilFinished % 60000 / 1000 + " seconds");
                pb_time.setProgress((int)millisUntilFinished);
            }

            public void onFinish() {
               gameOver();
            }
        }.start();
        nextQuestion();
    }

    private void gameOver(){ //to be modified
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QuizActivity.this);
        alertDialogBuilder.setMessage("Game Over! Your score is " + mScore + " points.")
                .setCancelable(false)
                .setNegativeButton("EXIT",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    Question currentQuestion;
    private void nextQuestion(){
        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://quyzygy.us/nextQuestion?sk=" + identity.SecretKey + "&ac=" + identity.AccessCode + "&wsid=" + identity.WSID;
            Log.v("getting next question", URL);
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, URL, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        Log.e("questionresponse", response);
                        if (response.contains("Completed!")){
                            Intent explicitIntent = new Intent(getApplicationContext(), StudentGradesActivity.class);
                            startActivityForResult(explicitIntent, 1);
                            Toast.makeText(getApplicationContext(), "Quiz completed!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        WSCPWQ packet = new Gson().fromJson(response, WSCPWQ.class);
                        question.setText(packet.Data.Text);
                        currentQuestion = packet.Data;
                        if (packet.Data.Type.equals("SingleAnswer")){
                            String[] answers = new Gson().fromJson(packet.Data.Answers, String[].class);
                            for (int  i = 0; i < answers.length; i++){
                                Button button = new Button(getApplicationContext());
                                button.setText(answers[i]);
                                button.setTextSize(22);
                                answersLayout.addView(button, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                button.setOnClickListener(new Button.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        postAnswer(((Button)v).getText().toString());
                                    }
                                });
                            }
                        }
                        else if (packet.Data.Type.equals("OpenAnswer")){
                            final EditText et = new EditText(getApplicationContext());
                            answersLayout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            Button button = new Button(getApplicationContext());
                            button.setText("Answer");
                            button.setTextSize(22);
                            answersLayout.addView(button, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            button.setOnClickListener(new Button.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    postAnswer(et.getText().toString());
                                }
                            });
                        }
                        else if (packet.Data.Type.equals("MultipleAnswer")){

                        }
                    }
                    catch (Exception ee){
                        Log.e("ParseError", ee.toString());
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
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
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(com.android.volley.Request<Object> request) {
                    requestQueue.getCache().clear();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postAnswer(String answer){
        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://quyzygy.us/postAnswer?sk=" + identity.SecretKey + "&ac=" + identity.AccessCode + "&wsid=" + identity.WSID;
            final String requestBody = "{\"questionID\":"+
                    currentQuestion.id
                    + ",\"answer\":\"" + answer + "\"}";
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, URL, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        Log.e("questionresponse", response);
                          if (answersLayout.getChildCount() > 0)
                                  answersLayout.removeAllViews();
                          nextQuestion();
                    }
                    catch (Exception ee){
                        Log.e("questionresponse", ee.toString());
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
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
            };
            requestQueue.add(stringRequest);
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(com.android.volley.Request<Object> request) {
                    requestQueue.getCache().clear();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

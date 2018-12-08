package com.victor.oprica.quyzygy20;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class QuizActivity extends AppCompatActivity {

    Button answer1, answer2, answer3, answer4;

    TextView tv_time, question;

    private Questions mQuestions = new Questions();

    private String mAnswer;
    private int mScore = 0;
    private int mQuestionsLength = mQuestions.mQuestions.length;
    ProgressBar pb_answers, pb_time;
    int index;
    private long quiz_time = 20 * 1000; //cand vom lua din bd se va inmulti cu 1000

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        index = 0;

        pb_answers = (ProgressBar) findViewById(R.id.pb_answers);
        pb_answers.setMax(mQuestionsLength);

        pb_time = (ProgressBar) findViewById(R.id.pb_time);
        pb_time.setMax((int)quiz_time - 1000);
        pb_time.setProgress((int)quiz_time - 1000);

        answer1 = (Button) findViewById(R.id.answer1);
        answer2 = (Button) findViewById(R.id.answer2);
        answer3 = (Button) findViewById(R.id.answer3);
        answer4 = (Button) findViewById(R.id.answer4);

        question = (TextView) findViewById(R.id.question);

        updateQuestion(index++);

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

        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < mQuestionsLength)
                {
                    if (answer1.getText().toString() == mAnswer) {
                        mScore++;
                        updateQuestion(index++);
                    } else {
                        updateQuestion(index++);
                    }
                }
                else{
                    if (answer1.getText().toString() == mAnswer) {
                        mScore++;
                    }
                    gameOver();
                }
                pb_answers.incrementProgressBy(1);
            }
        });

        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < mQuestionsLength) {
                    if (answer2.getText().toString() == mAnswer) {
                        mScore++;
                        updateQuestion(index++);
                    } else {
                        updateQuestion(index++);
                    }
                }
                else{
                    if (answer2.getText().toString() == mAnswer) {
                        mScore++;
                    }
                    gameOver();
                }
                pb_answers.incrementProgressBy(1);
            }
        });

        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < mQuestionsLength)
                {
                    if(answer3.getText().toString() == mAnswer){
                        mScore++;
                        updateQuestion(index++);
                    }
                    else{
                        updateQuestion(index++);
                    }
                }
                else{
                    if (answer3.getText().toString() == mAnswer) {
                        mScore++;
                    }
                    gameOver();
                }
                pb_answers.incrementProgressBy(1);
            }
        });

        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < mQuestionsLength) {
                    if (answer4.getText().toString() == mAnswer) {
                        mScore++;
                        updateQuestion(index++);
                    } else {
                        updateQuestion(index++);
                    }
                }
                else{
                    if (answer4.getText().toString() == mAnswer) {
                        mScore++;
                    }
                    gameOver();
                }
                pb_answers.incrementProgressBy(1);
            }
        });

    }

    private void updateQuestion(int num){
        question.setText(mQuestions.getQuestion(num));
        answer1.setText(mQuestions.getChoice1(num));
        answer2.setText(mQuestions.getChoice2(num));
        answer3.setText(mQuestions.getChoice3(num));
        answer4.setText(mQuestions.getChoice4(num));
        mAnswer = mQuestions.getCorrectAnswer(num);
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
}

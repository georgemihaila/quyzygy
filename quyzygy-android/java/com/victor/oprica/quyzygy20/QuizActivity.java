package com.victor.oprica.quyzygy20;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class QuizActivity extends AppCompatActivity {

    Button answer1, answer2, answer3, answer4;

    TextView score, question;

    private Questions mQuestions = new Questions();

    private String mAnswer;
    private int mScore = 0;
    private int mQuestionsLength = mQuestions.mQuestions.length;

    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        index = 0;

        answer1 = (Button) findViewById(R.id.answer1);
        answer2 = (Button) findViewById(R.id.answer2);
        answer3 = (Button) findViewById(R.id.answer3);
        answer4 = (Button) findViewById(R.id.answer4);

        score = (TextView) findViewById(R.id.score);
        question = (TextView) findViewById(R.id.question);

        updateQuestion(index++);

        score.setText("Score: " + mScore);

        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < mQuestionsLength)
                {
                    if (answer1.getText().toString() == mAnswer) {
                        mScore++;
                        score.setText("Score: " + mScore);
                        updateQuestion(index++);
                    } else {
                        updateQuestion(index++);
                    }
                }
                else{
                    gameOver();
                }
            }
        });

        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < mQuestionsLength) {
                    if (answer2.getText().toString() == mAnswer) {
                        mScore++;
                        score.setText("Score: " + mScore);
                        updateQuestion(index++);
                    } else {
                        updateQuestion(index++);
                    }
                }
                else{
                    gameOver();
                }
            }
        });

        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < mQuestionsLength)
                {
                    if(answer3.getText().toString() == mAnswer){
                        mScore++;
                        score.setText("Score: " + mScore);
                        updateQuestion(index++);
                    }
                    else{
                        updateQuestion(index++);
                    }
                }
                else{
                    gameOver();
                }
            }
        });

        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < mQuestionsLength) {
                    if (answer4.getText().toString() == mAnswer) {
                        mScore++;
                        score.setText("Score: " + mScore);
                        updateQuestion(index++);
                    } else {
                        updateQuestion(index++);
                    }
                }
                else{
                    gameOver();
                }
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

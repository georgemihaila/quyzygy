package com.victor.oprica.quyzygy20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ProffesorBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proffesor_board);
    }

    public void navigateToMainActivity(View view){
        Intent explicitIntent = new Intent(ProffesorBoardActivity.this, MainActivity.class);
        startActivity(explicitIntent);
    }

    public void navigateToQuiz(View view){
        Intent explicitIntent = new Intent(ProffesorBoardActivity.this, MainActivity.class);
        startActivity(explicitIntent);
    }
}

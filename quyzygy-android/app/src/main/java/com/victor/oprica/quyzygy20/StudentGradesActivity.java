package com.victor.oprica.quyzygy20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StudentGradesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_grades);
    }

    public void navigateToBoard(View view) {
        Intent explicitIntent = new Intent(StudentGradesActivity.this, EnterRoomActivity.class);
        startActivity(explicitIntent);
    }
}

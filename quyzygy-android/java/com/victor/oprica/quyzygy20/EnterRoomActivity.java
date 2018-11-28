package com.victor.oprica.quyzygy20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EnterRoomActivity extends AppCompatActivity {

    EditText et_invite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_room);
        Toast.makeText(getApplicationContext(),"Invite code: pistoale",Toast.LENGTH_LONG).show();
        et_invite = (EditText)findViewById(R.id.et_invitecode);
    }

    public void enterRoom(View view) {
        if (et_invite.getText().toString().equals("pistoale")) { //check for code thing
            navigateToQuiz(view);
        }
        else{
            Toast.makeText(getApplicationContext(), "Incorrect invite code", Toast.LENGTH_LONG).show();
        }
    }

    public void navigateToQuiz(View view) {
        Intent explicitIntent = new Intent(EnterRoomActivity.this, QuizActivity.class);
        startActivity(explicitIntent);
        et_invite.setText("");
    }

    public void navigateToMainActivity(View view){
        Intent explicitIntent = new Intent(EnterRoomActivity.this, MainActivity.class);
        startActivity(explicitIntent);
    }
}

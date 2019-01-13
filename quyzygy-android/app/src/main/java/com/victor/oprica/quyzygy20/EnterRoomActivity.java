package com.victor.oprica.quyzygy20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.victor.oprica.quyzygy20.entities.Identity;
import com.victor.oprica.quyzygy20.entities.WebSocketClientPacket;
import com.victor.oprica.quyzygy20.entities.WebSocketServerPacket;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class EnterRoomActivity extends AppCompatActivity {

    EditText et_invite;
    Button joinBtn;
    TextView output;
    private SharedPreferences keyPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_room);
        //Toast.makeText(getApplicationContext(),"Invite code: pistoale",Toast.LENGTH_LONG).show();
        et_invite = (EditText)findViewById(R.id.et_invitecode);
        joinBtn = (Button)findViewById(R.id.btn_invitecode);
        output = (TextView)findViewById(R.id.outputtv);
        identity = new Identity();
        keyPreferences = getSharedPreferences("keyPrefs", MODE_PRIVATE);
        identity.SecretKey = keyPreferences.getString("sk", "");
    }

    public void enterRoom(View view) {
        identity.AccessCode = Integer.parseInt(et_invite.getText().toString());
        et_invite.setEnabled(false);
        joinBtn.setEnabled(false);
        start();
    }

    public void navigateToQuiz(View view) {
        Intent explicitIntent = new Intent(EnterRoomActivity.this, QuizActivity.class);
        startActivity(explicitIntent);
        et_invite.setText("");
    }

    public void navigateToStudentGrades(View view) {
        Intent explicitIntent = new Intent(EnterRoomActivity.this, StudentGradesActivity.class);
        startActivity(explicitIntent);
    }

    public void navigateToMainActivity(View view){
        Intent explicitIntent = new Intent(EnterRoomActivity.this, MainActivity.class);
        startActivity(explicitIntent);
    }
    private OkHttpClient client;
    private Identity identity;
    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        public Identity identity;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            WebSocketClientPacket packet = new WebSocketClientPacket();
            packet.Identity = this.identity;
            packet.Action = "JoinQuiz";
            String s = packet.toJson();
            webSocket.send(s);
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output(text);
            WebSocketServerPacket packet = new Gson().fromJson(text, WebSocketServerPacket.class);
            if (packet.Success){
                if (identity.WSID == null || identity.WSID == ""){
                    identity.WSID = packet.Data;
                }
                else{
                    if (packet.Action != null && packet.Action.equals("QuizStarted")){
                        webSocket.close(NORMAL_CLOSURE_STATUS, null);
                        enableControls();
                        navigateToQuiz(identity);
                    }
                }
            }
            else{
                if (packet.Data.equals("Invalid quiz ID")){
                    invalidQuiz();
                }
                webSocket.close(NORMAL_CLOSURE_STATUS, null);
                enableControls();
            }
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Receiving bytes : " + bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing : " + code + " / " + reason);
            enableControls();
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.getMessage());
        }
    }
    private void start() {
        output.setText("");
        client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://79.115.188.173:8082").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        listener.identity = this.identity;
        WebSocket ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    private void enableControls(){
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                et_invite.setEnabled(true);
                joinBtn.setEnabled(true);
            }
        });
    }

    private void invalidQuiz(){
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                et_invite.setEnabled(true);
                joinBtn.setEnabled(true);
                et_invite.setText("");
                et_invite.setHint("Invalid quiz ID");
            }
        });
    }

    private void navigateToQuiz(final Identity i){
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                et_invite.setEnabled(true);
                joinBtn.setEnabled(true);
                identity = i;
                Toast.makeText(getApplicationContext(), "Quiz started!", Toast.LENGTH_LONG).show();
                Intent explicitIntent = new Intent(EnterRoomActivity.this, QuizActivity.class);
                startActivityForResult(explicitIntent, 1);
            }
        });
    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(output.getText().toString() + "\n\n" + txt);
            }
        });
    }
}

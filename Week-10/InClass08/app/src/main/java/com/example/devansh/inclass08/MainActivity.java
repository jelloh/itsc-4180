package com.example.devansh.inclass08;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private final OkHttpClient client = new OkHttpClient();

    String API_LOGINURL = "http://ec2-54-164-74-55.compute-1.amazonaws.com/api/login";
    UserToken token = null;
    public static String TOKEN_KEY = "tokenehehehehehehehehehehehe";
    EditText editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Chat Room");

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        findViewById(R.id.btnSignUp).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnLogin){
            Log.d("test", "onClick: LOGIN WAS CLICKED");

            String email = "", password = "";

            email = "" + ((TextView)findViewById(R.id.editTextEmail)).getText();
            password = "" + ((TextView)findViewById(R.id.editTextPassword)).getText();

            loginUser(email, password);

        }else if(view.getId() == R.id.btnSignUp){
            Log.d("test", "onClick: SIGNUP WAS CLICKED");
            goToSignup();


        }else{
            Log.d("test", "onClick: SOMETHING RANDOM WAS CLICKED");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Login Method
    ///////////////////////////////////////////////////////////////////////////

    public void loginUser(String email, String pass)  {

        RequestBody formBody = new FormBody.Builder()
                .add("email", email )
                .add("password", pass)
                .build();

        Request request = new Request.Builder()
                .url(API_LOGINURL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                printToast("Unable to Connect to Server");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Gson gson = new Gson();

                if(response.isSuccessful()){
                    Log.d("test", "Successfull YAYY");

                    token = gson.fromJson(response.body().string(), UserToken.class);
                    goToChatroom();
                }
                else{
                    Message msg = gson.fromJson(response.body().string(), Message.class);
                    printToast(msg.message);
                }
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // GoTo Methods (MessageThreads and SignUp)
    ///////////////////////////////////////////////////////////////////////////

    private void goToChatroom(){
        Intent intent = new Intent(MainActivity.this, MessageThreads.class);
        intent.putExtra(TOKEN_KEY, token);

        startActivity(intent);

        for(int i = 0; i < 100000; i++)
            for(int j = 0; j < 100; j++);

        editTextEmail.post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.editTextEmail)).setText("");
                ((TextView) findViewById(R.id.editTextPassword)).setText("");
            }
        });

        //finish(); // ---------------------------------------------------------------- DO we actually need this?
                   // ---------------------------------------------------------------- Instructions says to "finish" the login screen
                  // ---------------------------------------------------------------- but then we can't go back to this (it exits instead when back is pushed)
    }

    private void goToSignup(){
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        startActivity(intent);
        // finish();
    }


    ///////////////////////////////////////////////////////////////////////////
    // Supporting Methods
    ///////////////////////////////////////////////////////////////////////////

    void printToast(final String msg){
        editTextEmail.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class Message{
        String status, message;


    }

}

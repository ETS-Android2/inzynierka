package com.example.filip_derbin_praca_inzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button btn_Login, btn_Register;
    EditText login, password;
    AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_Login = findViewById(R.id.loginBtn);
        btn_Register = findViewById(R.id.btnRegister);
        login = findViewById(R.id.loginText);
        password = findViewById(R.id.passwordText);
        activity = this;

        btn_Login.setOnClickListener(v -> {
            String tempLog = login.getText().toString();
            String tempPass = password.getText().toString();
            checkIfUserIsValid(tempLog, tempPass);
        });
        btn_Register.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    public void checkIfUserIsValid(String login, String password) {
        String urlPOSTUser = "http://192.168.1.104:8080/SERVER/tutorials/acc";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("login", login);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPOSTUser,
             postData,
             response -> {
                 Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                 startActivity(intent);
             },
             error -> {
                 Toast.makeText(activity, "Invalid login or password !", Toast.LENGTH_LONG).show();
             });
        requestQueue.add(jsonObjectRequest);
    }
}
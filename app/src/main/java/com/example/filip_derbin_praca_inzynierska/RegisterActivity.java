package com.example.filip_derbin_praca_inzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class RegisterActivity extends AppCompatActivity {

    Button reg, back;
    EditText login, password;
    AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg = findViewById(R.id.btnReg);
        back = findViewById(R.id.btnBack);
        login = findViewById(R.id.login_Text);
        password = findViewById(R.id.password_Text);
        activity = this;

        reg.setOnClickListener(v -> {
            String tempLogin = login.getText().toString();
            String tempPass = password.getText().toString();
            sendDataToDB(tempLogin, tempPass);
        });
        back.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }

    public void sendDataToDB(String login, String password) {
        String urlPOSTUser = "http://192.168.1.104:8080/SERVER/temp/addUser";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("login", login);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPOSTUser,
             postData, response -> {
                 Toast.makeText(activity, "Registration complete successful !", Toast.LENGTH_LONG).show();
                 new CountDownTimer(3000, 1000) {
                     public void onTick(long milisUntilFinished) {

                     }

                     public void onFinish() {
                         Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                         startActivity(intent);
                     }
                 }.start();
             }, error -> Toast.makeText(activity, "Something went wrong, please try again !", Toast.LENGTH_LONG).show());
        requestQueue.add(jsonObjectRequest);
    }
}
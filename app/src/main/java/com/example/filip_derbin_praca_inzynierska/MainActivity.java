package com.example.filip_derbin_praca_inzynierska;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
  private static final String ONESIGNAL_APP_ID = "7ad6bdb1-1e9a-4380-9945-1d1dfc4fdf52";
  Button btn_Login;
  EditText login, password;
  AppCompatActivity activity;
  String tempLog, tempPass;
  Gson gson;

  public void onClick(View v) {
    startActivity(new Intent(this, RegisterActivity.class));
    overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    setContentView(R.layout.activity_main);
    OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

    // OneSignal Initialization
    OneSignal.initWithContext(this);
    OneSignal.setAppId(ONESIGNAL_APP_ID);

    btn_Login = findViewById(R.id.loginBtn);
    login = findViewById(R.id.login_Text);
    password = findViewById(R.id.editTextPassword);
    activity = this;
    gson = new Gson();

    btn_Login.setOnClickListener(v -> {
      tempLog = login.getText().toString();
      tempPass = password.getText().toString();
      checkIfUserIsValid(tempLog, tempPass);
    });

  }

  public void checkIfUserIsValid(String login, String password) {
    String urlPOSTUser = "http://localhost:8080/SERVER/temp/corpName";
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    JSONObject postData = new JSONObject();
    try {
      postData.put("login", login);
      postData.put("password", password);
      postData.put("isLogged", true);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPOSTUser,
      postData,
      response -> {
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        try {
          String corpName = response.getString("currentCorpName");
          String userID = response.getString("currentUserID");
          String corpID = response.getString("currentCorpID");
          String userIDOrganization = response.getString("currentUserIDOrganization");
          intent.putExtra("corpName", corpName);
          intent.putExtra("userID", userID);
          intent.putExtra("corpID", corpID);
          intent.putExtra("userIDOrg", userIDOrganization);
          intent.putExtra("login", tempLog);
          intent.putExtra("password", tempPass);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        startActivity(intent);
        finish();
      },
      error -> {
        Toast.makeText(activity, "Invalid login or password !", Toast.LENGTH_LONG).show();
      });
    requestQueue.add(jsonObjectRequest);
  }
}
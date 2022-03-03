package com.example.filip_derbin_praca_inzynierska;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity {

    Button registerCorp;
    EditText organizationName, secret,
      howManyUsersAcc, minThresholdToDecode;
    AppCompatActivity activity;
    byte[] decodedString;
    Bitmap bitmap;
    int howMany;

    public void onLoginClick(View v) {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerCorp = findViewById(R.id.btnReg);
        organizationName = findViewById(R.id.login_Text);
        secret = findViewById(R.id.secret_Text);
        howManyUsersAcc = findViewById(R.id.usersAccNumber_Text);
        minThresholdToDecode = findViewById(R.id.decodeThreshold_Text);
        activity = this;

        registerCorp.setOnClickListener(v -> {
            String tempOrganizationName = organizationName.getText().toString();
            String tempSecret = secret.getText().toString();
            String tempHowManyUsersAcc = howManyUsersAcc.getText().toString();
            String tempMinThresholdToDecode = minThresholdToDecode.getText().toString();
            if (tempOrganizationName.isEmpty() || tempSecret.isEmpty() || tempHowManyUsersAcc.isEmpty() || tempMinThresholdToDecode.isEmpty()) {
                Toast.makeText(activity, "Content can't be empty !", Toast.LENGTH_LONG).show();
            } else {
                createCorpTable(tempOrganizationName, tempSecret, tempHowManyUsersAcc,
                  tempMinThresholdToDecode);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        createUsersTable(tempOrganizationName, tempSecret, tempHowManyUsersAcc);
                    }
                }, 5000);
            }
        });
    }

    public void createCorpTable(String organizationName, String secret, String howManyUsersAcc,
                                String minThresholdToDecode) {
        String urlPOSTUser = "http://192.168.1.104:8080/SERVER/temp/addCorp";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("corpName", organizationName);
            postData.put("secret", secret);
            postData.put("howManyUsers", howManyUsersAcc);
            postData.put("thresholdToDecode", minThresholdToDecode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPOSTUser,
          postData,
          response -> {
              Toast.makeText(activity, "Corp registration complete successful !", Toast.LENGTH_LONG).show();
          }, error -> Toast.makeText(activity, "Something went wrong, please try again !", Toast.LENGTH_LONG).show());
        requestQueue.add(jsonObjectRequest);
    }

    public void createUsersTable(String organizationName, String secret, String howManyUsersAcc) {
        String urlPOSTUser = "http://192.168.1.104:8080/SERVER/temp/addUsers";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("corpName", organizationName);
            postData.put("secret", secret);
            postData.put("howManyUsers", howManyUsersAcc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPOSTUser,
          postData,
          response -> {
              JSONObject array = response;
              Log.d("response from server: ", array.toString());
              try {
                  JSONArray jsonArray = array.getJSONArray("arrayOfQrs");
                  JSONArray jsonArrayWithLogins = array.getJSONArray("usersAcc");
                  List<String> tempList = new ArrayList<>();
                  List<String> usersLoginList = new ArrayList<>();
                  for (int i = 0; i < Integer.parseInt(howManyUsersAcc); i++) {
                      tempList.add(jsonArray.getString(i));
                      usersLoginList.add(jsonArrayWithLogins.getString(i));
                  }
                  int size = tempList.size();
                  String[] tempStringArray = tempList.toArray(new String[size]);
                  String[] usersStringArray = usersLoginList.toArray(new String[size]);
                  for (int i = 0; i < tempStringArray.length; i++) {
                      tempStringArray[i] = tempStringArray[i].replace("data:image/png;base64,", "");
                      saveQR(tempStringArray[i], usersStringArray[i]);
                  }
                  for (String s : tempStringArray) Log.d("String : ", s);
              } catch (JSONException e) {
                  e.printStackTrace();
              }
              Toast.makeText(activity, "Users accounts generated successful !", Toast.LENGTH_LONG).show();
          }, error -> Toast.makeText(activity, "Something went wrong, please try again !", Toast.LENGTH_LONG).show());
        requestQueue.add(jsonObjectRequest);
    }

    public void saveQR(String text, String loginName) {
        decodedString = Base64.decode(text, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, loginName, loginName);
    }
}
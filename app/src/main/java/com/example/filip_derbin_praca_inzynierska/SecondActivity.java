package com.example.filip_derbin_praca_inzynierska;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.onesignal.OSNotificationAction;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SecondActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_IMAGE_FROM_NOTIFICATION = 2;
    // Set this to change time between sending request to server
    // ( now it's 1 request - 5 min ( 60000 - 1 min ) )
    private static final int TIME = 300000;
    TextView corpName, secret;
    String filePath, encodedString, corp, userID,
      corpID, userIDOrganization, tempLogin, tempPassword;
    AppCompatActivity activity;
    Boolean check = true, sendRequest = false;
    String tempID, tempRequestID;

    public void onChooseQRClick(View v) {
        Intent i = new Intent(
          Intent.ACTION_PICK,
          android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void onLogOutClick(View v) {
        userLogOut(tempLogin, tempPassword);
        Intent intent = new Intent(SecondActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        corpName = findViewById(R.id.corpName);
        secret = findViewById(R.id.secret);
        Bundle extras = getIntent().getExtras();
        corp = extras.getString("corpName");
        userID = extras.getString("userID");
        corpID = extras.getString("corpID");
        tempLogin = extras.getString("login");
        tempPassword = extras.getString("password");
        userIDOrganization = extras.getString("userIDOrg");
        String temp = corpName.getText() + "\n" + corp;
        corpName.setText(temp);
        activity = this;
        OneSignal.setNotificationOpenedHandler(
          result -> {
              String actionTopic = result.getNotification().getTitle();
              String actionId = result.getAction().getActionId();
              if (actionId.equals("id1") && corp.equals(actionTopic)) {
                  Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  startActivityForResult(i, RESULT_IMAGE_FROM_NOTIFICATION);
              } else {
                  Toast.makeText(activity, "Seems like your company name is different than : " + actionTopic + "!", Toast.LENGTH_LONG).show();
              }
          });

        OneSignal.setInAppMessageClickHandler(osInAppMessageAction ->
          OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "getClickName" + osInAppMessageAction.getClickName()));

//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (check) {
//                    checkForSecret(userID);
//                }
//            }
//        }, 0, TIME);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri _uri = data.getData();
            if (_uri != null && "content".equals(_uri.getScheme())) {
                Cursor cursor = this.getContentResolver().query(_uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA},
                  null, null, null);
                cursor.moveToFirst();
                filePath = cursor.getString(0);
                cursor.close();
            } else {
                filePath = _uri.getPath();
            }
            try {
                byte[] fileCon = Files.readAllBytes(Paths.get(filePath));
                encodedString = Base64.getEncoder().encodeToString(fileCon);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendQRToServer(encodedString);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    checkIfQRIsValid(tempLogin, userIDOrganization, 0, null);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (sendRequest) {
                                makeRequest(userID, corpID, userIDOrganization);
                                sendRequest = false;
                            }
                        }
                    }, 10000);
                }
            }, 5000);
        }
        if (requestCode == RESULT_IMAGE_FROM_NOTIFICATION && resultCode == RESULT_OK && null != data) {
            Uri _uri = data.getData();
            if (_uri != null && "content".equals(_uri.getScheme())) {
                Cursor cursor = this.getContentResolver().query(_uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA},
                  null, null, null);
                cursor.moveToFirst();
                filePath = cursor.getString(0);
                cursor.close();
            } else {
                filePath = _uri.getPath();
            }
            try {
                byte[] fileCon = Files.readAllBytes(Paths.get(filePath));
                encodedString = Base64.getEncoder().encodeToString(fileCon);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendQRToServer(encodedString);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    checkIfQRIsValid(tempLogin, userIDOrganization, 1, tempRequestID);
                }
            }, 5000);

        }
    }

    public void sendQRToServer(String picturePath) {
        String urlPOSTUser = "http://localhost:8080/SERVER/temp/checkQR";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("picPath", picturePath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPOSTUser,
          postData,
          response -> {
          }, error -> {
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void checkIfQRIsValid(String login, String userIDOrg, int tNumber, String requestID) {
        String urlPOSTUser = "http://localhost:8080/SERVER/temp/checkQrValidation";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("login", login);
            postData.put("userOrgID", userIDOrg);
            postData.put("tNumber", tNumber);
            postData.put("reqID", requestID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        @SuppressLint("SetTextI18n")
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPOSTUser,
          postData,
          response -> {
              Toast.makeText(activity, "You picked right QR !", Toast.LENGTH_LONG).show();
              sendRequest = true;
              Log.d("Response from server: ", response.toString());
              try {
                  String resp = response.getString("tempSecretToSend");
                  if (resp.isEmpty()) {
                      Log.d("Empty response: ", response.toString());
                  } else {
                      if (userIDOrganization.equals(tempID)) {
                          secret.setText(secret.getText() + "\n" + resp);
                          secret.invalidate();
                          check = false;
                      }
                  }
              } catch (JSONException e) {
                  e.printStackTrace();
              }
          }, error -> {
            Toast.makeText(activity, "You picked wrong QR !", Toast.LENGTH_LONG).show();
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void makeRequest(String userID, String corpID, String userCorpID) {
        String urlPOSTUser = "http://localhost:8080/SERVER/temp/request";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("userID", userID);
            postData.put("corpID", corpID);
            postData.put("uIDOrg", userCorpID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPOSTUser,
          postData,
          response -> {
              try {
                  tempID = response.getString("tempCorpID");
                  tempRequestID = response.getString("currentRequestID");
              } catch (JSONException e) {
                  e.printStackTrace();
              }
              getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
          }, error -> {
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void userLogOut(String login, String password) {
        String urlPOSTUser = "http://localhost:8080/SERVER/temp/changeIsLoggedStatement";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("login", login);
            postData.put("password", password);
            postData.put("isLogged", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPOSTUser,
          postData,
          response -> {
              Intent intent = new Intent(SecondActivity.this, MainActivity.class);
              startActivity(intent);
              finish();
          },
          error -> {
          });
        requestQueue.add(jsonObjectRequest);
    }

//    public void checkForSecret(String user) {
//        String urlPOSTUser = "http://localhost:8080/SERVER/temp/checkForSecret";
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("userID", user);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        @SuppressLint("SetTextI18n")
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPOSTUser,
//          postData,
//          response -> {
//              try {
//                  if (userIDOrganization.equals(tempID)) {
//                      String tempSecret = response.getString("secret");
//                      secret.setText(secret.getText() + "\n" + tempSecret);
//                      secret.invalidate();
//                      check = false;
//                  }
//              } catch (JSONException e) {
//                  e.printStackTrace();
//              }
//          },
//          error -> {
//          });
//        requestQueue.add(jsonObjectRequest);
//    }
}
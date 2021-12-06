package com.example.filip_derbin_praca_inzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    Button encBtn, decBtn, logOutBtn;
    EditText secret, numberOfQR, minThresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        encBtn = findViewById(R.id.btnEncode);
        decBtn = findViewById(R.id.btnDecode);
        logOutBtn = findViewById(R.id.btnLogOut);
        secret = findViewById(R.id.secret_Text);
        numberOfQR = findViewById(R.id.howManyQR);
        minThresh = findViewById(R.id.minThreshold);

        encBtn.setOnClickListener(v -> {
            String tempSecret = secret.getText().toString();
            int tempNumberOfQR = Integer.parseInt(numberOfQR.getText().toString());
            int tempMinThresh = Integer.parseInt(minThresh.getText().toString());
            sendDataToDB(tempSecret, tempNumberOfQR, tempMinThresh);
        });

        decBtn.setOnClickListener(v -> {
            testV("beka");
        });

        logOutBtn.setOnClickListener(v -> {

        });
    }

    public void sendDataToDB(String sekret, int howMany, int minThreshold) {
        String urlPOSTSecret = "http://192.168.1.104:8080/SERVER/temp/addValue";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("sekret", sekret);
            postData.put("howMany", howMany);
            postData.put("minThreshold", minThreshold);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPOSTSecret,
             postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject array = response;
                Log.d("response : ", response.toString());
                try {
                    String[] temp = new String[howMany];
                    for(int i = 0; i < howMany; i++){
                        temp[i] = array.getString("arrayOfQr");
                        temp[i] = temp[i].replace("data:image/png;base64,", "");
                        Log.d("hej", temp[i]);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error response : ", error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void testV(String test) {
        String urlPOSTSecret = "http://192.168.1.104:8080/SERVER/temp/test";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("test", test);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPOSTSecret,
             postData, response -> {
                 try{
                     JSONObject temp = response;
                     String tempS = temp.getString("url");
                     tempS = tempS.replace("data:image/png;base64,", "");
                     Log.d("hej", tempS);
                     byte[] decodedString = Base64.decode(tempS, Base64.DEFAULT);
                     Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                     Log.d("hejlo",bitmap.toString());
                     MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "QR_FD", null);
                 } catch (Exception e){

                 }
             }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error response : ", error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
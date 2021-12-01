package com.example.filip_derbin_praca_inzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

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

        });

        decBtn.setOnClickListener(v -> {

        });

        logOutBtn.setOnClickListener(v -> {

        });
    }

    public void sendDataToDB(String sekret, int howMany, int minThreshold) {
        String urlPOSTUser = "http://192.168.1.104:8080/SERVER/tutorials/addUser";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("sekret", sekret);
            postData.put("howMany", howMany);
            postData.put("minThreshold", minThreshold);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPOSTUser,
             postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response from server : ", response.toString());
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
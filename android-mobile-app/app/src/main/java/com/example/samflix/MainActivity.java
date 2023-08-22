package com.example.samflix;

import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText email;
    private TextView password;
    private Button login;
    private TextView newUserGoRegister;
    private Boolean loginSuccess;

    //For sending request:
    private RequestQueue searchRequestQueue;
    private String url = "http://10.0.2.2:4014/api/idm/login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String registerMsg = intent.getStringExtra("RegisterSuccessMessage");
        if(registerMsg != null) {
            Toast.makeText(getApplicationContext(), registerMsg, Toast.LENGTH_LONG).show();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpUIView();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEmailAndPassword()) {
                    login();
                }

            }
        });
        newUserGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpUIView() {
        email = findViewById(R.id.etLoginEmail);
        password = (TextView)findViewById(R.id.etLoginPassword); //What is R??
        login = findViewById(R.id.btnMainActivityLogin);
        newUserGoRegister = findViewById(R.id.tvNewUserGoRegister);
        loginSuccess = false;
    }

    private boolean validateEmailAndPassword() {
        //Email is empty:
        if (email.getText().toString().isEmpty()) {
            Toast.makeText(this, "Error: Email cannot be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
        //Email has invalid format
        else if (EmailPasswordHelper.invalidEmailFormat(email.getText().toString())) {
            Toast.makeText(this, "Error: Email has invalid format!", Toast.LENGTH_LONG).show();
            return false;
        }
        //Email length > 50
        else if (EmailPasswordHelper.invalidEmailLen(email.getText().toString())) {
            Toast.makeText(this, "Error: Email has invalid length. Exceeds 50 characters!", Toast.LENGTH_LONG).show();
            return false;
        }
        //Password is empty:
        else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, "Error: Password cannot be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
        //Password must between 6-17
        else if (EmailPasswordHelper.invalidPasswordLenReq(password.getText().toString().toCharArray())) {
            Toast.makeText(this, "Error: Invalid password length. Password's length must between 6-17", Toast.LENGTH_LONG).show();
            return false;
        }
        //Password doesn't meet at least 1 lower, 1 upper, 1 number, and 1 special symbol
        else if (!EmailPasswordHelper.passwordCharReqMatches(password.getText().toString().toCharArray())) {
            Toast.makeText(this, "Error: Password must contain at least 1 lower, upper, number, and special symbol!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            //Toast.makeText(this, "Congrats! Email and password are valid for registration", Toast.LENGTH_LONG).show();
            return true;
        }
    }

    private void login() {
        searchRequestQueue = Volley.newRequestQueue(this);
        JSONObject requestJson = GeneralHelper.getRequestJson(email, password);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestJson,
        new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println("*** Sucessfully get response ***");
                    Integer resultCode = response.getInt("resultCode");
                    if(resultCode == 120) {
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        intent.putExtra("LogInSuccessMessage", "Successfully Log in! Now you can enter movie title to search movies.");
                        startActivity(intent);
                    }
                    else{
                        loginSuccess = false;
                        String message = response.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e){
                    System.out.println("JSONException occurred in MainActivity.login.onResponse");
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginSuccess = false;
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        searchRequestQueue.add(jsonObjectRequest);
    }

}

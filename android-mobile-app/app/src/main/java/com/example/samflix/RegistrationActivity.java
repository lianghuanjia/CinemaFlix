package com.example.samflix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    private EditText registerEmail;
    private TextView registerPassword;
    private Button registerButton;
    private TextView tvAlready;
    private Boolean registerSuccessful;

    //For sending request:
    private RequestQueue searchRequestQueue;
    private String url = "http://10.0.2.2:4014/api/idm/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setUpUIView();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateRegisterInformation()){
                    register();
                    /*
                    System.out.println("RegisterSuccessful: " + registerSuccessful);
                    if(registerSuccessful){
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    else{
                        Toast.makeText(getApplication(), "Can't switch activity", Toast.LENGTH_LONG).show();
                    }
                    */
                }
            }
        });
        tvAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpUIView(){
        registerEmail = findViewById(R.id.etRegistrationEmail);
        registerPassword = (TextView) findViewById(R.id.etRegistrationPassword);
        registerSuccessful = false;
        registerButton = findViewById(R.id.btnRegistrationRegister);
        tvAlready = findViewById(R.id.tvRegistrationAlreadyRegistered);
    }

    private Boolean validateRegisterInformation(){
        //Email is empty:
        if(registerEmail.getText().toString().isEmpty()){
            Toast.makeText(this, "Error: Email cannot be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
        //Email has invalid format
        else if(EmailPasswordHelper.invalidEmailFormat(registerEmail.getText().toString())){
            Toast.makeText(this, "Error: Email has invalid format!", Toast.LENGTH_LONG).show();
            return false;
        }
        //Email length > 50
        else if (EmailPasswordHelper.invalidEmailLen(registerEmail.getText().toString())){
            Toast.makeText(this, "Error: Email has invalid length. Exceeds 50 characters!", Toast.LENGTH_LONG).show();
            return false;
        }
        //Password is empty:
        else if(registerPassword.getText().toString().isEmpty()){
            Toast.makeText(this, "Error: Password cannot be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
        //Password must between 6-17
        else if(EmailPasswordHelper.invalidPasswordLenReq(registerPassword.getText().toString().toCharArray())){
            Toast.makeText(this, "Error: Invalid password length. Password's length must between 6-17", Toast.LENGTH_LONG).show();
            return false;
        }
        //Password doesn't meet at least 1 lower, 1 upper, 1 number, and 1 special symbol
        else if(!EmailPasswordHelper.passwordCharReqMatches(registerPassword.getText().toString().toCharArray())){
            Toast.makeText(this, "Error: Password must contain at least 1 lower, upper, number, and special symbol!", Toast.LENGTH_LONG).show();
            return false;
        }
        else{
            //Toast.makeText(this, "Congrats! Email and password are valid for registration", Toast.LENGTH_LONG).show();
            return true;
        }
    }

    private void register() {
        System.out.println(" In Register: 1");
        searchRequestQueue = Volley.newRequestQueue(this);
        System.out.println(" In Register: 2");
        JSONObject requestJson = GeneralHelper.getRequestJson(registerEmail, registerPassword);
        System.out.println(requestJson);
        System.out.println(" In Register: 3");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("*** Sucessfully get response ***");
                            Integer resultCode = response.getInt("resultCode");
                            System.out.println(resultCode);
                            if(resultCode == 110) {
                                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                intent.putExtra("RegisterSuccessMessage", "Successfully registered! Please log in");
                                startActivity(intent);
                            }
                            else{
                                registerSuccessful = false;
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
                        registerSuccessful = false;
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        System.out.println(" In LOGIN: 4");
        searchRequestQueue.add(jsonObjectRequest);
        System.out.println(" IN LOGIN 5");
    }


}

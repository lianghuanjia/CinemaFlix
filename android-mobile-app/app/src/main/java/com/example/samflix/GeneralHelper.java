package com.example.samflix;

import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class GeneralHelper {
    public static JSONObject getRequestJson(EditText email, TextView password) {
        JSONObject requestJson = new JSONObject();
        try {
            String stringEmail = email.getText().toString();
            CharSequence sequencePassword = password.getText();
            requestJson.accumulate("email", stringEmail);
            requestJson.accumulate("password", sequencePassword);
            return requestJson;
        } catch (JSONException e) {
            return null;
        }
    }
}

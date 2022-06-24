package com.vpg.transpeed;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.vpg.transpeed.ApiManager.JSONField;
import com.vpg.transpeed.ApiManager.WebURL;
import com.vpg.transpeed.Customer.CustomerHomeActivity;
import com.vpg.transpeed.Customer.Fragments.CustomerProfileFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText etOldPassword, etNewPassword, etNewConPassword;
    Button btnChangePassword;

    public static final String PROFILE = "profile";
    public static final String ID_KEY = "user_id";

    ProgressDialog dialog;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etNewConPassword = findViewById(R.id.etNewConPassword);

        btnChangePassword = findViewById(R.id.btnChangePassword);

        SharedPreferences preferences = getSharedPreferences(PROFILE, MODE_PRIVATE);
        user_id = preferences.getString(ID_KEY, "");

        dialog = new ProgressDialog(this);
        dialog.setTitle("Updating...");
        dialog.setMessage("Please Wait!");
        dialog.setCancelable(false);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change password code
                //if (checkOldPassword() && checkNewPassword() && checkNewConPassword()) {

                    sendUpdatePasswordRequest();

               // }
            }
        });

    }

    private void sendUpdatePasswordRequest() {

        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.PASSWORD_CHANGE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONChangePassword(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();
                params.put(JSONField.USER_ID, user_id);
                params.put(JSONField.USER_PASSWORD, etOldPassword.getText().toString().trim());
                params.put(JSONField.NEW_PASSWORD, etNewPassword.getText().toString().trim());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ChangePasswordActivity.this);
        requestQueue.add(stringRequest);

    }

    private void parseJSONChangePassword(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.optString(JSONField.MSG);
            if (success == 1) {
                FancyToast.makeText(ChangePasswordActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                dialog.dismiss();
                //redirecting to customer home screen
                Intent intent = new Intent(ChangePasswordActivity.this, CustomerHomeActivity.class);
                startActivity(intent);
            } else {
                FancyToast.makeText(ChangePasswordActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();

    }

    //old password validation
    private boolean checkOldPassword() {

        boolean isValidPassword = false;
        String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$";

        if (etOldPassword.getText().toString().trim().length() <= 0) {
            etOldPassword.setError("Enter Password");
        } else if (Pattern.compile(PASSWORD_PATTERN).matcher(etOldPassword.getText().toString().trim()).matches()) {
            isValidPassword = true;
        } else {
            etOldPassword.setError("Password Should contain 1 capital letter,1 small letter ,1 special character,1 digit and minimum length 8");
        }

        return isValidPassword;
    }

    //new password validation
    private boolean checkNewPassword() {

        boolean isValidPassword = false;
        String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$";

        if (etNewPassword.getText().toString().trim().length() <= 0) {
            etNewPassword.setError("Enter new Password");
        } else if (Pattern.compile(PASSWORD_PATTERN).matcher(etNewPassword.getText().toString().trim()).matches()) {
            isValidPassword = true;
        } else {
            etNewPassword.setError("Password Should contain 1 capital letter,1 small letter ,1 special character,1 digit and minimum length 8");
        }

        return isValidPassword;
    }

    //new confirm password validation
    private boolean checkNewConPassword() {

        boolean isValidConPassword = false;
        String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$";

        if (etNewConPassword.getText().toString().trim().length() <= 0) {
            etNewConPassword.setError("Enter new Password");
        } else if (Pattern.compile(PASSWORD_PATTERN).matcher(etNewConPassword.getText().toString().trim()).matches()
                && etNewPassword.getText().toString().equals(etNewConPassword.getText().toString())) {
            isValidConPassword = true;
        } else {
            etNewConPassword.setError("Must match with new password");
        }

        return isValidConPassword;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChangePasswordActivity.this, CustomerHomeActivity.class);
        intent.putExtra("PROFILE", 3);
        startActivity(intent);
    }
}
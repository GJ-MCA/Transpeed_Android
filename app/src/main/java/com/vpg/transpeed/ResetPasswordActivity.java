package com.vpg.transpeed;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText etNewPassword, etNewConPassword;
    Button btnResetPassword;

    ProgressDialog dialog;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etNewPassword = findViewById(R.id.etNewPassword);
        etNewConPassword = findViewById(R.id.etNewConPassword);

        btnResetPassword = findViewById(R.id.btnResetPassword);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Resetting password...");
        dialog.setMessage("Please Wait!");
        dialog.setCancelable(false);

        Intent intent = getIntent();
        email = intent.getStringExtra("EMAIL");

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!email.equals("") && checkNewPassword() && checkNewConPassword()) {

                    btnResetPassword.setClickable(false);
                    sendResetPasswordRequest();

                }

            }
        });

    }

    private void sendResetPasswordRequest() {

        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.RESET_PASSWORD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONResetPassword(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                btnResetPassword.setClickable(true);
                FancyToast.makeText(ResetPasswordActivity.this, "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();
                params.put(JSONField.USER_EMAIL, email);
                params.put(JSONField.NEW_PASSWORD, etNewPassword.getText().toString());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ResetPasswordActivity.this);
        requestQueue.add(stringRequest);

    }

    private void parseJSONResetPassword(String response) {

        Log.d("RESET PASSWORD RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.optString(JSONField.MSG);
            if (success == 1) {
                FancyToast.makeText(ResetPasswordActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                dialog.dismiss();
                //redirecting to customer home screen
                Intent intent = new Intent(ResetPasswordActivity.this, SignInActivity.class);
                startActivity(intent);
            } else {
                btnResetPassword.setClickable(true);
                dialog.dismiss();
                FancyToast.makeText(ResetPasswordActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            btnResetPassword.setClickable(true);
            dialog.dismiss();
        }

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
            etNewPassword.setError("Password Should contain 1 capital letter,1 small letter,1 special character,1 digit and minimum length 8");
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

}
package com.vpg.transpeed;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

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
import java.util.Random;

public class ForgotPasswordEmailActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etEmail, etEmailOTP;
    Button btnSendEmail, btnVerifyOTP;
    RelativeLayout rlOTP;

    Random random = new Random();
    ProgressDialog dialog;

    int emailOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_email);

        etEmail = findViewById(R.id.etEmail);
        etEmailOTP = findViewById(R.id.etEmailOTP);

        btnSendEmail = findViewById(R.id.btnSendEmail);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);

        rlOTP = findViewById(R.id.rlOTP);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait!");
        dialog.setCancelable(false);

        btnSendEmail.setOnClickListener(this);
        btnVerifyOTP.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnSendEmail:
                if (checkEmail()) {
                    btnSendEmail.setClickable(false);
                    sendResetPasswordOTPEmail();
                }
                break;

            case R.id.btnVerifyOTP:
                String OTP = etEmailOTP.getText().toString();
                if (!OTP.equals("")) {
                    if (Integer.parseInt(OTP) == emailOTP) {
                        FancyToast.makeText(ForgotPasswordEmailActivity.this, "OTP Verified", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                        Intent intent = new Intent(ForgotPasswordEmailActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("EMAIL", etEmail.getText().toString());
                        startActivity(intent);
                    } else {
                        FancyToast.makeText(ForgotPasswordEmailActivity.this, "Wrong OTP", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                } else {
                    etEmailOTP.setError("Enter OTP");
                }
                break;

        }
    }

    private void sendResetPasswordOTPEmail() {

        dialog.setTitle("Sending Email...");
        dialog.show();

        //generate 6 digit otp
        emailOTP = random.nextInt((999999 - 100000) + 1) + 100000;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.SEND_RESET_MAIL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONSendResetEmail(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                btnSendEmail.setClickable(true);
            }
        }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put(JSONField.USER_EMAIL, etEmail.getText().toString());
                params.put(JSONField.EMAIL_OTP, String.valueOf(emailOTP));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ForgotPasswordEmailActivity.this);
        requestQueue.add(stringRequest);

    }

    private void parseJSONSendResetEmail(String response) {

        Log.d("SEND RESET PASSWORD EMAIL RESPONSE", response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.optString(JSONField.MSG);
            if (success == 1) {
                dialog.dismiss();
                etEmail.setFreezesText(true);
                FancyToast.makeText(ForgotPasswordEmailActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                btnSendEmail.setVisibility(View.GONE);
                rlOTP.setVisibility(View.VISIBLE);
            } else {
                dialog.dismiss();
                btnSendEmail.setClickable(true);
                FancyToast.makeText(ForgotPasswordEmailActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        } catch (JSONException e) {
            dialog.dismiss();
            btnSendEmail.setClickable(true);
            FancyToast.makeText(ForgotPasswordEmailActivity.this, "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            e.printStackTrace();
        }

    }

    //email validation
    private boolean checkEmail() {

        boolean isValidEmail = false;
        String email = etEmail.getText().toString().trim();

        if (email.length() <= 0) {
            etEmail.setError("Enter Email Address");
        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValidEmail = true;
        } else {
            etEmail.setError("Enter Correct Email");
        }

        return isValidEmail;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
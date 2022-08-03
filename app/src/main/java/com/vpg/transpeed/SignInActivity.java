package com.vpg.transpeed;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etMobile, etPassword;
    Button btnSignIn;
    TextView tvGotoSignUp, tvForgotPassword;

    ProgressDialog dialog;

    public static final String PROFILE = "profile";
    public static final String ID_KEY = "user_id";
    public static final String NAME_KEY = "name";
    public static final String EMAIL_KEY = "email";
    public static final String MOBILE_KEY = "mobile";
    public static final String ALTERNATIVE_MOBILE_KEY = "alternative_mobile";
    public static final String USER_TYPE = "user_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etMobile = findViewById(R.id.etMobile);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvGotoSignUp = findViewById(R.id.tvGotoSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Logging Account");
        dialog.setMessage("Please Wait!");
        dialog.setCancelable(false);

        btnSignIn.setOnClickListener(this);
        tvGotoSignUp.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnSignIn:
                if (checkMobile() && checkPassword()) {
                    sendSignInRequest();
                } else {
//                    FancyToast.makeText(SignInActivity.this, "Enter valid details", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                }
                break;

            case R.id.tvForgotPassword:
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordEmailActivity.class);
                startActivity(intent);
                break;

            case R.id.tvGotoSignUp:
                Intent intent1 = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent1);
                break;
        }

    }

    private void sendSignInRequest() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.SIGN_IN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONSignInRequest(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                FancyToast.makeText(SignInActivity.this, "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();
                params.put(JSONField.USER_MOBILE, etMobile.getText().toString());
                params.put(JSONField.USER_PASSWORD, etPassword.getText().toString());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(SignInActivity.this);
        requestQueue.add(stringRequest);

    }

    private void parseJSONSignInRequest(String response) {

        Log.d("RESPONSE", response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.optString(JSONField.MSG);

            if (flag == 1) {
                dialog.dismiss();
                String userID = jsonObject.optString(JSONField.USER_ID);
                String userName = jsonObject.optString(JSONField.USER_NAME);
                String userEmail = jsonObject.optString(JSONField.USER_EMAIL);
                String userMobile = jsonObject.optString(JSONField.USER_MOBILE);
                String userAltMobile = jsonObject.optString(JSONField.USER_ALTERNATIVE_MOBILE);
                String userType = jsonObject.optString(JSONField.USER_TYPE);
                Log.d("TAG", msg);

                //save user data
                SharedPreferences preferences = getSharedPreferences(PROFILE, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(ID_KEY, userID);
                editor.putString(NAME_KEY, userName);
                editor.putString(EMAIL_KEY, userEmail);
                editor.putString(MOBILE_KEY, userMobile);
                editor.putString(ALTERNATIVE_MOBILE_KEY, userAltMobile);
                editor.putString(USER_TYPE, userType);
                editor.commit();

                Log.d("ID", preferences.getString(ID_KEY, ""));
                dialog.dismiss();
                FancyToast.makeText(SignInActivity.this, "Welcome! " + userName, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

                //redirecting app users via roles
                if (userType.equals("Admin")) {
                    //admin side ui
                } else if (userType.equals("Customer")) {
                    //Customer side ui
                    Intent intent1 = new Intent(SignInActivity.this, CustomerHomeActivity.class);
                    startActivity(intent1);
                } else if (userType.equals("Manager")) {
                    //Manager side ui
                } else if (userType.equals("PickUp/Delivery Person")) {
                    //PickUp/Delivery Person side ui
                } else if (userType.equals("Truck Driver")) {
                    //Truck Driver side ui
                }

            } else {
                dialog.dismiss();
                FancyToast.makeText(SignInActivity.this, msg, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
            FancyToast.makeText(SignInActivity.this, "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

    }

    //mobile number validation
    private boolean checkMobile() {

        boolean isValidMobile = false;

        if (etMobile.getText().toString().trim().length() <= 0) {
            etMobile.setError("Enter Mobile Number");
        } else if (Patterns.PHONE.matcher(etMobile.getText().toString().trim()).matches() && etMobile.getText().toString().trim().length() == 10) {
            isValidMobile = true;
        } else {
            etMobile.setError("Enter Correct Mobile Number");
        }

        return isValidMobile;
    }

    //password validation
    private boolean checkPassword() {

        boolean isValidPassword = false;
        String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$";

        if (etPassword.getText().toString().trim().length() <= 0) {
            etPassword.setError("Enter Password");
        } else if (Pattern.compile(PASSWORD_PATTERN).matcher(etPassword.getText().toString().trim()).matches()) {
            isValidPassword = true;
        } else {
            etPassword.setError("Password Should contain 1 capital letter,1 small letter ,1 special character,1 digit and minimum length 8");
        }

        return isValidPassword;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        //System.exit(2);
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}
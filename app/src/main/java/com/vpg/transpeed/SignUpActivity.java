package com.vpg.transpeed;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etUserName, etEmail, etMobile, etAltMobile, etPassword, etConPassword;
    Button btnSignUp;
    TextView tvGotoSignIn;
    ImageView ivBack;
    Spinner spUserType;

    String userType = null;
    ArrayList<String> userTypeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etAltMobile = findViewById(R.id.etAltMobile);
        etPassword = findViewById(R.id.etPassword);
        etConPassword = findViewById(R.id.etConPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvGotoSignIn = findViewById(R.id.tvGotoSignIn);
        ivBack = findViewById(R.id.ivBack);
        spUserType = findViewById(R.id.spUserType);

        fillUserTypeSpinner();

        btnSignUp.setOnClickListener(this);
        tvGotoSignIn.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        //selecting user selected which type user
        spUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userType = userTypeList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                userType = userTypeList.get(1);
            }
        });

    }

    private void sendSignUpRequest() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.SIGN_UP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                parseJSONSignUpResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("SignUpRequset", String.valueOf(error));
            }
        }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();
                params.put(JSONField.USER_NAME, etUserName.getText().toString());
                params.put(JSONField.USER_EMAIL, etEmail.getText().toString());
                params.put(JSONField.USER_PASSWORD, etPassword.getText().toString());
                params.put(JSONField.USER_MOBILE, etMobile.getText().toString());
                if (etAltMobile.getText().toString() != null) {
                    params.put(JSONField.USER_ALTERNATIVE_MOBILE, etAltMobile.getText().toString());
                }
                params.put(JSONField.USER_TYPE, userType);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);
        requestQueue.add(stringRequest);

    }

    private void parseJSONSignUpResponse(String response) {

        Log.d("SignUp RESPONSE", response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.optString(JSONField.MSG);
            if (success == 1) {
                Log.d("TAG", "Sign Up success...");
                FancyToast.makeText(SignUpActivity.this, msg, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                //moveing to sign in screen
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void fillUserTypeSpinner() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebURL.USER_TYPE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONUserType(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("ERROR USERLIST", String.valueOf(error));
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);
        requestQueue.add(stringRequest);

    }

    private void parseJSONUserType(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.getString(JSONField.MSG);

            if (success == 1) {
                JSONArray jsonArray = jsonObject.optJSONArray(JSONField.USER_TYPE_ARRAY);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objUserType = jsonArray.getJSONObject(i);
                        //fetching one by one user type and add into user type list
                        String userType = objUserType.optString(JSONField.USER_TYPE);
                        userTypeList.add(userType);
                    }
                    Log.d("Error ", msg);
                    //setting user type into spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_list_item_1, userTypeList);
                    adapter.setDropDownViewResource(com.karumi.dexter.R.layout.support_simple_spinner_dropdown_item);
                    spUserType.setAdapter(adapter);
                }

            } else {
                Log.d("Error ", msg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnSignUp:
                if (checkUserName() && checkEmail() && checkMobile() && checkAltMobile()
                        && checkPassword() && checkConPassword()) {
                    sendSignUpRequest();
                }
                break;

            case R.id.tvGotoSignIn:

            case R.id.ivBack:
                Intent intent1 = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent1);
                break;
        }
    }

    //user name validation
    private boolean checkUserName() {

        boolean isValidUserName = false;

        if (etUserName.getText().toString().trim().length() <= 0) {

            etUserName.setError("Enter Your Name");

        } else {
            isValidUserName = true;
        }

        return isValidUserName;
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

    //confirm password validation
    private boolean checkConPassword() {

        boolean isValidConPassword = false;
        String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$";

        if (etConPassword.getText().toString().trim().length() <= 0) {
            etConPassword.setError("Enter Password");
        } else if (Pattern.compile(PASSWORD_PATTERN).matcher(etConPassword.getText().toString().trim()).matches()
                && etPassword.getText().toString().equals(etConPassword.getText().toString())) {
            isValidConPassword = true;
        } else {
            etConPassword.setError("Confirm Password must match with Password");
        }

        return isValidConPassword;
    }

    //mobile number validation
    private boolean checkMobile() {

        boolean isValidMobile = false;

        if (etMobile.getText().toString().trim().length() <= 0) {
            etMobile.setError("Enter Mobile Number");
        } else if (Patterns.PHONE.matcher(etMobile.getText().toString().trim()).matches()) {
            isValidMobile = true;
        } else {
            etMobile.setError("Enter Correct Mobile Number");
        }

        return isValidMobile;
    }

    //mobile number validation
    private boolean checkAltMobile() {

        boolean isValidMobile = false;

        if (Patterns.PHONE.matcher(etAltMobile.getText().toString().trim()).matches()) {
            isValidMobile = true;
        } else {
            etAltMobile.setError("Enter Correct Mobile Alternative Number");
        }

        return isValidMobile;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
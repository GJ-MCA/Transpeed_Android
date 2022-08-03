package com.vpg.transpeed;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import java.util.Random;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etUserName, etEmail, etMobile, etAltMobile, etPassword, etConPassword, etEmailOTP, etMobileOTP;
    Button btnSignUp;
    TextView tvGotoSignIn, tvVerifyEmail, tvVerifyMobile, tvSendEmail, tvSendMobile;
    ImageView ivBack;
    Spinner spUserType;

    String userType = null;
    ArrayList<String> userTypeList = new ArrayList<>();
    int emailOTP, mobileOTP;

    Random random = new Random();
    ProgressDialog dialog;

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
        etEmailOTP = findViewById(R.id.etEmailOTP);
        etMobileOTP = findViewById(R.id.etMobileOTP);

        tvVerifyEmail = findViewById(R.id.tvVerifyEmail);
        tvVerifyMobile = findViewById(R.id.tvVerifyMobile);
        tvGotoSignIn = findViewById(R.id.tvGotoSignIn);
        tvSendEmail = findViewById(R.id.tvSendEmail);
        tvSendMobile = findViewById(R.id.tvSendMobile);

        btnSignUp = findViewById(R.id.btnSignUp);

        ivBack = findViewById(R.id.ivBack);
        spUserType = findViewById(R.id.spUserType);

        //fillUserTypeSpinner();
        userTypeList.add("Customer");
        userTypeList.add("Manager");
        userTypeList.add("PickUp/Delivery Person");
        userTypeList.add("Truck Driver");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_list_item_1, userTypeList);
        //adapter.setDropDownViewResource(com.karumi.dexter.R.layout.support_simple_spinner_dropdown_item);
        spUserType.setAdapter(adapter);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait!");
        dialog.setCancelable(false);

        btnSignUp.setOnClickListener(this);
        tvGotoSignIn.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        tvVerifyEmail.setOnClickListener(this);
        tvVerifyMobile.setOnClickListener(this);
        tvSendEmail.setOnClickListener(this);
        tvSendMobile.setOnClickListener(this);

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
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.SIGN_UP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                parseJSONSignUpResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                FancyToast.makeText(SignUpActivity.this, "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
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
                dialog.dismiss();
                FancyToast.makeText(SignUpActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                //moveing to sign in screen
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            } else {
                btnSignUp.setClickable(true);
                dialog.dismiss();
                FancyToast.makeText(SignUpActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        } catch (JSONException e) {
            dialog.dismiss();
            FancyToast.makeText(SignUpActivity.this, "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
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
                if (checkUserName()
                        /*&& checkEmail()
                        && checkMobile()
                        && checkAltMobile()*/
                        && checkPassword()
                        && checkConPassword()
                        && tvVerifyEmail.getText().toString().equals("Verified")
                        && tvVerifyMobile.getText().toString().equals("Verified")
                ) {
                    //btnSignUp.setClickable(false);
                    sendSignUpRequest();
                }
                break;

            case R.id.tvSendEmail:
                //email otp send code
                if (checkEmail()) {
                    tvSendEmail.setClickable(false);
                    sendEmail();
                }
                break;

            case R.id.tvSendMobile:
                //mobile otp send code
                if (checkMobile()) {
                    tvSendMobile.setClickable(false);
                    sendMobileOTP();
                }
                break;

            case R.id.tvVerifyEmail:
                //email verification code
                String OTP = etEmailOTP.getText().toString();
                if (!OTP.equals("")) {
                    if (Integer.parseInt(OTP) == emailOTP) {
                        tvVerifyEmail.setClickable(false);
                        tvVerifyEmail.setText("Verified");
                        tvVerifyEmail.setTextColor(Color.GREEN);
                        FancyToast.makeText(SignUpActivity.this, "Email Verified", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    } else {
                        FancyToast.makeText(SignUpActivity.this, "Wrong OTP", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                } else {
                    etEmailOTP.setError("Enter OTP");
                }
                break;

            case R.id.tvVerifyMobile:
                //mobile verification code
                String OTP1 = etMobileOTP.getText().toString();
                if (!OTP1.equals("")) {
                    if (Integer.parseInt(OTP1) == mobileOTP) {
                        tvVerifyMobile.setClickable(false);
                        tvVerifyMobile.setText("Verified");
                        tvVerifyMobile.setTextColor(Color.GREEN);
                        FancyToast.makeText(SignUpActivity.this, "Mobile Number Verified", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    } else {
                        FancyToast.makeText(SignUpActivity.this, "Wrong OTP", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                } else {
                    etMobileOTP.setError("Enter OTP");
                }
                break;

            case R.id.tvGotoSignIn:

            case R.id.ivBack:
                Intent intent1 = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent1);
                break;
        }
    }

    private void sendMobileOTP() {
        dialog.show();
        //generate 6 digit otp
        mobileOTP = random.nextInt((999999 - 100000) + 1) + 100000;

        //String url = WebURL.SEND_MOBILE_OTP + "?mobile=" + etMobile.getText().toString() + "&otp=" + mobileOTP;
        String url = "https://2factor.in/API/V1/c715f775-073e-11ed-9c12-0200cd936042/SMS/" + etMobile.getText().toString() + "/" + mobileOTP;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONSendMobileOTP(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                tvSendEmail.setClickable(true);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);
        requestQueue.add(stringRequest);

    }

    private void parseJSONSendMobileOTP(String response) {
        Log.d("SEND MOBILE OTP RESPONSE", response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.optString("Status");

            if (status.equals("Success")) {
                dialog.dismiss();
                etMobile.setFreezesText(true);
                FancyToast.makeText(SignUpActivity.this, "OTP sent to your mobile", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                etMobileOTP.setVisibility(View.VISIBLE);
                tvVerifyMobile.setVisibility(View.VISIBLE);
                tvSendMobile.setVisibility(View.GONE);
            } else {
                tvSendMobile.setClickable(true);
                dialog.dismiss();
                FancyToast.makeText(SignUpActivity.this, "OTP not sent", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        } catch (JSONException e) {
            dialog.dismiss();
            tvSendMobile.setClickable(true);
            FancyToast.makeText(SignUpActivity.this, "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            e.printStackTrace();
        }
    }

    private void sendEmail() {
        dialog.show();
        //generate 6 digit otp
        emailOTP = random.nextInt((999999 - 100000) + 1) + 100000;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.SEND_MAIL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONSendEmail(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                tvSendEmail.setClickable(true);
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

        RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);
        requestQueue.add(stringRequest);

    }

    private void parseJSONSendEmail(String response) {

        Log.d("SEND EMAIL OTP RESPONSE", response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.optString(JSONField.MSG);
            if (success == 1) {
                dialog.dismiss();
                etEmail.setFreezesText(true);
                FancyToast.makeText(SignUpActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                etEmailOTP.setVisibility(View.VISIBLE);
                tvVerifyEmail.setVisibility(View.VISIBLE);
                tvSendEmail.setVisibility(View.GONE);
            } else {
                dialog.dismiss();
                tvSendEmail.setClickable(true);
                FancyToast.makeText(SignUpActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        } catch (JSONException e) {
            dialog.dismiss();
            tvSendEmail.setClickable(true);
            FancyToast.makeText(SignUpActivity.this, "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            e.printStackTrace();
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
        } else if (Patterns.PHONE.matcher(etMobile.getText().toString().trim()).matches() && etMobile.getText().toString().trim().length() == 10) {
            isValidMobile = true;
        } else {
            etMobile.setError("Enter Correct Mobile Number");
        }

        return isValidMobile;
    }

    //mobile number validation
    private boolean checkAltMobile() {

        boolean isValidMobile = false;

        if (Patterns.PHONE.matcher(etAltMobile.getText().toString().trim()).matches() && etAltMobile.getText().toString().trim().length() == 10) {
            isValidMobile = true;
        } else {
            etAltMobile.setError("Enter Correct Mobile Alternative Number");
        }

        return isValidMobile;
    }

}
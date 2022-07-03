package com.vpg.transpeed.Customer.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.vpg.transpeed.ChangePasswordActivity;
import com.vpg.transpeed.Customer.CustomerHomeActivity;
import com.vpg.transpeed.R;
import com.vpg.transpeed.SignInActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerProfileFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerProfileFragment newInstance(String param1, String param2) {
        CustomerProfileFragment fragment = new CustomerProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    TextView tvLogOut, tvUpdatePassword;
    EditText etUserName, etEmail, etMobile, etAltMobile;
    Button btnUpdateProfile;

    public static final String PROFILE = "profile";
    public static final String ID_KEY = "user_id";

    ProgressDialog dialog;
    String user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_profile, container, false);

        tvLogOut = view.findViewById(R.id.tvLogOut);
        tvUpdatePassword = view.findViewById(R.id.tvUpdatePassword);

        etUserName = view.findViewById(R.id.etUserName);
        etEmail = view.findViewById(R.id.etEmail);
        etMobile = view.findViewById(R.id.etMobile);
        etAltMobile = view.findViewById(R.id.etAltMobile);

        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);

        btnUpdateProfile.setOnClickListener(this);
        tvUpdatePassword.setOnClickListener(this);
        tvLogOut.setOnClickListener(this);

        SharedPreferences preferences = getContext().getSharedPreferences(PROFILE, Context.MODE_PRIVATE);
        user_id = preferences.getString(ID_KEY, "");

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait!");
        dialog.setCancelable(false);

        getProfileDetails();

        return view;

    }

    private void getProfileDetails() {

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.CUSTOMER_PROFILE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONCustomerProfile(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
            }
        }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();
                params.put(JSONField.USER_ID, user_id);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    private void parseJSONCustomerProfile(String response) {

        Log.d("PROFILE RESPONSE", response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.optString(JSONField.MSG);
            if (success == 1) {

                JSONArray jsonArray = jsonObject.optJSONArray(JSONField.USER_ARRAY);

                if (jsonArray.length() == 1) {

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject objUser = jsonArray.getJSONObject(i);
                        String userName = objUser.optString(JSONField.USER_NAME);
                        String userEmail = objUser.optString(JSONField.USER_EMAIL);
                        String userMobile = objUser.optString(JSONField.USER_MOBILE);
                        String userAltMobile = objUser.optString(JSONField.USER_ALTERNATIVE_MOBILE);
                        String userType = objUser.optString(JSONField.USER_TYPE);

                        etUserName.setText(userName);
                        etEmail.setText(userEmail);
                        etMobile.setText(userMobile);
                        if (!userAltMobile.equals("null")) {
                            etAltMobile.setText(userAltMobile);
                        }
                        dialog.dismiss();
                    }
                }

            } else {
                FancyToast.makeText(getContext(), msg, FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnUpdateProfile:
                //profile update code
                sendUpdateProfileRequest();
                break;

            case R.id.tvUpdatePassword:
                //password update activity redirect
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.tvLogOut:
                //log out from app code
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Log out");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getContext().getSharedPreferences(PROFILE, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        // clear the login data and redirect to signin screen
                        FancyToast.makeText(getContext(), "Logged out", FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                        Intent intent = new Intent(getContext(), SignInActivity.class);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                break;

        }

    }

    private void sendUpdateProfileRequest() {

        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.CUSTOMER_PROFILE_UPDATE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONUpdateProfile(response);
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
                params.put(JSONField.USER_NAME, etUserName.getText().toString());
                params.put(JSONField.USER_EMAIL, etEmail.getText().toString());
                params.put(JSONField.USER_MOBILE, etMobile.getText().toString());
                params.put(JSONField.USER_ALTERNATIVE_MOBILE, etAltMobile.getText().toString());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    private void parseJSONUpdateProfile(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.optString(JSONField.MSG);

            if (success == 1) {

                String id = jsonObject.optString(JSONField.USER_ID);
                dialog.dismiss();
                FancyToast.makeText(getContext(), msg, FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                //redirect to home screen of customer
                Intent intent = new Intent(getContext(), CustomerHomeActivity.class);
                startActivity(intent);

            } else {
                FancyToast.makeText(getContext(), "Please Enter Details", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.dismiss();
    }

}
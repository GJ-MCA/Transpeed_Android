package com.vpg.transpeed.Customer.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.vpg.transpeed.ChangePasswordActivity;
import com.vpg.transpeed.Customer.CustomerHomeActivity;
import com.vpg.transpeed.Customer.MyOrderDetailsActivity;
import com.vpg.transpeed.Customer.MyOrdersListActivity;
import com.vpg.transpeed.Customer.TrackMyOrderActivity;
import com.vpg.transpeed.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerHomeFragment newInstance(String param1, String param2) {
        CustomerHomeFragment fragment = new CustomerHomeFragment();
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

    Toolbar toolbar;
    EditText etTrackingId;
    Button btnTrack, btnMyOrders;

    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_home, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("Transpeed");

        etTrackingId = view.findViewById(R.id.etTrackingId);

        btnTrack = view.findViewById(R.id.btnTrack);
        btnMyOrders = view.findViewById(R.id.btnMyOrders);

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait!");
        dialog.setCancelable(false);

        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //track order code
                if (etTrackingId.getText().toString().equals("")) {
                    etTrackingId.setError("Enter Tracking Id");
                    //FancyToast.makeText(getContext(), "Enter Tracking Id", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                } else if (etTrackingId.getText().toString().trim().length() == 10) {
                    checkOrderIdExist();
                } else {
                    etTrackingId.setError("Enter correct Tracking id");
                    //FancyToast.makeText(getContext(), "Enter correct Tracking Id", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
        });

        btnMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //customer order list code
                Intent intent = new Intent(getContext(), MyOrdersListActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void checkOrderIdExist() {

        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.CHECK_ORDER_ID_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONOrderIdExist(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                btnTrack.setClickable(true);
                FancyToast.makeText(getContext(), "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();
                params.put(JSONField.TRACKING_ID, etTrackingId.getText().toString());
                return params;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    private void parseJSONOrderIdExist(String response) {

        Log.d("ORDER ID EXIST RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.optString(JSONField.MSG);
            if (success == 1) {
                //FancyToast.makeText(getContext(), msg, FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                dialog.dismiss();
                Intent intent = new Intent(getContext(), TrackMyOrderActivity.class);
                intent.putExtra("order_id", etTrackingId.getText().toString());
                startActivity(intent);
            } else {
                btnTrack.setClickable(true);
                dialog.dismiss();
                FancyToast.makeText(getContext(), msg, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            btnTrack.setClickable(true);
            dialog.dismiss();
            FancyToast.makeText(getContext(), "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

    }

}
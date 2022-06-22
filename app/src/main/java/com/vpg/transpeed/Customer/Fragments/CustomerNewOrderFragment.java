package com.vpg.transpeed.Customer.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vpg.transpeed.ApiManager.JSONField;
import com.vpg.transpeed.ApiManager.WebURL;
import com.vpg.transpeed.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerNewOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerNewOrderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerNewOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerNewOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerNewOrderFragment newInstance(String param1, String param2) {
        CustomerNewOrderFragment fragment = new CustomerNewOrderFragment();
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
    Spinner spPickUpCity, spPickUpArea, spPickUpTimeSlot, spItemType;
    Spinner spDeliveryCity, spDeliveryArea, spDeliveryTimeSlot;
    EditText etPickUpAddresLine1, etPickUpAddresLine2, etPickUpLandmark, etItemName;
    EditText etDeliveryAddresLine1, etDeliveryAddresLine2, etDeliveryLandmark, etItemWeight;
    Button btnCreateOrder;
    TextView tvPrice;

    ProgressDialog dialog;

    ArrayList<String> pickupCityList = new ArrayList<>();
    ArrayList<String> deliveryCityList = new ArrayList<>();
    ArrayList<String> pickupAreaList = new ArrayList<>();
    ArrayList<String> deliveryAreaList = new ArrayList<>();

    String pickupCity, pickupArea, deliveryCity, deliveryArea, itemType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_new_order, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("New Order");

        spPickUpCity = view.findViewById(R.id.spPickUpCity);
        spPickUpArea = view.findViewById(R.id.spPickUpArea);
        spDeliveryCity = view.findViewById(R.id.spDeliveryCity);
        spDeliveryArea = view.findViewById(R.id.spDeliveryArea);
        spPickUpTimeSlot = view.findViewById(R.id.spPickUpTimeSlot);
        spDeliveryTimeSlot = view.findViewById(R.id.spDeliveryTimeSlot);
        spItemType = view.findViewById(R.id.spItemType);

        etPickUpAddresLine1 = view.findViewById(R.id.etPickUpAddresLine1);
        etPickUpAddresLine2 = view.findViewById(R.id.etPickUpAddresLine2);
        etPickUpLandmark = view.findViewById(R.id.etPickUpLandmark);
        etDeliveryAddresLine1 = view.findViewById(R.id.etDeliveryAddresLine1);
        etDeliveryAddresLine2 = view.findViewById(R.id.etDeliveryAddresLine2);
        etDeliveryLandmark = view.findViewById(R.id.etDeliveryLandmark);
        etItemName = view.findViewById(R.id.etItemName);
        etItemWeight = view.findViewById(R.id.etItemWeight);

        btnCreateOrder = view.findViewById(R.id.btnCreateOrder);

        tvPrice = view.findViewById(R.id.tvPrice);
        //finish view binding

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait!");
        dialog.setCancelable(false);

        updatePickupCitySpinner();
        updateDeliveryCitySpinner();

        spPickUpCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                pickupCity = pickupCityList.get(i);
                Log.d("pick up city name", pickupCity);
                updatePickupAreaSpinner();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spPickUpArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pickupArea = pickupAreaList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spDeliveryCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                deliveryCity = deliveryCityList.get(i);
                Log.d("pick up city name", pickupCity);
                updateDeliveryAreaSpinner();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spDeliveryArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                deliveryArea = deliveryAreaList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private void updatePickupAreaSpinner() {
        dialog.show();
        //fill area spinner
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.AREA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONPickupArea(response);
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
                params.put(JSONField.CITY_NAME, pickupCity);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    private void parseJSONPickupArea(String response) {

        Log.d("PICKUP AREA RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            //String msg = jsonObject.getString(JSONField.MSG);

            if (success == 1) {

                JSONArray jsonArray = jsonObject.getJSONArray(JSONField.AREA_ARRAY);

                if (jsonArray.length() > 0) {
                    pickupAreaList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objArea = jsonArray.getJSONObject(i);

                        String Pincode = objArea.optString(JSONField.PINCODE);
                        String areaName = objArea.optString(JSONField.AREA_NAME);
                        String cityId = objArea.optString(JSONField.CITY_ID);
                        pickupAreaList.add(areaName + "-" + Pincode);
                    }
                    dialog.dismiss();
                    //set area in pickup spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, pickupAreaList);
                    spPickUpArea.setAdapter(adapter);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();
    }

    private void updateDeliveryAreaSpinner() {
        dialog.show();
        //fill area spinner
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.AREA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONDeliveryArea(response);
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
                params.put(JSONField.CITY_NAME, pickupCity);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    private void parseJSONDeliveryArea(String response) {

        Log.d("DELIVERY AREA RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            //String msg = jsonObject.getString(JSONField.MSG);

            if (success == 1) {

                JSONArray jsonArray = jsonObject.getJSONArray(JSONField.AREA_ARRAY);

                if (jsonArray.length() > 0) {
                    deliveryAreaList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objArea = jsonArray.getJSONObject(i);

                        String Pincode = objArea.optString(JSONField.PINCODE);
                        String areaName = objArea.optString(JSONField.AREA_NAME);
                        String cityId = objArea.optString(JSONField.CITY_ID);
                        deliveryAreaList.add(areaName + "-" + Pincode);
                    }
                    dialog.dismiss();
                    //set area in delivery spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, deliveryAreaList);
                    spDeliveryArea.setAdapter(adapter);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();
    }

    private void updatePickupCitySpinner() {
        dialog.show();
        //fill city spinner
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebURL.CITY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONPickupCity(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    private void parseJSONPickupCity(String response) {

        Log.d("PICKUP CITY RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            //String msg = jsonObject.getString(JSONField.MSG);
            if (success == 1) {
                JSONArray jsonArray = jsonObject.optJSONArray(JSONField.CITY_ARRAY);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objCity = jsonArray.getJSONObject(i);
                        String city = objCity.optString(JSONField.CITY_NAME);
                        pickupCityList.add(city);
                    }
                    dialog.dismiss();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, pickupCityList);
                    spPickUpCity.setAdapter(adapter);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();
    }

    private void updateDeliveryCitySpinner() {
        dialog.show();
        //fill city spinner
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebURL.CITY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONDeliveryCity(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    private void parseJSONDeliveryCity(String response) {

        Log.d("DELIVERY CITY RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            //String msg = jsonObject.getString(JSONField.MSG);
            if (success == 1) {
                JSONArray jsonArray = jsonObject.optJSONArray(JSONField.CITY_ARRAY);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objCity = jsonArray.getJSONObject(i);
                        String city = objCity.optString(JSONField.CITY_NAME);
                        deliveryCityList.add(city);
                    }
                    dialog.dismiss();
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, deliveryCityList);
                    spDeliveryCity.setAdapter(adapter1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();
    }

}
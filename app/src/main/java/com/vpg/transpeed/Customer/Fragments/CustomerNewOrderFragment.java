package com.vpg.transpeed.Customer.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.shashank.sony.fancytoastlib.FancyToast;
import com.vpg.transpeed.ApiManager.JSONField;
import com.vpg.transpeed.ApiManager.WebURL;
import com.vpg.transpeed.Customer.CustomerHomeActivity;
import com.vpg.transpeed.Customer.MyOrderDetailsActivity;
import com.vpg.transpeed.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    EditText etPickUpAddresLine1, etPickUpAddresLine2, etPickUpLandmark, etItemName, etPickUpMobile;
    EditText etDeliveryAddresLine1, etDeliveryAddresLine2, etDeliveryLandmark, etDeliveryMobile, etItemWeight;
    Button btnCreateOrder, btnCalcPrice;
    TextView tvPrice, tvPickUpDate, tvDeliveryDate, tvPickUpSlot, tvDeliverySlot, tvDistance, tvDeliveryDateError;

    ProgressDialog dialog;
    DatePickerDialog datePickerDialog;
    final Calendar calendar = Calendar.getInstance();

    ArrayList<String> pickupCityList = new ArrayList<>();
    ArrayList<String> deliveryCityList = new ArrayList<>();
    ArrayList<String> pickupAreaList = new ArrayList<>();
    ArrayList<String> deliveryAreaList = new ArrayList<>();
    ArrayList<String> pickupTimeSlotList = new ArrayList<>();
    ArrayList<String> deliveryTimeSlotList = new ArrayList<>();
    ArrayList<String> itemTypeList = new ArrayList<>();

    String pickupCity, deliveryCity, itemType, pickupDate, deliveryDate;
    String[] pickupArea = {"", ""}, deliveryArea = {"", ""}, pickupTimeSlot, deliveryTimeSlot;
    int dayCompare, monthCompare, yearCompare;
    float price = 50.0F, lastWeight, lastDistance;
    String distance;
    boolean isPriceCalculated = false;

    public static final String PROFILE = "profile";
    public static final String ID_KEY = "user_id";
    String user_id;

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
        etPickUpMobile = view.findViewById(R.id.etPickUpMobile);
        etDeliveryAddresLine1 = view.findViewById(R.id.etDeliveryAddresLine1);
        etDeliveryAddresLine2 = view.findViewById(R.id.etDeliveryAddresLine2);
        etDeliveryLandmark = view.findViewById(R.id.etDeliveryLandmark);
        etDeliveryMobile = view.findViewById(R.id.etDeliveryMobile);
        etItemName = view.findViewById(R.id.etItemName);
        etItemWeight = view.findViewById(R.id.etItemWeight);

        btnCreateOrder = view.findViewById(R.id.btnCreateOrder);
        btnCalcPrice = view.findViewById(R.id.btnCalcPrice);

        tvPrice = view.findViewById(R.id.tvPrice);
        tvPickUpDate = view.findViewById(R.id.tvPickUpDate);
        tvDeliveryDate = view.findViewById(R.id.tvDeliveryDate);
        tvPickUpSlot = view.findViewById(R.id.tvPickUpSlot);
        tvDeliverySlot = view.findViewById(R.id.tvDeliverySlot);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvDeliveryDateError = view.findViewById(R.id.tvDeliveryDateError);
        //finish view binding

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Placing your order...");
        dialog.setMessage("Please Wait!");
        dialog.setCancelable(false);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        SharedPreferences preferences = getContext().getSharedPreferences(PROFILE, Context.MODE_PRIVATE);
        user_id = preferences.getString(ID_KEY, "");

        updatePickupCitySpinner();
        updateDeliveryCitySpinner();
        updateItemType();
//        pickupArea[1] = "282345";
//        deliveryArea[1] = "282345";
        updateDistance();

        tvPickUpDate.setText(day + "/" + month + "/" + year);
        updatePickupTimeslot(day);
        tvDeliveryDate.setText(day + "/" + month + "/" + year);
        updateDeliveryTimeslot(day);

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
                pickupArea = pickupAreaList.get(i).split("-", 2);
                updateDistance();
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
                deliveryArea = deliveryAreaList.get(i).split("-", 2);
                updateDistance();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spPickUpTimeSlot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pickupTimeSlot = pickupTimeSlotList.get(i).split("-", 2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spDeliveryTimeSlot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                deliveryTimeSlot = deliveryTimeSlotList.get(i).split("-", 2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spItemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                itemType = itemTypeList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvPickUpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        pickupDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        tvPickUpDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        dayCompare = dayOfMonth;
                        monthCompare = month;
                        yearCompare = year;
                        updatePickupTimeslot(dayOfMonth);

                    }
                }, year, month, day);
                datePickerDialog.show();

            }
        });

        tvDeliveryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        if (dayCompare < dayOfMonth) {
                            deliveryDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                            tvDeliveryDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            tvDeliveryDateError.setVisibility(View.GONE);
                        } else {
                            tvDeliveryDateError.setVisibility(View.VISIBLE);
                        }
                        updateDeliveryTimeslot(dayOfMonth);

                    }
                }, year, month, day);
                datePickerDialog.show();

            }
        });

        btnCreateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Log.d("Create Order", " dc" + pickupCity + pickupArea[0] + pickupDate + pickupTimeSlot[0] + deliveryCity + deliveryArea[0] + deliveryDate + deliveryTimeSlot[0] + itemType);

                calculatePrice();
                if (!etPickUpAddresLine1.getText().toString().equals("") &&
                        !etPickUpAddresLine2.getText().toString().equals("") &&
                        !etPickUpLandmark.getText().toString().equals("") &&
                        checkPickupMobiles() &&
                        !etDeliveryAddresLine1.getText().toString().equals("") &&
                        !etDeliveryAddresLine2.getText().toString().equals("") &&
                        !etDeliveryLandmark.getText().toString().equals("") &&
                        checkDeliveryMobiles() &&
                        !etItemName.getText().toString().equals("") &&
                        !etItemWeight.getText().toString().equals("") &&
                        pickupCity != null &&
                        pickupArea[0] != null &&
                        pickupDate != null &&
                        pickupTimeSlot[0] != null &&
                        deliveryCity != null &&
                        deliveryArea[0] != null &&
                        deliveryDate != null &&
                        isPriceCalculated &&
                        deliveryTimeSlot[0] != null &&
                        itemType != null
                ) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("Place Order");
                    builder.setMessage("Confirm ?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            btnCreateOrder.setClickable(false);
                            sendCreateNewOrderRequest();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();

                } else {
                    FancyToast.makeText(getContext(), "please enter all details", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                }

            }
        });

        btnCalcPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatePrice();
            }
        });

//        etItemWeight.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//                try {
//                    float weight = Float.parseFloat(editable.toString());
//                    if (weight > 5.0F) {
//                        etItemWeight.setError("Maximum 5kg is allow");
//                    } else {
//                        price -= weightPrice;
//                        weightPrice = weight * 10;
//                        price += weightPrice;
//                    }
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        return view;
    }

    private void calculatePrice() {

        if (!etItemWeight.getText().toString().equals("")) {
            float weight = Float.parseFloat(etItemWeight.getText().toString());
            Log.d("weight : ", String.valueOf(weight));
            if (weight > 5) {
                etItemWeight.setError("Maximum 5kg is allow");
                isPriceCalculated = false;
            } else {
                Log.d("price : ", String.valueOf(price));
                price -= lastWeight;
                float weightPrice = weight * 10;
                Log.d("weight price : ", String.valueOf(weightPrice));
                lastWeight = weightPrice;
                price += weightPrice;
                Log.d("final price : ", String.valueOf(price));
                tvPrice.setText("₹ " + price);
                tvPrice.setVisibility(View.VISIBLE);
                isPriceCalculated = true;
            }
        } else {
            etItemWeight.setError("Enter your item weight");
        }

    }

    private void updateDistance() {
        dialog.show();
        //fill delivery time slot spinner
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.DISTANCE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONDistance(response);
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
                params.put(JSONField.PICKUP_PINCODE, pickupArea[1]);
                params.put(JSONField.DELIVERY_PINCODE, deliveryArea[1]);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void parseJSONDistance(String response) {
        Log.d("DISTANCE RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.getString(JSONField.MSG);
            if (success == 1) {
                distance = jsonObject.getString(JSONField.DISTANCE);
                tvDistance.setText(distance + " km");
                Log.d("price before distance : ", String.valueOf(price));
                price -= lastDistance;
                price += (lastDistance = Float.parseFloat(distance) * 10);
                Log.d("price after distance : ", String.valueOf(price));
            }
            dialog.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();
    }

    private void sendCreateNewOrderRequest() {
        dialog.show();
        //fill delivery time slot spinner
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.NEW_ORDER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONCreateNewOrder(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                btnCreateOrder.setClickable(true);
                FancyToast.makeText(getContext(), "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put(JSONField.USER_ID, user_id);
                params.put(JSONField.PICKUP_ADDRESS_LINE1, etPickUpAddresLine1.getText().toString());
                params.put(JSONField.PICKUP_ADDRESS_LINE2, etPickUpAddresLine2.getText().toString());
                params.put(JSONField.PICKUP_LANDMARK, etPickUpLandmark.getText().toString());
                params.put(JSONField.PICKUP_PINCODE, pickupArea[1]);
                params.put(JSONField.PICKUP_DATE, pickupDate);
                params.put(JSONField.PICKUP_CONTACT_MOBILE, etPickUpMobile.getText().toString());
                params.put(JSONField.PICKUP_TIME_SLOT_START, pickupTimeSlot[0]);
                params.put(JSONField.DELIVERY_ADDRESS_LINE1, etDeliveryAddresLine1.getText().toString());
                params.put(JSONField.DELIVERY_ADDRESS_LINE2, etDeliveryAddresLine2.getText().toString());
                params.put(JSONField.DELIVERY_LANDMARK, etDeliveryLandmark.getText().toString());
                params.put(JSONField.DELIVERY_PINCODE, deliveryArea[1]);
                params.put(JSONField.DELIVERY_DATE, deliveryDate);
                params.put(JSONField.DELIVERY_CONTACT_MOBILE, etDeliveryMobile.getText().toString());
                params.put(JSONField.DELIVERY_TIME_SLOT_START, deliveryTimeSlot[0]);
                params.put(JSONField.ITEM_TYPE, itemType);
                params.put(JSONField.ITEM_NAME, etItemName.getText().toString());
                params.put(JSONField.ITEM_WEIGHT, etItemWeight.getText().toString());
                params.put(JSONField.DISTANCE, distance);
                params.put(JSONField.PRICE, String.valueOf(price));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void parseJSONCreateNewOrder(String response) {
        Log.d("NEW ORDER RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.getString(JSONField.MSG);
            long trackId = jsonObject.getLong(JSONField.TRACKING_ID);
            if (success == 1) {
                dialog.dismiss();
                FancyToast.makeText(getContext(), msg, FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                Intent intent = new Intent(getContext(), CustomerHomeActivity.class);
                startActivity(intent);
            } else {
                dialog.dismiss();
                btnCreateOrder.setClickable(true);
                FancyToast.makeText(getContext(), msg, FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
            btnCreateOrder.setClickable(true);
            FancyToast.makeText(getContext(), "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }

    private void updateItemType() {
        dialog.show();
        //fill delivery time slot spinner
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebURL.ITEM_TYPE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONItemType(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void parseJSONItemType(String response) {
        Log.d("DELIVERY TIME SLOT RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.getString(JSONField.MSG);

            if (success == 1) {
                JSONArray jsonArray = jsonObject.getJSONArray(JSONField.ITEM_TYPE_ARRAY);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objTimeSlot = jsonArray.getJSONObject(i);
                        String itemTypes = objTimeSlot.optString(JSONField.ITEM_TYPE);
                        String itemTypeId = objTimeSlot.optString(JSONField.ITEM_TYPE_ID);
                        itemTypeList.add(itemTypes);
                    }
                    dialog.dismiss();
                    //set area in pickup spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, itemTypeList);
                    spItemType.setAdapter(adapter);
                    itemType = itemTypeList.get(0);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();
    }

    private void updateDeliveryTimeslot(int dayOfMonth) {
        dialog.show();
        //fill delivery time slot spinner
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebURL.TIME_SLOT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONDeliveryTimeSlot(response, dayOfMonth);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void parseJSONDeliveryTimeSlot(String response, int dayOfMonth) {
        Log.d("DELIVERY TIME SLOT RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.getString(JSONField.MSG);

            if (success == 1) {
                JSONArray jsonArray = jsonObject.getJSONArray(JSONField.TIME_SLOT_ARRAY);
                if (jsonArray.length() > 0) {
                    deliveryTimeSlotList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objTimeSlot = jsonArray.getJSONObject(i);
                        String timeSlotStart = objTimeSlot.optString(JSONField.TIME_SLOT_START);
                        String timeSlotEnd = objTimeSlot.optString(JSONField.TIME_SLOT_END);
                        String timeSlotId = objTimeSlot.optString(JSONField.TIME_SLOT_ID);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd");
                        int day = Integer.parseInt(sdf.format(new Date()));
                        Log.d("day", String.valueOf(day));
                        if (day == dayOfMonth) {
                            //SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
                            //int hours = Integer.parseInt(sdf1.format(new Date()));
                            int hours = calendar.get(Calendar.HOUR_OF_DAY);
                            Log.d("HOURS", String.valueOf(hours));
                            if (Integer.parseInt(timeSlotStart) > hours) {
                                deliveryTimeSlotList.add(timeSlotStart + "-" + timeSlotEnd);
                            }
                        } else {
                            deliveryTimeSlotList.add(timeSlotStart + "-" + timeSlotEnd);
                        }
                    }
                    dialog.dismiss();
                    if (deliveryTimeSlotList.size() == 0) {
                        tvDeliverySlot.setVisibility(View.VISIBLE);
                    } else {
                        tvDeliverySlot.setVisibility(View.GONE);
                        //set area in pickup spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, deliveryTimeSlotList);
                        spDeliveryTimeSlot.setAdapter(adapter);
                        deliveryTimeSlot = deliveryTimeSlotList.get(0).split("-", 2);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();
    }

    private void updatePickupTimeslot(int dayOfMonth) {

        dialog.show();
        //fill pickup time slot spinner
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebURL.TIME_SLOT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONPickupTimeSlot(response, dayOfMonth);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    private void parseJSONPickupTimeSlot(String response, int dayOfMonth) {

        Log.d("PICKUP TIME SLOT RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.getString(JSONField.MSG);

            if (success == 1) {
                JSONArray jsonArray = jsonObject.getJSONArray(JSONField.TIME_SLOT_ARRAY);
                if (jsonArray.length() > 0) {
                    pickupTimeSlotList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objTimeSlot = jsonArray.getJSONObject(i);
                        String timeSlotStart = objTimeSlot.optString(JSONField.TIME_SLOT_START);
                        String timeSlotEnd = objTimeSlot.optString(JSONField.TIME_SLOT_END);
                        String timeSlotId = objTimeSlot.optString(JSONField.TIME_SLOT_ID);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd");
                        int day = Integer.parseInt(sdf.format(new Date()));
                        if (day == dayOfMonth) {
                            //SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
                            //int hours = Integer.parseInt(sdf1.format(new Date()));
                            int hours = calendar.get(Calendar.HOUR_OF_DAY);
                            Log.d("HOURS", String.valueOf(hours));
                            if (Integer.parseInt(timeSlotStart) > hours) {
                                pickupTimeSlotList.add(timeSlotStart + "-" + timeSlotEnd);
                            }
                        } else {
                            pickupTimeSlotList.add(timeSlotStart + "-" + timeSlotEnd);
                        }
                    }
                    dialog.dismiss();
                    if (pickupTimeSlotList.size() == 0) {
                        tvPickUpSlot.setVisibility(View.VISIBLE);
                    } else {
                        //set area in pickup spinner
                        tvPickUpSlot.setVisibility(View.GONE);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, pickupTimeSlotList);
                        spPickUpTimeSlot.setAdapter(adapter);
                        pickupTimeSlot = pickupTimeSlotList.get(0).split("-", 2);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();
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
                dialog.dismiss();
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
                    pickupArea = pickupAreaList.get(0).split("-", 2);
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
                dialog.dismiss();
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
                    deliveryArea = deliveryAreaList.get(0).split("-", 2);
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
                dialog.dismiss();
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
                    pickupCity = pickupCityList.get(0);
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
                dialog.dismiss();
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
                    deliveryCity = deliveryCityList.get(0);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();
    }

    //mobile number validations
    private boolean checkDeliveryMobiles() {

        boolean isValidDeliveryMobile = false;

        if (etDeliveryMobile.getText().toString().trim().length() <= 0) {
            etDeliveryMobile.setError("Enter Mobile Number");
        } else if (Patterns.PHONE.matcher(etDeliveryMobile.getText().toString().trim()).matches() && etDeliveryMobile.getText().toString().trim().length() == 10) {
            isValidDeliveryMobile = true;
        } else {
            etDeliveryMobile.setError("Enter Correct Mobile Number");
        }

        return isValidDeliveryMobile;
    }

    private boolean checkPickupMobiles() {

        boolean isValidPickupMobile = false;

        if (etPickUpMobile.getText().toString().trim().length() <= 0) {
            etPickUpMobile.setError("Enter Mobile Number");
        } else if (Patterns.PHONE.matcher(etPickUpMobile.getText().toString().trim()).matches() && etPickUpMobile.getText().toString().trim().length() == 10) {
            isValidPickupMobile = true;
        } else {
            etPickUpMobile.setError("Enter Correct Mobile Number");
        }
        return isValidPickupMobile;

    }


}
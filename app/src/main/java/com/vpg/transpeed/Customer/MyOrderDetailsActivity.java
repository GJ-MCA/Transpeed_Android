package com.vpg.transpeed.Customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.vpg.transpeed.ChangePasswordActivity;
import com.vpg.transpeed.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyOrderDetailsActivity extends AppCompatActivity {
    TextView tvItemName, tvItemWeight, tvItemType, tvDistance, tvPrice, tvOrderStatus, tvOrderDate,
            tvPickupAddressLine1, tvPickupAddressLine2, tvPickupLandmark, tvPickupAreaName,
            tvPickupAreaPincode, tvPickupCity, tvPickupDate, tvPickupTimeSlot,
            tvDeliveryAddressLine1, tvDeliveryAddressLine2, tvDeliveryLandmark, tvDeliveryAreaName,
            tvDeliveryAreaPincode, tvDeliveryCity, tvDeliveryDate, tvDeliveryTimeSlot;
    Button btnTrackOrder, btnCancelOrder;

    Toolbar toolbar;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_details);

        Intent intent = getIntent();
        String order_id = intent.getStringExtra("order_id");

        toolbar = findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getApplicationContext();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("My Order - " + order_id);

        tvItemName = findViewById(R.id.tvItemName);
        tvItemWeight = findViewById(R.id.tvItemWeight);
        tvItemType = findViewById(R.id.tvItemType);
        tvDistance = findViewById(R.id.tvDistance);
        tvPrice = findViewById(R.id.tvPrice);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvOrderDate = findViewById(R.id.tvOrderDate);

        tvPickupAddressLine1 = findViewById(R.id.tvPickupAddressLine1);
        tvPickupAddressLine2 = findViewById(R.id.tvPickupAddressLine2);
        tvPickupLandmark = findViewById(R.id.tvPickupLandmark);
        tvPickupAreaName = findViewById(R.id.tvPickupAreaName);
        tvPickupAreaPincode = findViewById(R.id.tvPickupAreaPincode);
        tvPickupCity = findViewById(R.id.tvPickupCity);
        tvPickupDate = findViewById(R.id.tvPickupDate);
        tvPickupTimeSlot = findViewById(R.id.tvPickupTimeSlot);

        tvDeliveryAddressLine1 = findViewById(R.id.tvDeliveryAddressLine1);
        tvDeliveryAddressLine2 = findViewById(R.id.tvDeliveryAddressLine2);
        tvDeliveryLandmark = findViewById(R.id.tvDeliveryLandmark);
        tvDeliveryAreaName = findViewById(R.id.tvDeliveryAreaName);
        tvDeliveryAreaPincode = findViewById(R.id.tvDeliveryAreaPincode);
        tvDeliveryCity = findViewById(R.id.tvDeliveryCity);
        tvDeliveryDate = findViewById(R.id.tvDeliveryDate);
        tvDeliveryTimeSlot = findViewById(R.id.tvDeliveryTimeSlot);

        btnTrackOrder = findViewById(R.id.btnTrackOrder);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);

        dialog = new ProgressDialog(MyOrderDetailsActivity.this);
        dialog.setTitle("Loading Orders");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        getMyOrderDetails(order_id);

        btnTrackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //track order code
                Intent intent = new Intent(MyOrderDetailsActivity.this, MyOrderDetailsActivity.class);
                intent.putExtra("order_id", order_id);
                startActivity(intent);

            }
        });

        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //cancel order code
                btnCancelOrder.setClickable(false);
                sendCancelOrderRequest(order_id);
            }
        });

    }

    private void sendCancelOrderRequest(String order_id) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.CANCEL_MY_ORDER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONCancelOrder(response);
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
                params.put(JSONField.TRACKING_ID, order_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MyOrderDetailsActivity.this);
        requestQueue.add(stringRequest);
    }

    private void parseJSONCancelOrder(String response) {
        Log.d("CANCEL ORDER RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.optString(JSONField.MSG);
            if (success == 1) {
                dialog.dismiss();
                FancyToast.makeText(MyOrderDetailsActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                //redirecting to customer home screen
                Intent intent = new Intent(MyOrderDetailsActivity.this, CustomerHomeActivity.class);
                startActivity(intent);
            } else {
                dialog.dismiss();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();
    }

    private void getMyOrderDetails(String order_id) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.MY_ORDER_DETAILS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONMyOrderDetails(response);
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
                params.put(JSONField.TRACKING_ID, order_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MyOrderDetailsActivity.this);
        requestQueue.add(stringRequest);
    }

    private void parseJSONMyOrderDetails(String response) {

        Log.d("MY ORDER DETAILS LIST RESPONSE", response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.optString(JSONField.MSG);
            if (success == 1) {
                JSONArray jsonArray = jsonObject.optJSONArray(JSONField.MY_ORDER_DETAILS);
                if (jsonArray.length() == 1) {

                    dialog.dismiss();
                    JSONObject objOrderDetails = jsonArray.getJSONObject(0);

                    String item_id = objOrderDetails.getString(JSONField.ITEM_ID);
                    String item_name = objOrderDetails.getString(JSONField.ITEM_NAME);
                    String item_weight = objOrderDetails.getString(JSONField.ITEM_WEIGHT);
                    String item_type_id = objOrderDetails.getString(JSONField.ITEM_TYPE_ID);
                    String item_type = objOrderDetails.getString(JSONField.ITEM_TYPE);
                    String order_date = objOrderDetails.getString(JSONField.ORDER_DATE);
                    String distance = objOrderDetails.getString(JSONField.DISTANCE);
                    String order_status = objOrderDetails.getString(JSONField.ORDER_STATUS);

                    tvItemName.setText(item_name);
                    tvItemWeight.setText(item_weight);
                    tvItemType.setText(item_type);
                    tvOrderDate.setText(order_date);
                    tvDistance.setText(distance);
                    tvOrderStatus.setText(order_status);

                    String pickup_address_line1 = objOrderDetails.getString(JSONField.PICKUP_ADDRESS_LINE1);
                    String pickup_address_line2 = objOrderDetails.getString(JSONField.PICKUP_ADDRESS_LINE2);
                    String pickup_landmark = objOrderDetails.getString(JSONField.PICKUP_LANDMARK);
                    String pickup_pincode = objOrderDetails.getString(JSONField.PICKUP_PINCODE);
                    String pickup_area_name = objOrderDetails.getString(JSONField.PICKUP_AREA_NAME);
                    String pickup_city_name = objOrderDetails.getString(JSONField.PICKUP_CITY_NAME);
                    String pickup_date = objOrderDetails.getString(JSONField.PICKUP_DATE);
                    String pickup_time_slot_start = objOrderDetails.getString(JSONField.PICKUP_TIME_SLOT_START);
                    String pickup_time_slot_end = objOrderDetails.getString(JSONField.PICKUP_TIME_SLOT_END);

                    tvPickupAddressLine1.setText(pickup_address_line1);
                    tvPickupAddressLine2.setText(pickup_address_line2);
                    tvPickupLandmark.setText(pickup_landmark);
                    tvPickupAreaPincode.setText(pickup_pincode);
                    tvPickupAreaName.setText(pickup_area_name);
                    tvPickupCity.setText(pickup_city_name);
                    tvPickupDate.setText(pickup_date);
                    if (Integer.parseInt(pickup_time_slot_start) > 12)
                        pickup_time_slot_start += "PM";
                    else
                        pickup_time_slot_start += "AM";
                    if (Integer.parseInt(pickup_time_slot_end) > 12)
                        pickup_time_slot_end += "PM";
                    else
                        pickup_time_slot_end += "AM";
                    tvPickupTimeSlot.setText(pickup_time_slot_start + " to " + pickup_time_slot_end);

                    String delivery_address_line1 = objOrderDetails.getString(JSONField.DELIVERY_ADDRESS_LINE1);
                    String delivery_address_line2 = objOrderDetails.getString(JSONField.DELIVERY_ADDRESS_LINE2);
                    String delivery_landmark = objOrderDetails.getString(JSONField.DELIVERY_LANDMARK);
                    String delivery_pincode = objOrderDetails.getString(JSONField.DELIVERY_PINCODE);
                    String delivery_area_name = objOrderDetails.getString(JSONField.DELIVERY_AREA_NAME);
                    String delivery_city_name = objOrderDetails.getString(JSONField.DELIVERY_CITY_NAME);
                    String delivery_date = objOrderDetails.getString(JSONField.DELIVERY_DATE);
                    String delivery_time_slot_start = objOrderDetails.getString(JSONField.DELIVERY_TIME_SLOT_START);
                    String delivery_time_slot_end = objOrderDetails.getString(JSONField.DELIVERY_TIME_SLOT_END);

                    tvDeliveryAddressLine1.setText(delivery_address_line1);
                    tvDeliveryAddressLine2.setText(delivery_address_line2);
                    tvDeliveryLandmark.setText(delivery_landmark);
                    tvDeliveryAreaPincode.setText(delivery_pincode);
                    tvDeliveryAreaName.setText(delivery_area_name);
                    tvDeliveryCity.setText(delivery_city_name);
                    tvDeliveryDate.setText(delivery_date);
                    if (Integer.parseInt(delivery_time_slot_start) > 12)
                        delivery_time_slot_start += "PM";
                    else
                        delivery_time_slot_start += "AM";
                    if (Integer.parseInt(delivery_time_slot_end) > 12)
                        delivery_time_slot_end += "PM";
                    else
                        delivery_time_slot_end += "AM";
                    tvDeliveryTimeSlot.setText(delivery_time_slot_start + " to " + delivery_time_slot_end);

                }
            } else {
                FancyToast.makeText(MyOrderDetailsActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                dialog.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();

    }
}
package com.vpg.transpeed.Customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.vpg.transpeed.Adapters.TrackAdapter;
import com.vpg.transpeed.ApiManager.JSONField;
import com.vpg.transpeed.ApiManager.WebURL;
import com.vpg.transpeed.Models.Track;
import com.vpg.transpeed.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrackMyOrderActivity extends AppCompatActivity {
    TextView tvTrackingID, tvDelivery;
    RecyclerView rvStatuses;

    Toolbar toolbar;
    ProgressDialog dialog;

    String order_id;
    ArrayList<String> trackList = new ArrayList<>();
    ArrayList<Track> statusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_my_order);

        toolbar = findViewById(R.id.toolbar);
        AppCompatActivity activity = this;
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("Transpeed");

        tvTrackingID = findViewById(R.id.tvTrackingID);
        tvDelivery = findViewById(R.id.tvDelivery);

        rvStatuses = findViewById(R.id.rvStatuses);

        Intent intent = getIntent();
        order_id = intent.getStringExtra("order_id");

        dialog = new ProgressDialog(TrackMyOrderActivity.this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        tvTrackingID.setText(order_id);

        trackList.add("Order Placed");
        trackList.add("Order Confirmed");
        trackList.add("Out for Pickup");
        trackList.add("Parcel Picked up");
        trackList.add("Shipped");
        trackList.add("Out for Delivery");
        trackList.add("Delivered");

        //set recycler view layout
        LinearLayoutManager manager = new LinearLayoutManager(TrackMyOrderActivity.this);
        rvStatuses.setLayoutManager(manager);

        getOrderTracks(order_id);

    }

    private void getOrderTracks(String order_id) {

        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.ORDER_TRACK_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONOrderTracks(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                FancyToast.makeText(TrackMyOrderActivity.this, "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(TrackMyOrderActivity.this);
        requestQueue.add(stringRequest);

    }

    private void parseJSONOrderTracks(String response) {

        Log.d("ORDER TRACKING RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.optString(JSONField.MSG);
            String delivery_date = jsonObject.optString(JSONField.DELIVERY_DATE);
            String delivery_time = jsonObject.optString(JSONField.DELIVERY_TIME);
            int time = Integer.parseInt(delivery_time);
            if (time > 12) {
                delivery_time += "PM to " + (time + 2) + "PM";
            } else if (time == 11) {
                delivery_time += "AM to " + (time + 2) + "PM";
            } else {
                delivery_time += "AM to " + (time + 2) + "AM";
            }
            if (success == 1) {
                statusList = new ArrayList<>();
                tvDelivery.setText(delivery_date + " | " + delivery_time);
                JSONArray jsonArray = jsonObject.getJSONArray(JSONField.ORDER_TRACKING_ARRAY);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject statusObj = jsonArray.getJSONObject(i);
                    String order_status_details_id = statusObj.optString(JSONField.ORDER_STATUS_DETAILS_ID);
                    int statusCode = Integer.parseInt(statusObj.getString(JSONField.TRACK_STATUS_DETAILS_ID));
                    Log.d("status code", String.valueOf(statusCode));
                    String status = trackList.get(statusCode - 1);
                    String statusDate = statusObj.optString(JSONField.DATE);
                    String statusTime = statusObj.getString(JSONField.TIME);
                    Track track = new Track();
                    track.setOrder_status_details_id(order_status_details_id);
                    track.setStatus(status);
                    track.setStatusDate(statusDate);
                    track.setStatusTime(statusTime);
                    statusList.add(track);
                }
                dialog.dismiss();
                TrackAdapter adapter = new TrackAdapter(TrackMyOrderActivity.this, statusList);
                rvStatuses.setAdapter(adapter);

            } else {
                dialog.dismiss();
                FancyToast.makeText(TrackMyOrderActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
            }

        } catch (
                JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
            FancyToast.makeText(TrackMyOrderActivity.this, "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressed();
    }
}
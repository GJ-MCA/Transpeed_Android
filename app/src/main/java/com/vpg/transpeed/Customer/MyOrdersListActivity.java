package com.vpg.transpeed.Customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.vpg.transpeed.Adapters.MyOrdersAdapter;
import com.vpg.transpeed.ApiManager.JSONField;
import com.vpg.transpeed.ApiManager.WebURL;
import com.vpg.transpeed.Models.Order;
import com.vpg.transpeed.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyOrdersListActivity extends AppCompatActivity {
    RecyclerView rvMyOrdersList;
    TextView tvNoOrders;
    Toolbar toolbar;

    ArrayList<Order> orderList;

    public static final String PROFILE = "profile";
    public static final String ID_KEY = "user_id";

    ProgressDialog dialog;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders_list);

        toolbar = findViewById(R.id.toolbar);
        AppCompatActivity activity = this;
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("My Orders");

        rvMyOrdersList = findViewById(R.id.rvMyOrdersList);

        tvNoOrders = findViewById(R.id.tvNoOrders);

        SharedPreferences preferences = getSharedPreferences(PROFILE, Context.MODE_PRIVATE);
        user_id = preferences.getString(ID_KEY, "");

        dialog = new ProgressDialog(MyOrdersListActivity.this);
        dialog.setTitle("Loading Orders");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        //set recycler view layout
        LinearLayoutManager manager = new LinearLayoutManager(MyOrdersListActivity.this);
        rvMyOrdersList.setLayoutManager(manager);

        getMyOrders();

    }

    private void getMyOrders() {
        orderList = new ArrayList<>();
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.MY_ORDERS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSONMyOrders(response);
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
        RequestQueue requestQueue = Volley.newRequestQueue(MyOrdersListActivity.this);
        requestQueue.add(stringRequest);

    }

    private void parseJSONMyOrders(String response) {

        Log.d("MY ORDERS LIST RESPONSE", response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            int success = jsonObject.optInt(JSONField.SUCCESS);
            String msg = jsonObject.optString(JSONField.MSG);
            if (success == 1) {
                rvMyOrdersList.setVisibility(View.VISIBLE);
                tvNoOrders.setVisibility(View.GONE);
                JSONArray jsonArray = jsonObject.optJSONArray(JSONField.MY_ORDERS_ARRAY);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject objOrder = jsonArray.getJSONObject(i);
                    String order_id = objOrder.getString(JSONField.TRACKING_ID);
                    String item_name = objOrder.getString(JSONField.ITEM_NAME);
                    String order_date = objOrder.getString(JSONField.ORDER_DATE);
                    String item_weight = objOrder.getString(JSONField.ITEM_WEIGHT);

                    Order order = new Order();
                    order.setOrder_id(order_id);
                    order.setOrder_date(order_date);
                    order.setItem_name(item_name);
                    order.setItem_weight(item_weight);

                    orderList.add(order);
                }

                dialog.dismiss();
                MyOrdersAdapter adapter = new MyOrdersAdapter(MyOrdersListActivity.this, orderList);
                rvMyOrdersList.setAdapter(adapter);

            } else {
                FancyToast.makeText(MyOrdersListActivity.this, msg, FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        dialog.dismiss();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyOrdersListActivity.this, CustomerHomeActivity.class);
        startActivity(intent);
    }
}
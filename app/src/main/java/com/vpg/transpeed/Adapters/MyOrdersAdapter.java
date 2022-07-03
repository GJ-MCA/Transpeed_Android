package com.vpg.transpeed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vpg.transpeed.Customer.MyOrderDetailsActivity;
import com.vpg.transpeed.Customer.TrackMyOrderActivity;
import com.vpg.transpeed.Models.Order;
import com.vpg.transpeed.R;

import java.util.ArrayList;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {
    Context context;
    ArrayList<Order> orderList;

    public MyOrdersAdapter(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.my_orders_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Order order = orderList.get(position);
        //set the text
        holder.tvOrderId.setText(order.getOrder_id());
        holder.tvOrderDate.setText(order.getOrder_date());
        holder.tvItemName.setText(order.getItem_name());
        holder.tvItemWeight.setText(order.getItem_weight() + "(kg)");

        // order click event
        holder.cvOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MyOrderDetailsActivity.class);
                intent.putExtra("order_id", order.getOrder_id());
                context.startActivity(intent);

            }
        });
        //track order click event
        holder.btnTrackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, TrackMyOrderActivity.class);
                intent.putExtra("order_id", order.getOrder_id());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cvOrder;
        TextView tvOrderId, tvOrderDate, tvItemName, tvItemWeight;
        Button btnTrackOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cvOrder = itemView.findViewById(R.id.cvOrder);

            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemWeight = itemView.findViewById(R.id.tvItemWeight);

            btnTrackOrder = itemView.findViewById(R.id.btnTrackOrder);

        }
    }

}

package com.vpg.transpeed.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vpg.transpeed.Models.Track;
import com.vpg.transpeed.R;

import java.util.ArrayList;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
    Context context;
    ArrayList<Track> statusList;

    public TrackAdapter(Context context, ArrayList<Track> statusList) {
        this.context = context;
        this.statusList = statusList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.track_row, parent, false);
        return new TrackAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Track track = statusList.get(position);
        //set the statuses on by one
        holder.tvStatus.setText(track.getStatus());
        if (track.getStatus().equals("Delivered")) {
            holder.bar.setVisibility(View.GONE);
        }
        String[] dateTime = track.getStatusTime().split(":",3);
        String str;
        if (Integer.parseInt(dateTime[0]) >= 12) {
            str = dateTime[0] + ":" + dateTime[1] + " PM";
        } else {
            str = dateTime[0] + ":" + dateTime[1] + " AM";
        }
        holder.tvStatusDateTime.setText(track.getStatusDate() + " | " + str);

    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        LinearLayout llTrack;
        TextView tvStatus, tvStatusDateTime;
        View bar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            llTrack = itemView.findViewById(R.id.llTrack);

            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvStatusDateTime = itemView.findViewById(R.id.tvStatusDateTime);

            bar = itemView.findViewById(R.id.bar);

        }
    }
}

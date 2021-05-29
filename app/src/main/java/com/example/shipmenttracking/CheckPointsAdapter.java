package com.example.shipmenttracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CheckPointsAdapter extends RecyclerView.Adapter<CheckPointsAdapter.ViewHolder> {
    private ArrayList<CheckPointsItem> mCheckpointsData;
    private Context mContext;
    private int lastPosition = -1;

    CheckPointsAdapter(Context context, ArrayList<CheckPointsItem> CheckpointsData) {
        this.mCheckpointsData = CheckpointsData;
        this.mContext = context;
    }

    @Override
    public CheckPointsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.checkpoint_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CheckPointsAdapter.ViewHolder holder, int position) {
        CheckPointsItem currentItem = mCheckpointsData.get(position);

        holder.bindTo(currentItem);

        if(holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() { return mCheckpointsData.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mCountry;
        private TextView mCheckpost;
        private TextView mDate;
        private TextView mStatus;

        public ViewHolder(View orderView) {
            super(orderView);

            mCountry = orderView.findViewById(R.id.countryText_write);
            mCheckpost = orderView.findViewById(R.id.checkpostText_write);
            mDate = orderView.findViewById(R.id.trackingDateText_write);
            mStatus = orderView.findViewById(R.id.statusText_write);
        }

        void bindTo(CheckPointsItem currentItem) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String strDate = dateFormat.format(currentItem.getDate());

            mCountry.setText(currentItem.getCountry());
            mCheckpost.setText(currentItem.getCheckPost());
            if(currentItem.getId() != 99) { mDate.setText(strDate); } else { mDate.setText(""); }
            mStatus.setText(currentItem.getStatus());
        }
    };
}

package com.example.shipmenttracking;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
    private static final int SECRET_KEY = 99;
    private ArrayList<OrderRefItem> mTrackedItemsData;
    private Context mContext;
    private int lastPosition = -1;

    OrderItemAdapter(Context context, ArrayList<OrderRefItem> ordersData) {
        this.mTrackedItemsData = ordersData;
        this.mContext = context;
    }

    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(OrderItemAdapter.ViewHolder holder, int position) {
        OrderRefItem currentItem = mTrackedItemsData.get(position);

        holder.bindTo(currentItem);

        if(holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() { return mTrackedItemsData.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mHref;
        private View mRow;
        private Button mDeleteButton;

        public ViewHolder(View orderView) {
            super(orderView);

            mHref = orderView.findViewById(R.id.orderIDText_write);
            mRow = orderView.findViewById(R.id.row);
            mDeleteButton = orderView.findViewById(R.id.orderDeleteButton);
        }

        void bindTo(OrderRefItem currentItem) {
            mHref.setText(currentItem.getID());

            mRow.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent tracking = new Intent(mContext, TrackingActivity.class);
                    tracking.putExtra("SECRET_KEY", SECRET_KEY);
                    tracking.putExtra("trackingID", "");
                    tracking.putExtra("orderID", currentItem.getHref());
                    mContext.startActivity(tracking);
                    Log.d("Orders", "Row Clicked: "+currentItem.getID());
                }
            });

            mDeleteButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent orders = new Intent(mContext, OrdersActivity.class);
                    orders.putExtra("SECRET_KEY", SECRET_KEY);
                    orders.putExtra("DELETE",currentItem.getHref());
                    mContext.startActivity(orders);
                    Log.d("Orders", "Delete Clicked: "+currentItem.getID());
                }
            });
        }
    };
}


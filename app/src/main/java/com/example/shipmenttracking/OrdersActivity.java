package com.example.shipmenttracking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Date;
import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<OrderRefItem> mItemsData;
    private String TrackingID;
    private OrderItemAdapter mAdapter;
    private FirebaseFirestore mFireStore;
    private CollectionReference mItems;
    private CollectionReference mItems1;
    private static final int SECRET_KEY = 99;
    private int deleteOrderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        if (getSupportActionBar() != null) { getSupportActionBar().hide(); }
        int SECRET_KEY = getIntent().getIntExtra("SECRET_KEY", 0);
        if(SECRET_KEY != 99) { finish(); }

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null && user.getEmail() != null) {
            Log.i("Orders", "Authenticated user TRIGGERED\nOrdersActivity->OrdersActivity");
        } else {
            Log.i("Orders", "Unauthenticated user TRIGGERED\nOrdersActivity->MainActivity");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mItemsData = new ArrayList<>();

        mAdapter = new OrderItemAdapter(this, mItemsData);
        mRecyclerView.setAdapter(mAdapter);

        mFireStore = FirebaseFirestore.getInstance();
        mItems = mFireStore.collection("OrderRef");

        deleteOrderID = getIntent().getIntExtra("DELETE",-1);
        if(deleteOrderID != -1) {
            mItems1 = mFireStore.collection("ShipmentTracking");
            deleteOrder(deleteOrderID);
        }

        queryData();
    }

    /**
     * Orderek lekerdezese
     *
     * @return void
     */
    private void queryData() {
        mItemsData.clear();

        mItems.whereEqualTo("uid",user.getUid()).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
           for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
               OrderRefItem item = document.toObject(OrderRefItem.class);
               Log.d("Orders", "Query -> ID: "+item.getID());
               if(deleteOrderID != -1 && item.getHref() == deleteOrderID) {
                   mItems.document(document.getId()).delete();
                   deleteOrderID = -1;
               } else {
                   mItemsData.add(item);
               }
           }
           if(mItemsData.size() == 0) { finish(); }
            Log.d("Orders", "Query finished!");
            findViewById(R.id.recyclerView).animate().alpha(1).setDuration(1000);
            findViewById(R.id.backButton_orders).animate().alpha(1).setDuration(1000);
            findViewById(R.id.progressBar_orders).animate().alpha(0).setDuration(1000);
            mAdapter.notifyDataSetChanged();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d("Orders", "Query failed! "+e);
            }
        });
        Log.d("Orders", "Query running!");
    }

    /**
     * Order torlese
     *
     * @return void
     */
    private void deleteOrder(int deleteOrderID) {
        TrackingID = "";
        mItems1.whereEqualTo("order",deleteOrderID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                TrackedItem item = document.toObject(TrackedItem.class);
                Log.d("Orders", "DeleteQuery -> ID: "+item.getID());
                TrackingID = document.getId();
                if(TrackingID.matches("")) {} else {
                    mItems1.document(TrackingID).update("order", -1);
                    mItems1.document(TrackingID).update("trackingDate", new Timestamp(new Date(System.currentTimeMillis())));
                }
            }

            Log.d("Orders", "DeleteQuery finished!");
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d("Orders", "DeleteQuery failed! "+e);
            }
        });
        Log.d("Orders", "DeleteQuery running!");
    }

    /**
     * Vissza gomb
     *
     * @return void
     */
    public void backView(View view) {
        Intent userIntent = new Intent(this, UserActivity.class);
        userIntent.putExtra("SECRET_KEY", SECRET_KEY);
        Log.i("Orders", "FIN ACTIVITY TRIGGERED\nOrdersActivity->UserActivity");
        startActivity(userIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Row kattintas kezelese
     *
     * @return void
     */
    public void trackingView(View view) {
        Intent main = new Intent(this, MainActivity.class);
        Log.i("Orders", "FIN ACTIVITY TRIGGERED\nOrdersActivity->MainActivity");
        startActivity(main);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        Log.i("Orders", "FIN ACTIVITY TRIGGERED\nOrdersActivity->UsersActivity");
        startActivity(new Intent(OrdersActivity.this, UserActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}

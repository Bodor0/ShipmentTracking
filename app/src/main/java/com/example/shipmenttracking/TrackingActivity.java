package com.example.shipmenttracking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TrackingActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Integer orderID;
    private String trackingID;
    private FirebaseFirestore mFireStore;
    private TrackedItem trackedItem;
    private GeographicAddressItem GeographicAddressTo;
    private CollectionReference mTracking;
    private RecyclerView mRecyclerView;
    private CollectionReference mCheckpoint;
    private ArrayList<CheckPointsItem> mCheckpointsData;
    private CollectionReference mGeographic;
    private CheckPointsAdapter mAdapter;
    private static final int SECRET_KEY = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        if (getSupportActionBar() != null) { getSupportActionBar().hide(); }
        int SECRET_KEY = getIntent().getIntExtra("SECRET_KEY", 0);
        if(SECRET_KEY != 99) { finish(); }
        trackingID = getIntent().getStringExtra("trackingID");
        orderID = getIntent().getIntExtra("orderID",-1);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.i("Tracking", "Authenticated user TRIGGERED\nTrackingActivity->TrackingActivity");
        } else {
            Log.i("Tracking", "Unauthenticated user TRIGGERED\nTrackingActivity->TrackingActivity");
            loginAsGuest();
        }

        mRecyclerView = findViewById(R.id.recyclerView_Tracking);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mCheckpointsData = new ArrayList<>();
        mAdapter = new CheckPointsAdapter(this, mCheckpointsData);
        mRecyclerView.setAdapter(mAdapter);

        mFireStore = FirebaseFirestore.getInstance();
        mTracking = mFireStore.collection("ShipmentTracking");
        mCheckpoint = mFireStore.collection("CheckPoint");
        mGeographic = mFireStore.collection("GeographicAddress");
        if(trackingID.matches("")) {
            if(orderID == -1) {
                finish();
            } else {
                queryDatabyOrder(orderID);
            }
        } else {
            queryData();
        }
    }

    /**
     * TrackingID alapjan query
     *
     * @return void
     */
    private void queryData() {
        mCheckpointsData.clear();
        trackedItem = null;
        mTracking.whereEqualTo("id", trackingID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot documents : queryDocumentSnapshots) {
                TrackedItem item = documents.toObject(TrackedItem.class);
                Log.d("Tracking", "Query -> ID: "+item.getID());
                trackedItem = item;
                for(Integer ID : item.getCheckpoint()) {
                    mCheckpoint.whereEqualTo("id", ID).get().addOnSuccessListener(queryDocumentSnapshot -> {
                        for(QueryDocumentSnapshot document : queryDocumentSnapshot) {
                            CheckPointsItem checkpoint = document.toObject(CheckPointsItem.class);
                            mCheckpointsData.add(checkpoint);
                            mAdapter.notifyDataSetChanged();
                            Log.d("Tracking", "Query -> CheckPointID: "+checkpoint.getId().toString());
                        }
                    });
                }
                if(item.getOrder() == -1) {
                    mCheckpointsData.add(new CheckPointsItem("","", new Date(0),99,"User deleted"));
                    mAdapter.notifyDataSetChanged();
                }
            }
            if(trackedItem == null) {
                Toast.makeText(TrackingActivity.this, "No Shipment found!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                queryAddress(trackedItem.getAddressTo());
                Log.d("Tracking", "Query finished!");
            }
            queryAddress(trackedItem.getAddressTo());
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(TrackingActivity.this, "Server communication error!", Toast.LENGTH_LONG).show();
                Log.d("Tracking", "Query failed! "+e);
                finish();
            }
        });
        Log.d("Tracking", "Query running!");
    }

    /**
     * orderID alapjan query
     *
     * @return void
     */
    public void queryDatabyOrder(Integer orderID) {
        mCheckpointsData.clear();
        trackedItem = null;
        mTracking.whereEqualTo("order", orderID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot documents : queryDocumentSnapshots) {
                TrackedItem item = documents.toObject(TrackedItem.class);
                Log.d("Tracking", "Query -> ID: "+item.getID());
                trackedItem = item;
                for(Integer ID : item.getCheckpoint()) {
                    mCheckpoint.whereEqualTo("id", ID).get().addOnSuccessListener(queryDocumentSnapshot -> {
                        for(QueryDocumentSnapshot document : queryDocumentSnapshot) {
                            CheckPointsItem checkpoint = document.toObject(CheckPointsItem.class);
                            mCheckpointsData.add(checkpoint);
                            mAdapter.notifyDataSetChanged();
                            Log.d("Tracking", "Query -> CheckPointID: "+checkpoint.getId().toString());
                        }
                    });
                }
                if(item.getOrder() == -1) {
                    mCheckpointsData.add(new CheckPointsItem("","", new Date(0),99,"User deleted"));
                    mAdapter.notifyDataSetChanged();
                }
            }
            if(trackedItem == null) {
                Toast.makeText(TrackingActivity.this, "No Shipment found!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                queryAddress(trackedItem.getAddressTo());
                Log.d("Tracking", "Query finished!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(TrackingActivity.this, "Server communication error!", Toast.LENGTH_LONG).show();
                Log.d("Tracking", "Query failed! "+e);
                finish();
            }
        });
        Log.d("Tracking", "Query running!");
    }

    /**
     * Ha siman trackingID alapjan van lekerdezes akkor guest login
     *
     * @return void
     */
    public void loginAsGuest() {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.i("Login", "Guest Login SUCCESSFULLY TRIGGERED\nLoginActivity->MainActivity");
                } else {
                    Log.i("Login", "Guest Login FAILED TRIGGERED\nLoginActivity->LoginActivity");
                    Toast.makeText(TrackingActivity.this, "An error occurred: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    /**
     * TrackedItem address lekerdezese(GeographicAddress)
     *
     * @return void
     */
    private void queryAddress(Integer addressTo) {
        GeographicAddressTo = null;
        mGeographic.whereEqualTo("id", addressTo).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot documents : queryDocumentSnapshots) {
                GeographicAddressItem item = documents.toObject(GeographicAddressItem.class);
                Log.d("Tracking", "GeoQuery -> ID: "+item.getId());
                GeographicAddressTo = item;
            }
            if(GeographicAddressTo == null) {
                Toast.makeText(TrackingActivity.this, "No Geo found!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                setTexts();
                Log.d("Tracking", "GeoQuery finished!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(TrackingActivity.this, "Server communication error!", Toast.LENGTH_LONG).show();
                Log.d("Tracking", "GeoQuery failed! "+e);
                finish();
            }
        });
        Log.d("Tracking", "GeoQuery running!");
    }

    /**
     * Lekerdezesel utan a text mezok beallitasa
     *
     * @return void
     */
    private void setTexts() {
        TextView mID;
        TextView mCarrier;
        TextView mTimestamp;
        TextView mAddress1;
        TextView mAddress2;
        TextView mAddress3;

        mID = findViewById(R.id.trackingID_write);
        mCarrier = findViewById(R.id.carrierText_tracking_write);
        mTimestamp = findViewById(R.id.lastDateText_write);
        mAddress1 = findViewById(R.id.addressText1_write);
        mAddress2 = findViewById(R.id.addressText2_write);
        mAddress3 = findViewById(R.id.addressText3_write);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strDate = dateFormat.format(trackedItem.getTrackingDate());
        String mAddress1_Str = GeographicAddressTo.getStateOrProvince();
        String mAddress2_Str = GeographicAddressTo.getPostcode()+" "+GeographicAddressTo.getCity();
        String mAddress3_Str = GeographicAddressTo.getStreetName()+" "+GeographicAddressTo.getStreetType()+" "+GeographicAddressTo.getStreetNr();

        mID.setText(trackedItem.getID());
        mCarrier.setText(trackedItem.getCarrier());
        mTimestamp.setText(strDate);
        mAddress1.setText(mAddress1_Str);
        mAddress2.setText(mAddress2_Str);
        mAddress3.setText(mAddress3_Str);

        findViewById(R.id.Tracking_Table).animate().alpha(1).setDuration(1000);
        findViewById(R.id.progressBar).animate().alpha(0).setDuration(1000);
    }

    /**
     * Vissza gomb
     *
     * @return void
     */
    public void backToMain(View view) {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        //Logged felhasznalo a User-re, guest pedig a Main-re
        if (user != null && user.getEmail() != null) {
            Log.i("Orders", "FIN ACTIVITY TRIGGERED\nTrackingActivity->OrdersActivity");
            Intent orders = new Intent(this, OrdersActivity.class);
            orders.putExtra("SECRET_KEY", SECRET_KEY);
            startActivity(orders);
        } else {
            Log.i("Orders", "FIN ACTIVITY TRIGGERED\nTrackingActivity->MainActivity\n");
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
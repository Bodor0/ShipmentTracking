package com.example.shipmenttracking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class UserActivity extends AppCompatActivity {
    private FirebaseFirestore mFireStore;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private CollectionReference user_Data_ref;
    private userDataItem user_Data;
    private static final int SECRET_KEY = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        if (getSupportActionBar() != null) { getSupportActionBar().hide(); }
        int SECRET_KEY = getIntent().getIntExtra("SECRET_KEY", 0);
        if(SECRET_KEY != 99) { finish(); }

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null && user.getEmail() != null) {
            Log.i("User", "Authenticated user TRIGGERED\nUserActivity->UserActivity");
        } else {
            Log.i("User", "Unauthenticated user TRIGGERED\nUserActivity->MainActivity");
            finish();
        }

        mFireStore = FirebaseFirestore.getInstance();
        user_Data_ref = mFireStore.collection("users");
        getUserData(user.getUid());
    }

    /**
     * User adatok query
     *
     * @return void
     */
    private void getUserData(String UID) {
        user_Data = null;

        user_Data_ref.whereEqualTo("uid", UID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                userDataItem item = document.toObject(userDataItem.class);
                Log.d("User", "Query -> ID: "+item.getUid());
                user_Data = item;
            }
            if(user_Data != null) {
                Log.d("User", "Query finished!");
                setTexts();
            } else {
                userModifyView(null);
                Log.i("User", "FIN ACTIVITY TRIGGERED\nUserActivity->UserModifyActivity");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d("User", "Query failed! "+e);
            }
        });
        Log.d("User", "Query running!");
    }

    /**
     * Query utan text-ek beallitasa
     *
     * @return void
     */
    private void setTexts() {
        TextView mName;
        TextView mEmail;
        TextView mAddress;
        TextView mAddress_state;
        String addressStr;

        mName = findViewById(R.id.nameText_user_write);
        mEmail = findViewById(R.id.emailText_user_write);
        mAddress = findViewById(R.id.addressText_user_write);
        mAddress_state = findViewById(R.id.addressText_user_state_write);
        if(user_Data == null) {
            addressStr = "";
        } else {
            addressStr = user_Data.getPostcode() + " " + user_Data.getCity() + ", " + user_Data.getStreetName() + " " + user_Data.getStreetType() + " " + user_Data.getStreetNr();
        }
        mName.setText(user_Data.getName());
        mEmail.setText(user.getEmail());
        mAddress.setText(addressStr);
        mAddress_state.setText(user_Data.getState());

        findViewById(R.id.User_Table).animate().alpha(1).setDuration(1000);
        findViewById(R.id.changeDataButton).animate().alpha(1).setDuration(1000);
        findViewById(R.id.ordersButton).animate().alpha(1).setDuration(1000);
        findViewById(R.id.logoutButton).animate().alpha(1).setDuration(1000);
        findViewById(R.id.progressBar_user).animate().alpha(0).setDuration(1000);
    }

    /**
     * Kijelentkezes -> MainActivity inditasa
     *
     * @return void
     */
    public void logoutView(View view) {
        Intent main = new Intent(this, MainActivity.class);
        mAuth.signOut();
        Log.i("User", "FIN ACTIVITY TRIGGERED\nUserActivity->MainActivity");
        startActivity(main);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * UserModifyActivity inditasa
     *
     * @return void
     */
    public void userModifyView(View view) {
        Intent userModify = new Intent(this, UserModifyActivity.class);
        userModify.putExtra("SECRET_KEY", SECRET_KEY);
        if(user_Data == null) {
            userModify.putExtra("disableBack", true);
        }
        Log.i("User", "FIN ACTIVITY TRIGGERED\nUserActivity->UserModifyActivity");
        startActivity(userModify);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * OrderActivity inditasa
     *
     * @return void
     */
    public void ordersView(View view) {
        Intent orders = new Intent(this, OrdersActivity.class);
        orders.putExtra("SECRET_KEY", SECRET_KEY);
        Log.i("User", "FIN ACTIVITY TRIGGERED\nUserActivity->OrdersActivity");
        startActivity(orders);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
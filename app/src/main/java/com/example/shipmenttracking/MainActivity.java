package com.example.shipmenttracking;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private static final int SECRET_KEY = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) { getSupportActionBar().hide(); }

        new RandomAsyncTask().execute();

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        startService(new Intent(this,NotificationService.class));
        setZoomingAnim();
    }

    /**
     * TrackingID csekkolas majd TrackingActivity
     *
     * @param view No parameters in this use case.
     * @return void
     */
    public void searchShipment(View view) {
        Intent tracking = new Intent(this, TrackingActivity.class);
        tracking.putExtra("SECRET_KEY", SECRET_KEY);
        EditText trackingID = findViewById(R.id.TrackingIDText);
        tracking.putExtra("trackingID", trackingID.getText().toString());

        if(trackingID.getText().toString().matches("")) {
            Toast.makeText(MainActivity.this, "No TrackingID entered!", Toast.LENGTH_LONG).show();
        } else {
            Log.i("Main", "FIN ACTIVITY TRIGGERED\nMainActivity->TrackingActivity\nEntered TrackingID: "+trackingID.getText().toString());
            startActivity(tracking);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    /**
     * Zooming animation inditasa
     *
     * @return void
     *
     * 1000ms-kent oda-vissza a zoom animacio a fokepernyon
     */
    private void setZoomingAnim() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            boolean in_out = true;
            final Animation zoom_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
            final Animation zoom_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
            @Override
            public void run() {
                if(in_out) {
                    findViewById(R.id.logoText).startAnimation(zoom_in);
                    findViewById(R.id.logoImage).startAnimation(zoom_in);
                    in_out = false;
                } else {
                    findViewById(R.id.logoText).startAnimation(zoom_out);
                    findViewById(R.id.logoImage).startAnimation(zoom_out);
                    in_out = true;
                }
            }
        }, 1000, 1000);
    }

    /**
     * LoginActivity inditasa
     *
     * @param view No parameters in this use case.
     * @return void
     */
    public void loginView(View view) {
        Intent login = new Intent(this, LoginActivity.class);
        login.putExtra("SECRET_KEY", SECRET_KEY);

        Log.i("Main", "FIN ACTIVITY TRIGGERED\nMainActivity->LoginActivity");
        startActivity(login);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * RegistrationActivity inditasa
     *
     * @param view No parameters in this use case.
     * @return void
     */
    public void registrationView(View view) {
        Intent registration = new Intent(this, RegistrationActivity.class);
        registration.putExtra("SECRET_KEY", SECRET_KEY);

        Log.i("Main", "FIN ACTIVITY TRIGGERED\nMainActivity->RegistrationActivity");
        startActivity(registration);
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Main", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Main", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Main", "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Main", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(user != null && user.getEmail() != null) { mAuth.signOut(); }
        Log.i("Main", "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("Main", "onRestart");
    }
}
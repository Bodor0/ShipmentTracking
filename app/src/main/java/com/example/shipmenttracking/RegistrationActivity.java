package com.example.shipmenttracking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegistrationActivity extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private CollectionReference user_Data;
    private static final int SECRET_KEY = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        if (getSupportActionBar() != null) { getSupportActionBar().hide(); }
        int SECRET_KEY = getIntent().getIntExtra("SECRET_KEY", 0);
        if(SECRET_KEY != 99) { finish(); }

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user_Data = mFirestore.collection("users");
    }

    /**
     * Regisztacio
     *
     * @return void
     */
    public void registration(View view) {
        EditText emailET = findViewById(R.id.emailText);
        EditText passwordET = findViewById(R.id.passwordText);
        EditText password2ET = findViewById(R.id.password2Text);
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String password2 = password2ET.getText().toString();

        if(!password.equals(password2)) {
            Log.i("Registration", "Password and confirm password does not match TRIGGERED\nRegistrationActivity->RegistrationActivity");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    loginWithWrite(email, password);
                    Log.i("Registration", "Registration SUCCESSFULLY TRIGGERED\nRegistrationActivity->MainActivity");
                    Toast.makeText(RegistrationActivity.this, "User created!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Log.i("Registration", "Registration FAILED TRIGGERED\nRegistrationActivity->RegistrationActivity");
                    Toast.makeText(RegistrationActivity.this, "User not created: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Regisztracio utan login adatok irasaval
     *
     * @return void
     */
    private void loginWithWrite(String email, String password) {
        EditText cityET = findViewById(R.id.cityText);
        String city = cityET.getText().toString();
        EditText nameET = findViewById(R.id.nameText);
        String name = nameET.getText().toString();
        EditText postcodeET = findViewById(R.id.postcodeNumber);
        String postcode = postcodeET.getText().toString();
        EditText stateET = findViewById(R.id.stateText);
        String state = stateET.getText().toString();
        EditText streetET = findViewById(R.id.streetText);
        String street = streetET.getText().toString();
        EditText streettypeET = findViewById(R.id.streetTypeText);
        String streettype = streettypeET.getText().toString();
        EditText streetnrET = findViewById(R.id.streetNrText);
        String streetnr = streetnrET.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.i("Registration", "Login SUCCESSFULLY TRIGGERED\nRegistrationActivity->UserActivity");
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    user_Data.add(new userDataItem(
                            city,
                            name,
                            postcode,
                            state,
                            street,
                            streetnr,
                            streettype,
                            user.getUid()
                    ));
                    startUser();
                } else {
                    Toast.makeText(RegistrationActivity.this, "User is created but not logged in: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Regisztracio utani Login kezelese
     *
     * @return void
     */
    private void startUser() {
        Log.i("Registration", "FIN ACTIVITY TRIGGERED\nRegistrationActivity->UserActivity");
        Intent userData = new Intent(this, UserActivity.class);
        userData.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(userData);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        Log.i("Registration", "FIN ACTIVITY TRIGGERED\nRegistrationActivity->MainActivity");
        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}

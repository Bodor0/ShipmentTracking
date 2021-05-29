package com.example.shipmenttracking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int SECRET_KEY = 99;
    private static final int RC_SIGN_IN = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null) { getSupportActionBar().hide(); }
        int SECRET_KEY = getIntent().getIntExtra("SECRET_KEY", 0);
        if(SECRET_KEY != 99) { finish(); }

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.i("Google Login", "Google Login TRIGGERED: " + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.i("Google Login", "Google Login FAILED TRIGGERED: ", e);
            }
        }
    }

    /**
     * Google login
     *
     * @return void
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.i("Google Login", "Google Login SUCCESSFULLY TRIGGERED\nLoginActivity->UserActivity");
                    Toast.makeText(LoginActivity.this, "User logged in!", Toast.LENGTH_LONG).show();
                    startUser();
                } else {
                    Log.i("Google Login", "Google Login FAILED TRIGGERED\nLoginActivity->LoginActivity");
                    Toast.makeText(LoginActivity.this, "User not logged in: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Login with email and password
     *
     * @return void
     */
    public void login(View view) {
        EditText emailET = findViewById(R.id.emailText);
        EditText passwordET = findViewById(R.id.passwordText);
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.i("Login", "Login SUCCESSFULLY TRIGGERED\nLoginActivity->UserActivity");
                    Toast.makeText(LoginActivity.this, "User logged in!", Toast.LENGTH_LONG).show();
                    startUser();
                } else {
                    Log.i("Login", "Login FAILED TRIGGERED\nLoginActivity->LoginActivity");
                    Toast.makeText(LoginActivity.this, "User not logged in: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Google login
     *
     * @return void
     */
    public void loginWithGoogle(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * UserActivity inditasa
     *
     * @return void
     */
    private void startUser() {
        Log.i("Login", "FIN ACTIVITY TRIGGERED\nLoginActivity->UserActivity");
        Intent userData = new Intent(this, UserActivity.class);
        userData.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(userData);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        Log.i("Login", "FIN ACTIVITY TRIGGERED\nLoginActivity->MainActivity");
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
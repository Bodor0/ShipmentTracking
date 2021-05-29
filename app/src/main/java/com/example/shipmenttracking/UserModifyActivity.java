package com.example.shipmenttracking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class UserModifyActivity extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private CollectionReference user_Data_ref;
    private userDataItem user_Data;
    private String users_ID;
    private static final int SECRET_KEY = 99;
    private Boolean disableBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermodify);
        if (getSupportActionBar() != null) { getSupportActionBar().hide(); }
        int SECRET_KEY = getIntent().getIntExtra("SECRET_KEY", 0);
        disableBack = getIntent().getBooleanExtra("disableBack", false);
        if(SECRET_KEY != 99) { finish(); }

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        user_Data_ref = mFirestore.collection("users");
        if(user != null && user.getEmail() != null) {
            Log.i("UserModify", "Authenticated user TRIGGERED\nUserModifyActivity->UserModifyActivity");
        } else {
            Log.i("UserModify", "Unauthenticated user TRIGGERED\nUserModifyActivity->MainActivity");
            finish();
        }
        getUserData(user.getUid());
        if(disableBack) {
            Toast.makeText(UserModifyActivity.this, "You need to change your personal data!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * User adatainak lekerese
     *
     * @return void
     */
    private void getUserData(String UID) {
        user_Data = null;

        user_Data_ref.whereEqualTo("uid", UID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                userDataItem item = document.toObject(userDataItem.class);
                Log.d("User", "UserData Query -> ID: "+document.getId());
                users_ID = document.getId();
                user_Data = item;
            }
            if(user_Data != null) {
                Log.d("User", "Query finished!");
            } else {
                user_Data = new userDataItem("","","","","","","",user.getUid());
                Log.d("User", "Query not success! Making placeholder data!");
            }
            setModifyTexts();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d("User", "Query failed! "+e);
            }
        });
        Log.d("User", "Query running!");
    }

    /**
     * User data modositasa
     *
     * @return void
     */
    public void modifyUserData(View view) {
        Boolean changed = false;
        EditText cityET = findViewById(R.id.cityText_modify);
        String city = cityET.getText().toString();
        EditText nameET = findViewById(R.id.nameText_modify);
        String name = nameET.getText().toString();
        EditText emailET = findViewById(R.id.emailText_modify);
        String email = emailET.getText().toString();
        EditText passwordET = findViewById(R.id.passwordText_modify);
        String password = passwordET.getText().toString();
        EditText postcodeET = findViewById(R.id.postcodeNumber_modify);
        String postcode = postcodeET.getText().toString();
        EditText stateET = findViewById(R.id.stateText_modify);
        String state = stateET.getText().toString();
        EditText streetET = findViewById(R.id.streetText_modify);
        String street = streetET.getText().toString();
        EditText streettypeET = findViewById(R.id.streetTypeText_modify);
        String streettype = streettypeET.getText().toString();
        EditText streetnrET = findViewById(R.id.streetNrText_modify);
        String streetnr = streetnrET.getText().toString();

        if(name.matches("") ||
                state.matches("") ||
                postcode.matches("") ||
                city.matches("") ||
                street.matches("") ||
                streetnr.matches("") ||
                streettype.matches("") ||
                email.matches("")) {
            Toast.makeText(UserModifyActivity.this, "User Data Not Changed!\nEmpty Text Box!", Toast.LENGTH_LONG).show();
            Log.i("UserModify", "No User Data Changed!\nEmpty Text Box!\nUserModifyActivity->UserActivity");
        } else {
            if(!disableBack) {
                if(name.matches(user_Data.getName())) {} else { user_Data_ref.document(users_ID).update("name", name); changed = true; Log.i("UserModify", "Name modify"); }
                if(state.matches(user_Data.getState())) {} else { user_Data_ref.document(users_ID).update("state", state); changed = true; Log.i("UserModify", "State modify"); }
                if(postcode.matches(user_Data.getPostcode())) {} else { user_Data_ref.document(users_ID).update("postcode", postcode); changed = true; Log.i("UserModify", "Postcode modify"); }
                if(city.matches(user_Data.getCity())) {} else {  user_Data_ref.document(users_ID).update("city", city); changed = true; Log.i("UserModify", "City modify"); }
                if(street.matches(user_Data.getStreetName())) {} else { user_Data_ref.document(users_ID).update("streetName", street); changed = true; Log.i("UserModify", "StreetName modify"); }
                if(streetnr.matches(user_Data.getStreetNr())) {} else { user_Data_ref.document(users_ID).update("streetNr", streetnr); changed = true; Log.i("UserModify", "StreetNr modify"); }
                if(streettype.matches(user_Data.getStreetType())) {} else { user_Data_ref.document(users_ID).update("streetType", streettype); changed = true; Log.i("UserModify", "StreetType modify"); }

                if(email.matches(user.getEmail())) {} else { user.updateEmail(email); changed = true; Log.i("UserModify", "Email modify"); }
                if(password.matches("")) {} else { user.updatePassword(password); changed = true; Log.i("UserModify", "Password modify"); }
            } else {
                user_Data_ref.add(new userDataItem(city, name, postcode, state, street, streetnr, streettype, user.getUid()));
                changed=true;
            }
            if(changed) {
                Log.i("UserModify", "User Data Changed!\nUserModifyActivity->UserActivity");
                Toast.makeText(UserModifyActivity.this, "User Data Changed Successfully!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(UserModifyActivity.this, "User Data Not Changed!", Toast.LENGTH_LONG).show();
                Log.i("UserModify", "No User Data Changed!\nUserModifyActivity->UserActivity");
            }
            finish();
        }
    }

    /**
     * Query utan text-ek beallitasa
     *
     * @return void
     */
    private void setModifyTexts() {
        EditText mName;
        EditText mEmail;
        EditText mState;
        EditText mPostcode;
        EditText mCity;
        EditText mStreet;
        EditText mStreetType;
        EditText mStreetNr;

        mName = findViewById(R.id.nameText_modify);
        mEmail = findViewById(R.id.emailText_modify);
        mState = findViewById(R.id.stateText_modify);
        mPostcode = findViewById(R.id.postcodeNumber_modify);
        mCity = findViewById(R.id.cityText_modify);
        mStreet = findViewById(R.id.streetText_modify);
        mStreetType = findViewById(R.id.streetTypeText_modify);
        mStreetNr = findViewById(R.id.streetNrText_modify);

        if(user_Data.getName().matches("")) {} else { mName.setText(user_Data.getName()); }
        if(user.getEmail().matches("")) {} else { mEmail.setText(user.getEmail()); }
        if(user_Data.getState().matches("")) {} else { mState.setText(user_Data.getState()); }
        if(user_Data.getPostcode().matches("")) {} else { mPostcode.setText(user_Data.getPostcode()); }
        if(user_Data.getCity().matches("")) {} else { mCity.setText(user_Data.getCity()); }
        if(user_Data.getStreetName().matches("")) {} else { mStreet.setText(user_Data.getStreetName()); }
        if(user_Data.getStreetType().matches("")) {} else { mStreetType.setText(user_Data.getStreetType()); }
        if(user_Data.getStreetNr().matches("")) {} else { mStreetNr.setText(user_Data.getStreetNr()); }

        findViewById(R.id.hideLayout).animate().alpha(1).setDuration(1000);
        if(!disableBack) { findViewById(R.id.backButton_modify).animate().alpha(1).setDuration(1000); }
        findViewById(R.id.progressBar_usermodify).animate().alpha(0).setDuration(1000);
    }

    /**
     * Vissza gomb
     *
     * @return void
     */
    public void backButtonView(View view) {
        Intent userIntent = new Intent(this, UserActivity.class);
        userIntent.putExtra("SECRET_KEY", SECRET_KEY);
        Log.i("UserModify", "FIN ACTIVITY TRIGGERED\nUserModifyActivity->UserActivity");
        startActivity(userIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        Intent user = new Intent(this, UserActivity.class);
        user.putExtra("SECRET_KEY", SECRET_KEY);
        Log.i("UserModify", "FIN ACTIVITY TRIGGERED\nUserModifyActivity->UserActivity");
        startActivity(user);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

package com.cs2340.project2.Auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cs2340.project2.MainActivity;
import com.cs2340.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    Button logout_btn;

    Button delete_btn;

    Button update_btn;

    Button back_btn;

    CharSequence text = "";

    private FirebaseUser currentUser;

    private final String TAG = "ProfileActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout_view);

        logout_btn = findViewById(R.id.button_profile_logout);
        delete_btn = findViewById(R.id.button_profile_delete_account);
        update_btn = findViewById(R.id.button_profile_update);

        back_btn = findViewById(R.id.profile_back_button);

        // get current FirebaseAuth user, useful for updating profile, logging out, deleting account
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // logout and go back to login page
                logout();
                // start login activity

                text = "Logged Out Successfully, Returning to Login Page";
                Toast.makeText(ProfileActivity.this, text, Toast.LENGTH_SHORT).show();
                startLoginActivity();
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete account and go back to login page
                deleteAccount(currentUser);

                text = "Account Deleted";
                Toast.makeText(ProfileActivity.this, text, Toast.LENGTH_SHORT).show();
                // start login activity
                startLoginActivity();
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open update info prompt (email / password)
                updateAccount();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back to the main page
                goBackToMain();
            }
        });

    }

    private void logout() {
        // log the user out
        FirebaseAuth.getInstance().signOut();
    }

    private void deleteAccount(FirebaseUser currentUser) {
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "deleteAccount:Successful");
                } else {
                    Log.d(TAG, task.getException().toString());
                }
            }
        });
    }

    private void updateAccount() {
        Intent intent = new Intent(ProfileActivity.this, UpdateActivity.class);
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void goBackToMain() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }


}

package com.cs2340.project2.Auth;

import static com.cs2340.project2.Auth.LoginActivity.userEmail;
import static com.cs2340.project2.Auth.LoginActivity.userPassword;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.cs2340.project2.R;
import com.cs2340.project2.R.id;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class UpdateActivity extends AppCompatActivity {
    private Button cancel;
    private Button savePassword;

    private EditText oldPassword;
    private EditText password;

    private FirebaseUser currentUser;

    private final String TAG = "UpdateActivityLog";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_update_popup_view2);

        // get current instance of FirebaseAuth
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        cancel = findViewById(R.id.profile_update_cancel_button);
        savePassword = findViewById(R.id.profile_update_password_button);

        oldPassword = findViewById(R.id.profile_update_oldPassword_EditText);
        password = findViewById(R.id.profile_update_password_EditText);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back to ProfileActivity
                cancel();
            }
        });

        savePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password_str = password.getText().toString();
                String oldPassword_str = oldPassword.getText().toString();

                // make sure password matches
                if (oldPassword_str.equals(userPassword)) {
                    if (oldPassword_str.equals(userPassword)) {
                        updatePassword(password_str, currentUser);
                    } else {
                        Toast.makeText(UpdateActivity.this, "Please enter a valid password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpdateActivity.this, "Please enter your old password!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void cancel() {
        Intent intent = new Intent(UpdateActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void updatePassword(String password, FirebaseUser user) {
        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User password updated");

                    Toast.makeText(getApplicationContext(), "Password Updated!", Toast.LENGTH_SHORT).show();
                } else {
                    CharSequence text = "Update Password Failed";
                    Log.d(TAG, task.getException().toString());
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        text = "Password needs to be at least 6 characters";
                    } catch (FirebaseAuthInvalidUserException e) {
                        text = "Credentials are no longer valid";
                    } catch (FirebaseAuthUserCollisionException e) {
                        text = "Please reauthenticate!";
                    } catch (Exception e) {
                        Log.d("signUpFirebase", e.getMessage());
                    }
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
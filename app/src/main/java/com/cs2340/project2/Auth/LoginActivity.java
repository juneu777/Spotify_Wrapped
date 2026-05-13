package com.cs2340.project2.Auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cs2340.project2.MainActivity;
import com.cs2340.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import androidx.annotation.NonNull;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;

    Button signUpBtn;

    private FirebaseAuth myAuth;

    public static String userPassword;

    public static String userEmail;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout_view);

        // get firebaseAuth instance
        myAuth = FirebaseAuth.getInstance();

        // get buttons from the login view
        loginBtn = (Button) findViewById(R.id.button_login_login);
        signUpBtn = (Button) findViewById(R.id.button_login_signup);

        // get email
        EditText loginEmail = (EditText) findViewById(R.id.editText_login_EmailAddress);
        // get password
        EditText loginPassword = (EditText) findViewById(R.id.editText_login_password);

        // create on click listeners for the buttons
        // on click listener for login
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call login function to handle login logic
                login(loginEmail, loginPassword);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call signup function to handle signup logic
                signUpAccount(v);
            }
        });
    }

    private void login(EditText loginEmail, EditText loginPassword) {
        String loginEmail_string = (String) loginEmail.getText().toString();
        String loginPassword_string = (String) loginPassword.getText().toString();
        userPassword = loginPassword.getText().toString();
        userEmail = loginEmail.getText().toString();

        CharSequence text = "";
        int duration = Toast.LENGTH_SHORT;

        if (!loginEmail_string.equals("") && !loginPassword_string.equals("")) {
            signInFirebase(loginEmail_string, loginPassword_string, myAuth);
        }
        else if (loginEmail_string.equals("")) {
            // display a toast to the user to say that the loginEmail is empty
            text = "Email is empty!";

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        } else if (loginPassword_string.equals("")) {
            // display a toast to the user to say that the loginPassword is empty
            text = "Password is empty!";

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
    }
    private void signUpAccount(View v) {
        // open a new activity with signup_layout_view
        Intent intent = new Intent(v.getContext(), SignupActivity.class);
        v.getContext().startActivity(intent);
    }

    private void signInFirebase(String email, String password, FirebaseAuth myAuth) {
        String TAG = "signInFirebase";

        myAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // login to application -> redirect to homepage
                            Log.d(TAG, "signInWithEmailAndPassword:success");
                            Toast.makeText(getApplicationContext(), "Login Successful, redirecting to home page!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            CharSequence text = "Login Failed";
                            Log.d(TAG, task.getException().toString());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                text = "Email does not exist";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                text = "Invalid password";
                            } catch (Exception e) {

                            }
                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

}

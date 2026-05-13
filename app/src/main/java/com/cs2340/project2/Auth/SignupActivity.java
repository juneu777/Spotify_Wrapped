package com.cs2340.project2.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cs2340.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

// this class deals with the signup activity and the signup layout view
public class SignupActivity extends AppCompatActivity {

    Button backToLoginBtn;
    Button signUpBtn;
    EditText email;
    EditText password;

    private FirebaseAuth myAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout_view);

        // get FirebaseAuth instance
        myAuth = FirebaseAuth.getInstance();

        // get buttons from the signup view
        backToLoginBtn = (Button) findViewById(R.id.button_signup_login);
        signUpBtn = (Button) findViewById(R.id.button_signup_signup);

        // get email
        email = (EditText) findViewById(R.id.editText_signup_EmailAddress);
        // get password
        password = (EditText) findViewById(R.id.editText_signup_Password);

        // create on click listeners for the buttons
        backToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call backToLogin(v) which starts the intent for the login page
                backToLogin(v);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call signup() which deals with the signup logic required
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                signUp(emailString, passwordString, myAuth);
            }
        });

    }

    private void backToLogin(View v) {
        // start the login activity -> starts up the login layout view as well
        Intent intent = new Intent(v.getContext(), LoginActivity.class);
        v.getContext().startActivity(intent);
    }

    private void signUp(String email, String password, FirebaseAuth myAuth) {
        // TODO: performs the sign up logic and signs up a user using Firebase Auth
        CharSequence text = "";
        int duration = Toast.LENGTH_SHORT;

        if (!email.equals("") && !password.equals("")) {
            // call method from FirebaseAccountModel in order to sign in the current user
            signUpFirebase(email, password, myAuth);
        }
        else if (email.equals("")) {
            // display a toast to the user to say that the loginEmail is empty
            text = "Email is empty!";

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        } else if (password.equals("")) {
            // display a toast to the user to say that the loginPassword is empty
            text = "Password is empty!";

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
    }

    private void signUpFirebase(String email, String password, FirebaseAuth myAuth) {
        myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("signUpFirebase", "createUserWithEmail:success");
                    Toast.makeText(getApplicationContext(), "Registration successful, redirecting to login page!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    CharSequence text = "Registration Failed";
                    Log.d("signUpFirebase", task.getException().toString());
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        text = "Password needs to be at least 6 characters";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        text = "Invalid email or password";
                    } catch (FirebaseAuthUserCollisionException e) {
                        text = "Email is already registered";
                    } catch (Exception e) {
                        Log.d("signUpFirebase", e.getMessage());
                    }
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}

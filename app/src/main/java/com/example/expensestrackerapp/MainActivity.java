package com.example.expensestrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText mEmail;
    private EditText mPassword;
    private Button mBtnLogin;
    private TextView mForgetPassword;
    private TextView mSignup;

    private ProgressDialog mDialog;

    // Firebase
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This is so if the user is already logged in and is authorized
        // There is no need for login
        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        mDialog = new ProgressDialog(this);
        loginDetails();
    }

    private void loginDetails(){
        mEmail = findViewById(R.id.email_login);
        mPassword = findViewById(R.id.password_login);
        mBtnLogin = findViewById(R.id.btn_login);
        mForgetPassword = findViewById(R.id.forgot_password);
        mSignup = findViewById(R.id.dont_have_account);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password Required");
                    return;
                }

                mDialog.setMessage("Processing");
                mDialog.show();

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            mDialog.dismiss();
                            Log.d(TAG, "Login successful");
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }else{
                            String localizedMessage = task.getException().getLocalizedMessage();
                            Log.d(TAG, "Error during user login attempt: "  + localizedMessage);
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "Login Failed: " + localizedMessage,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                });
            }
        });

        // Change to register activity because user doesn't have an account
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        // If user has forgotten password got to reset activity
        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResetActivity.class));
            }
        });
    }
}
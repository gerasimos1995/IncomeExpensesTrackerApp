package com.example.expensestrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mBtnSignup;
    private TextView mAlready_user;

    private ProgressDialog mDialog;

    // Firebase
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mDialog = new ProgressDialog(this);
        fAuth = FirebaseAuth.getInstance();
        register();
    }

    private void register(){
        mEmail = findViewById(R.id.email_register);
        mPassword = findViewById(R.id.password_register);
        mBtnSignup = findViewById(R.id.btn_sign_up);
        mAlready_user = findViewById(R.id.already_user);

        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required.."); // I could add a toast msg
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password Required");
                    return;
                }
                // I can add more validations here

                mDialog.setMessage("Processing");
                mDialog.show();

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Registration Complete", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }else{
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


        // Change activity to login because user has an account already
        mAlready_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}
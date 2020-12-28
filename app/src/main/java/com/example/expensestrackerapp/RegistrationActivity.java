package com.example.expensestrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button btnSignup;
    private TextView already_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        register();
    }

    private void register(){
        email = findViewById(R.id.email_register);
        password = findViewById(R.id.password_register);
        btnSignup = findViewById(R.id.btn_sign_up);
        already_user = findViewById(R.id.already_user);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memail = email.getText().toString().trim();
                String mpassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(memail)){
                    email.setError("Email Required.."); // I could add a toast msg
                    return;
                }

                if (TextUtils.isEmpty(mpassword)){
                    password.setError("Password Required");
                    return;
                }
                // I can add more validations here
            }
        });


        // Change activity to login because user has an account already
        already_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}
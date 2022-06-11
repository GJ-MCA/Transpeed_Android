package com.vpg.transpeed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etUserName, etEmail, etMobile, etPassword, etConPassword;
    Button btnSignUp;
    TextView tvGotoSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etPassword = findViewById(R.id.etPassword);
        etConPassword = findViewById(R.id.etConPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvGotoSignIn = findViewById(R.id.tvGotoSignIn);

        btnSignUp.setOnClickListener(this);
        tvGotoSignIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnSignUp:

                break;

            case R.id.tvGotoSignIn:
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
    }
}
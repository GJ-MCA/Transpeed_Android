package com.vpg.transpeed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etUserName, etPassword;
    Button btnSignIn;
    TextView tvGotoSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvGotoSignUp = findViewById(R.id.tvGotoSignUp);

        btnSignIn.setOnClickListener(this);
        tvGotoSignUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tvGotoSignUp:
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;

            case R.id.btnSignIn:

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
package com.vpg.transpeed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class VerifyOTPActivity extends AppCompatActivity {
    EditText etOTP;
    Button btnVerifyOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpactivity);

        etOTP = findViewById(R.id.etOTP);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);

        Intent intent = getIntent();
        int otp = intent.getIntExtra("RAND", 0);
        Log.d("OTP", String.valueOf(otp));

        btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (otp == Integer.parseInt(etOTP.getText().toString())) {
                    Toast.makeText(VerifyOTPActivity.this, "OTP verified Successfully...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(VerifyOTPActivity.this, "OTP is Wrong...", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
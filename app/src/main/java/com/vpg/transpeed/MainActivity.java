package com.vpg.transpeed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.vpg.transpeed.Customer.CustomerHomeActivity;

public class MainActivity extends AppCompatActivity {
    ImageView ivLogo;

    public static final String PROFILE = "profile";
    public static final String USER_TYPE = "user_type";
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this code for remove layout top menu
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ivLogo = findViewById(R.id.ivLogo);

        SharedPreferences preferences = getSharedPreferences(PROFILE, MODE_PRIVATE);
        userType = preferences.getString(USER_TYPE, "");

        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //redirecting app users via roles
                    if (userType.equals("Admin")) {
                        //admin side ui
                    } else if (userType.equals("Customer")) {
                        //Customer side ui
                        Intent intent1 = new Intent(MainActivity.this, CustomerHomeActivity.class);
                        startActivity(intent1);
                    } else if (userType.equals("Manager")) {
                        //Manager side ui
                    } else if (userType.equals("PickUp/Delivery Person")) {
                        //PickUp/Delivery Person side ui
                    } else if (userType.equals("Truck Driver")) {
                        //Truck Driver side ui
                    } else {
                        //if user has not logged in redirecting to sign in screen
                        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
package com.vpg.transpeed.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.vpg.transpeed.Customer.Fragments.CustomerHomeFragment;
import com.vpg.transpeed.Customer.Fragments.CustomerNewOrderFragment;
import com.vpg.transpeed.Customer.Fragments.CustomerProfileFragment;
import com.vpg.transpeed.R;

public class CustomerHomeActivity extends AppCompatActivity {
    BottomNavigationView bnvMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        bnvMenu = findViewById(R.id.bnvMenu);

        Intent intent = getIntent();
        int number = intent.getIntExtra("PROFILE", 0);
        if (number == 3) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragContainer, new CustomerProfileFragment()).commit();
            bnvMenu.setSelectedItemId(R.id.menu_profile);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragContainer, new CustomerHomeFragment()).commit();
        }

        bnvMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;

                switch (item.getItemId()) {

                    case R.id.menu_home:
                        fragment = new CustomerHomeFragment();
                        break;

                    case R.id.menu_new_order:
                        fragment = new CustomerNewOrderFragment();
                        break;

                    case R.id.menu_profile:
                        fragment = new CustomerProfileFragment();
                        break;

                    default:
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragContainer, fragment).commit();

                return true;
            }
        });

    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}
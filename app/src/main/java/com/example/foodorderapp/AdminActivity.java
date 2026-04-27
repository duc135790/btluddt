package com.example.foodorderapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_admin);
        loadFragment(new AdminMenuFragment());
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.nav_admin_menu)        fragment = new AdminMenuFragment();
            else if (id == R.id.nav_admin_orders) fragment = new AdminOrdersFragment();
            else                                   fragment = new AdminStatsFragment();
            loadFragment(fragment);
            return true;
        });
    }

    private void loadFragment(Fragment f) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_admin, f).commit();
    }
}
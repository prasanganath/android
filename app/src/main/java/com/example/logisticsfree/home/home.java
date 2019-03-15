package com.example.logisticsfree.home;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.logisticsfree.R;

public class home extends AppCompatActivity {
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private HistoryFragment historyFragment;
    private RattingFragment rattingFragment;
    private SettingFragment settingFragment;
    private HomeFragment homeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mMainFrame = findViewById(R.id.main_frame);
        mMainNav = findViewById(R.id.main_nav);
        historyFragment = new HistoryFragment();
        rattingFragment = new RattingFragment();
        settingFragment = new SettingFragment();
        homeFragment = new HomeFragment();

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home: {
                        setFragment(homeFragment);
                        return true;
                    }
                    case R.id.nav_history: {
                        setFragment(historyFragment);
                        return true;
                    }
                    case R.id.nav_rating: {
                        setFragment(rattingFragment);
                        return true;
                    }
                    case R.id.nav_setting: {
                        setFragment(settingFragment);
                        return true;
                    }
                    default:
                        return false;
                }
            }

            private void setFragment(Fragment fragment) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, fragment);
                fragmentTransaction.commit();
            }
        });

        mMainNav.setSelectedItemId(R.id.nav_home);

    }
}
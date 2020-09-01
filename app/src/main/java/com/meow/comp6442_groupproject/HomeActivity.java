package com.meow.comp6442_groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.comp6442_groupproject.Model.User;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (getIntent() != null) {
            if (getIntent().getStringExtra("from") != null) {
                if(getIntent().getStringExtra("from").equals("fillSurvey")) {
                    Toast.makeText(HomeActivity.this, "Submit successfully",
                            Toast.LENGTH_SHORT).show();
                }
                else if(getIntent().getStringExtra("from").equals("BeforePostActivity")){
                    Toast.makeText(HomeActivity.this, "Post successfully",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        showFragment();
    }



    private void showFragment(){

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                changeFragment(new HomeFragment(), HomeFragment.class
                                        .getSimpleName());
                                break;
                            case R.id.navigation_mysurvey:
                                changeFragment(new MySurveyFragment(), MySurveyFragment.class
                                        .getSimpleName());
                                break;
                            case R.id.navigation_profile:
                                changeFragment(new ProfileFragment(), ProfileFragment.class
                                        .getSimpleName());
                                break;
                        }
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        changeFragment(new HomeFragment(), HomeFragment.class
                .getSimpleName());
    }



    public void changeFragment(Fragment fragment, String tagFragmentName) {

        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment currentFragment = mFragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        Fragment fragmentTemp = mFragmentManager.findFragmentByTag(tagFragmentName);
        if (fragmentTemp == null) {
            fragmentTemp = fragment;
            fragmentTransaction.add(R.id.frame_layout, fragmentTemp, tagFragmentName);
        } else {
            fragmentTransaction.show(fragmentTemp);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNowAllowingStateLoss();
    }


}

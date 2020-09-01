package com.meow.comp6442_groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CoverActivity extends AppCompatActivity {

    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

       sleep();


    }


    //Show cover page for 1.5s
    private void sleep(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1500); // Wait for the toast message disappear
                    selectActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }



    private void selectActivity(){
        if (fUser != null) {    //If already login, go to homepage
            Intent i = new Intent(CoverActivity.this,
                    HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        else {    //If not login, go to login page.

            Intent i = new Intent(CoverActivity.this,
                    MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}

package com.meow.comp6442_groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button login, signUp;
    FirebaseAuth auth;
    FirebaseUser fUser;
    EditText email, password;
    TextView forgetPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Send notificaion after sign up
        if (getIntent() != null) {
            if (getIntent().getStringExtra("from") != null) {
                Toast.makeText(MainActivity.this, "Verification email is sent",
                        Toast.LENGTH_SHORT).show();
            }
        }

        FirebaseApp.initializeApp(this);

        initUI();

    }



    private void initUI() {

        //Initialize variables
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email_login);
        login = findViewById(R.id.login_main);
        signUp = findViewById(R.id.signUp_main);
        forgetPassword = findViewById(R.id.forgetPassword_main);
        password = findViewById(R.id.password_login);


        //Set up Listeners
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });


        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ResetPasswordActivity.class));
                //Set up animation
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                //Set up animation
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        // CLose keyboard if click outside the EditText
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                            MainActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        // CLose keyboard if click outside the EditText
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                            MainActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

    }



    private void Login() {

        //Get input email and password
        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();

        //Check if all fields are filled
        if (sEmail.isEmpty() || sPassword.isEmpty()) {
            Toast.makeText(MainActivity.this, "All fields must be filled",
                    Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Logging..");
            pDialog.show();
            auth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                fUser = auth.getCurrentUser();
                                pDialog.dismiss();

                                if (fUser.isEmailVerified()) { // Check if Email is verified
                                    Intent i = new Intent(MainActivity.this,
                                            HomeActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    Toast.makeText(MainActivity.this,
                                            "Login Success", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {    // If Email is not verified, sign out
                                    auth.signOut();
                                    Toast.makeText(MainActivity.this,
                                            "Please verify your email", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                pDialog.dismiss();
                                Toast.makeText(MainActivity.this,
                                        "Authentication Fail!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}

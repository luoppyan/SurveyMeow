package com.meow.comp6442_groupproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meow.comp6442_groupproject.Model.User;


public class SignUpActivity extends AppCompatActivity {

    Button backBtn, signUpBtn;
    RadioButton radioSexButton;
    RadioGroup radioSexGroup;
    EditText username, email, password;
    FirebaseAuth auth;
    DatabaseReference dbReference;
    String sUsername, sEmail, sPassword, userId;
    FirebaseUser firebaseUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initUI();
    }

    private void initUI() {

        auth = FirebaseAuth.getInstance();
        username = findViewById(R.id.user_name_sign_up);
        email = findViewById(R.id.email_sign_up);
        password = findViewById(R.id.password_sign_up);
        signUpBtn= findViewById(R.id.sign_up_button);
        backBtn = findViewById(R.id.goBackBtn);
        radioSexGroup =  findViewById(R.id.radioSex);


        signUpBtn.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sUsername = username.getText().toString();
                sEmail= email.getText().toString();
                sPassword = password.getText().toString();

                if(sUsername.isEmpty() || sEmail.isEmpty() || sPassword.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "All fields must be filled",
                            Toast.LENGTH_SHORT).show();
                }else if(sPassword.length() < 6){
                    Toast.makeText(SignUpActivity.this,
                            "Password need has at least 6 characters",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    signUp();
                }
            }
        } );


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager =(InputMethodManager) getSystemService(
                            MainActivity.INPUT_METHOD_SERVICE);
                    assert inputMethodManager != null;
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });


        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(
                            MainActivity.INPUT_METHOD_SERVICE);
                    assert inputMethodManager != null;
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });


       password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(
                            MainActivity.INPUT_METHOD_SERVICE);
                    assert inputMethodManager != null;
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }



    private void signUp() {
        final ProgressDialog pDialog = new ProgressDialog(SignUpActivity.this);
        pDialog.setMessage("Signing up..");
        pDialog.show();
        auth.createUserWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            firebaseUser = auth.getCurrentUser();
                            if(firebaseUser != null) {
                                userId = firebaseUser.getUid();
                            }

                            dbReference = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userId);

                            radioSexButton = findViewById(radioSexGroup.getCheckedRadioButtonId());

                            User newUser = new User(userId,sEmail,sUsername,radioSexButton.getText().toString());

                            dbReference.setValue(newUser).addOnCompleteListener(
                                    new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        sendVerificationEmail();
                                        Intent i = new Intent(SignUpActivity.this,
                                                MainActivity.class) ;
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.putExtra("from", "signUp");
                                        pDialog.dismiss();
                                        Log.w("", "createUserWithEmail:Success",
                                                task.getException());
                                        startActivity(i);

                                        overridePendingTransition(android.R.anim.fade_in,
                                                android.R.anim.fade_out);
                                        finish();
                                    }
                                    else{
                                        pDialog.dismiss();
                                        Toast.makeText(SignUpActivity.this, "Fail to save user's info",
                                                Toast.LENGTH_SHORT).show();
                                        Log.w("", "Save user info:failure",
                                                task.getException());
                                    }
                                }
                            });
                        }else{
                            pDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Fail to create new user:\n"
                                            + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            Log.w("", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }



    private void sendVerificationEmail(){
        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,
                                    "Verification email sent to\n" + firebaseUser.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("", "sendEmailVerification", task.getException());
                            Toast.makeText(SignUpActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}

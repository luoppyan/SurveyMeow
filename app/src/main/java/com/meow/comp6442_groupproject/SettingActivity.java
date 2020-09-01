package com.meow.comp6442_groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.comp6442_groupproject.Model.User;

public class SettingActivity extends AppCompatActivity {

    ImageView avatar;
    EditText username, confirmPassword, changePassword;
    Button applyBtn, cancelBtn;
    RadioButton radioSexButton;
    RadioGroup radioSexGroup;
    FirebaseUser fUser;
    DatabaseReference currentUserRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUI();
        GetUserDataOnce();
    }



    // initialize user interface
    private void initUI() {
        //initialize widget variables
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(fUser.getUid());

        avatar = findViewById(R.id.avatars_image);
        username = findViewById(R.id.username_profile);
        applyBtn = findViewById(R.id.applyBtn_profile);
        cancelBtn = findViewById(R.id.backBtn_setting);
        radioSexGroup = findViewById(R.id.radioSex);
        changePassword = findViewById(R.id.change_password_profile);
        confirmPassword = findViewById(R.id.confirm_password_profile);
        avatar.setImageResource(R.drawable.avatar_male);

        // set up button click listener
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordsValid()) {
                    SaveChanges();
                    onBackPressed();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // set up data change listener
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                            MainActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                            MainActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        changePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                            MainActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        radioSexButton = findViewById(radioSexGroup.getCheckedRadioButtonId());
        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioSexButton = findViewById(checkedId);
                if (radioSexButton.getText().toString().equals("Male")) {
                    avatar.setImageResource(R.drawable.avatar_male);
                } else {
                    avatar.setImageResource(R.drawable.avatar_female);
                }
            }
        });
    }



    // load user data from remote set set up data change listener
    private void GetUserDataOnce() {
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getGender().equals("Male")) {
                    radioSexGroup.check(R.id.radioMale);
                } else if (user.getGender().equals("Female")) {
                    radioSexGroup.check(R.id.radioFemale);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



    // save gender and username change on remote
    // save password updated
    private void SaveChanges() {
        currentUserRef.child("gender").setValue(radioSexButton.getText().toString());
        currentUserRef.child("username").setValue(username.getText().toString());

        if (!changePassword.getText().toString().equals("")) {
            //both files are equal and not empty
            fUser.updatePassword(changePassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingActivity.this, "Passwords Update success!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingActivity.this, "Passwords Update failed!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    //Check if new password is valid.
    private boolean isPasswordsValid() {
        String p1 = changePassword.getText().toString();
        String p2 = confirmPassword.getText().toString();

        if (!p1.equals(p2)) {
            Toast.makeText(SettingActivity.this, "Passwords do not match.",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (p1.length()==0 && p2.length()>0) {
            Toast.makeText(SettingActivity.this, "Both password fields must be filled.",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (p2.length()==0 && p1.length()>0) {
            Toast.makeText(SettingActivity.this, "Both password fields must be filled.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}

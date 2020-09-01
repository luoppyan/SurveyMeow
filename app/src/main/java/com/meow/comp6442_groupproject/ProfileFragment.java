package com.meow.comp6442_groupproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.comp6442_groupproject.Model.User;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    ImageView avatar;
    TextView nickname, email;
    Button setting;
    Button logoff;
    FirebaseUser fbUser;
    DatabaseReference currUserRef;




    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        avatar = view.findViewById(R.id.avatar);
        nickname = view.findViewById(R.id.nickname);
        email = view.findViewById(R.id.email);
        setting = view.findViewById(R.id.title_setting);
        logoff = view.findViewById(R.id.logoff);

        initUI();
    }



    // initialize user interface
    public void initUI() {
        // get firebase reference
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        currUserRef = FirebaseDatabase.getInstance().getReference("Users").child(fbUser.getUid());

        // set up event listener and data change listener
        currUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    nickname.setText(user.getUsername());
                    email.setText(user.getEmail());
                    if(user.getGender().equals("Male")){
                        avatar.setImageResource(R.drawable.avatar_male);
                    }
                    else if(user.getGender().equals("Female")){
                        avatar.setImageResource(R.drawable.avatar_female);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // set up click listener
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        logoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity( new Intent(getActivity(), MainActivity.class));
            }
        });
    }
}

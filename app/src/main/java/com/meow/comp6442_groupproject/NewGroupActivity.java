package com.meow.comp6442_groupproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.meow.comp6442_groupproject.Adapter.CreateGroupAdapter;
import com.meow.comp6442_groupproject.Model.Group;
import com.meow.comp6442_groupproject.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewGroupActivity extends AppCompatActivity {
    // Firebase connection
    private DatabaseReference mDatabase;
    // UI elements
    private RecyclerView mRecyclerView;
    private Button backBtn, submitBtn, addMemberBtn, clearInputBtn;
    private EditText  et_groupName, et_groupDes, et_Email;
    // Data
    private Group group;
    // The member user invited
    private List<User> addedUsers = new ArrayList<>();
    // All user information of the system
    private List<User> users = new ArrayList<>();
    // Used to get current user's information
    private FirebaseUser fUser;
    private String groupDescription;
    // The adapter for mRecyclerView
    CreateGroupAdapter myadapter;

    private final int NAME_START_POSITION = "Group name: ".length();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        mRecyclerView=findViewById(R.id.newGroupRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myadapter = new CreateGroupAdapter(this, addedUsers);
        mRecyclerView.setAdapter(myadapter);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initUI();

    }

    /**
     * Initialize the elements and add functionality to them
     */
    private void initUI() {
        // Initialize the page elements
        addMemberBtn = findViewById(R.id.addBtn_newMember);
        backBtn = findViewById(R.id.backBtn_NewGroup);
        clearInputBtn = findViewById(R.id.removeBtn_newMember);
        submitBtn = findViewById(R.id.createSubmitBtn);
        et_Email = findViewById(R.id.textEnterEmailForNewMember_groupInfo);
        et_groupName = findViewById(R.id.ET_groupName);
        et_groupDes = findViewById(R.id.ET_groupDescription);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        group = new Group(fUser.getUid());



        myadapter.setOnItemLongClick(new CreateGroupAdapter.OnItemLongClickListener(){
            @Override
            public void OnItemLongClick(View view, final int position) {
                Log.i("CreateSurveyActivity", "Long Clicked item " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(NewGroupActivity.this);
                builder.setTitle("Delete Member")
                        .setMessage("Do you want to delete this member from the group?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(addedUsers.get(position).getUserId().equals(fUser.getUid())){
                                    Toast.makeText(NewGroupActivity.this,
                                            "Can not remove yourself",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    addedUsers.remove(position); // Remove item from the ArrayList
                                    myadapter.notifyDataSetChanged(); // Notify listView adapter to update the list
                                }
                            } })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User cancelled the dialog
                                // Nothing happens
                            } });
                builder.create().show();
            }
        });

        // Get the user list from database
        DatabaseReference userRef = mDatabase.child("Users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterate all items in Users table
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    users.add(user);

                    //Add myself into members
                    if(user.getUserId() != null && user.getUserId().equals(fUser.getUid())){
                        addedUsers.add(user);
                        myadapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("NewGroupActivity", "onCancelled");
            }
        });

        //Set up listeners
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        et_groupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        et_groupDes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        et_Email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        // Event for add email
        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Denote the email user input
                final String email = et_Email.getText().toString().trim();
                boolean noMatchedUser = true;
                for (User user : users){
                    if(user.getEmail().equalsIgnoreCase(email)){
                        if(addedUsers.contains(user))
                            continue;
                        addedUsers.add(user);
                        group.addMember(user.getUserId());
                        myadapter.notifyDataSetChanged();
                        noMatchedUser = false;
                    }
                }
                if (noMatchedUser){
                    showNoMatchDialog(email);
                } else
                    et_Email.setText("");

            }
        });

        clearInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_Email.setText("");
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(missNeededInfo()){
                    return;
                }
                DatabaseReference groupRef = mDatabase.child("Groups");

                String groupID = groupRef.push().getKey();

                group.setGroupId(groupID);
                group.setGroupDescr(et_groupDes.getText().toString());
                group.setGroupTitle(et_groupName.getText().toString());

                for (User user : addedUsers){
                    if(!group.getMembers().containsKey(user.getUserId()))
                        group.addMember(user.getUserId());

                }

                //Make sure host is in the group
                if(group.getMembers().containsKey(fUser.getUid()) == false){
                    group.addMember(fUser.getUid());
                }

                if(group.getSurveys() == null){
                    group.setSurveys(new HashMap<String, Boolean>());
                }

                groupRef.child(groupID).setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(NewGroupActivity.this,
                                    "Group Created",
                                    Toast.LENGTH_SHORT).show();
                            onBackPressed();
//                            Intent i = new Intent(NewGroupActivity.this, GroupActivity.class);
//                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
//                                    Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(i);
                        } else {
                            Toast.makeText(NewGroupActivity.this,
                                    "Createfailure: " + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private boolean missNeededInfo() {
        if (group == null)
            Toast.makeText(NewGroupActivity.this,
                    "Something Wrong",
                    Toast.LENGTH_SHORT).show();
        if (et_groupName.getText().toString() == null || et_groupName.getText().toString().trim().equals("")){
            Toast.makeText(NewGroupActivity.this,
                    "Please enter group name",
                    Toast.LENGTH_SHORT).show();
        }
        else if (addedUsers.size() == 1) {
            Toast.makeText(NewGroupActivity.this,
                    "Group needs at least two member",
                    Toast.LENGTH_SHORT).show();
        }
        else
            return false;
        return true;
    }



    private void showNoMatchDialog(String email){
        AlertDialog.Builder builder = new AlertDialog.Builder(NewGroupActivity.this);
        builder.setTitle("No matched Email found")
                .setMessage("Sorry, there is no user's email matches \""+email+"\", please check " +
                        "your input and try again.")
                .setPositiveButton("OK", null);
        builder.create().show();
    }

}

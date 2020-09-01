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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class GroupInfoActivity extends AppCompatActivity {
    // Firebase connection
    private DatabaseReference mDatabase;
    // UI elements
    private RecyclerView mRecyclerView;
    private Button backBtn,  submitBtn, addMemberBtn, clearInputBtn, deleteGroupBtn;
    private EditText  et_groupName, et_groupDes, et_Email;
    // Data
    private String groupID;
    private Group group = new Group();
    // The member user invited
    private List<User> addedUsers = new ArrayList<>();
    // All user information of the system
    private List<User> users = new ArrayList<>();
    // Used to get current user's information
    private FirebaseUser fUser;
    // The adapter for mRecyclerView
    CreateGroupAdapter myadapter;

    private final int NAME_START_POSITION = "Group name: ".length();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent=getIntent();
        groupID = intent.getStringExtra("groupID");

        initUI();
        readUsersAndGroup();



    }



    /**
     * Initialize the elements and add functionality to them
     */
    private void initUI() {
        // Initialize the page elements
        addMemberBtn = findViewById(R.id.addBtn_newMember_groupInfo);
        backBtn = findViewById(R.id.backBtn_groupInfo);
        clearInputBtn = findViewById(R.id.removeBtn_newMember_groupInfo);
        deleteGroupBtn = findViewById(R.id.deleteGroup);
        submitBtn = findViewById(R.id.createSubmitBtn_groupInfo);

        et_Email = findViewById(R.id.textEnterEmailForNewMember_groupInfo);
        et_groupName = findViewById(R.id.ET_groupName);
        et_groupDes = findViewById(R.id.ET_groupDescription);


        //Set up recycler view
        mRecyclerView=findViewById(R.id.groupInfoRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        myadapter = new CreateGroupAdapter(GroupInfoActivity.this, addedUsers);
        mRecyclerView.setAdapter(myadapter);

        //Set up adapter
        myadapter.setOnItemLongClick(new CreateGroupAdapter.OnItemLongClickListener(){
            @Override
            public void OnItemLongClick(View view, final int position) {
                Log.i("CreateSurveyActivity", "Long Clicked item " + position);
                if (addedUsers.get(position).getUserId().equals(group.getHostId()))
                    showDeleteAdminRejectedDialog();
                else
                    showDeleteMemberDialog(position);
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

        deleteGroupBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteGroupDialog();
            }
        }));

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(missNeededInfo()){
                    return;
                }
                DatabaseReference currentgroupRef = FirebaseDatabase.getInstance().getReference().
                        child("Groups").child(group.getGroupId());

                currentgroupRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Group currentGroup = mutableData.getValue(Group.class);
                        if (currentGroup  == null) {
                            return Transaction.success(mutableData);
                        }

                        for (User user : addedUsers){
                            if(!currentGroup.getMembers().keySet().contains(user.getUserId()))
                                currentGroup.addMember(user.getUserId());
                        }

                        if(et_groupName.getText().toString() != null) {
                            currentGroup.setGroupTitle(et_groupName.getText().toString());
                        }
                        currentGroup.setGroupDescr(et_groupDes.getText().toString());
                        mutableData.setValue(currentGroup);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        Toast.makeText(GroupInfoActivity.this,
                                "Group information updated",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                onBackPressed();

            }
        });
    }

    private void readUsersAndGroup(){

        DatabaseReference usersRef = mDatabase.child("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterate all items in Users table
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                }

                DatabaseReference groupRef = mDatabase.child("Groups").child(groupID);
                groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        group = dataSnapshot.getValue(Group.class);

                        et_groupName.setText(group.getGroupTitle());
                        et_groupDes.setText(group.getGroupDescr());

                        addedUsers.clear();
                        for (User user : users){
                            if (group.getMembers().containsKey(user.getUserId())){
                                addedUsers.add(user);
                                Log.d("GroupInfoActivity", "add user");
                            }
                        }

                        myadapter.notifyDataSetChanged();

                        fUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (fUser.getUid().equals(group.getHostId())){
                            deleteGroupBtn.setVisibility(View.VISIBLE);
                        }
                        Log.d("Visibility", "initUI: "+deleteGroupBtn.getVisibility());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("GroupInfoActivity", "onCancelled");
            }
        });


    }


    //Checked if all fields are filled
    private boolean missNeededInfo() {
        if (group == null)
            Toast.makeText(GroupInfoActivity.this,
                    "Can not read group information",
                    Toast.LENGTH_SHORT).show();
        if (et_groupName.getText().toString() == null || et_groupName.getText().toString().trim().equals("")){
            Toast.makeText(GroupInfoActivity.this,
                    "Please enter group name",
                    Toast.LENGTH_SHORT).show();
        }
        else if (addedUsers.size() == 1) {
            Toast.makeText(GroupInfoActivity.this,
                    "Group needs at least two member",
                    Toast.LENGTH_SHORT).show();
        }
        else
            return false;
        return true;
    }


    // Show this dialog when the input email doesn't exist in the database
    private void showNoMatchDialog(String email){
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
        builder.setTitle("No matched Email found")
                .setMessage("Sorry, there is no user's email matches \""+email+"\", please check " +
                        "your input and try again.")
                .setPositiveButton("OK", null);
        builder.create().show();
    }

    // Double check if the user would like to delete the corresponding member from the group
    private void showDeleteMemberDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
        builder.setTitle("Delete Member")
                .setMessage("Do you want to delete this member from the group?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(addedUsers.get(position).getUserId().equals(fUser.getUid())){
                            Toast.makeText(GroupInfoActivity.this,
                                    "Can not remove host",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            User tempUser = addedUsers.remove(position); // Remove item from the ArrayList
                            myadapter.notifyDataSetChanged(); // Notify listView adapter to update the list
                            //
                            group.removeMember(tempUser);
                        }

                    } })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User cancelled the dialog
                        // Nothing happens
                    } });
        builder.create().show();
    }

    private void showDeleteAdminRejectedDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
        builder.setTitle("Delete Rejected")
                .setMessage("Sorry you cannot delete the creator from the group")
                .setPositiveButton("OK", null);
        builder.create().show();
    }

    private void showDeleteGroupDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
        builder.setTitle("Delete Group")
                .setMessage("Are you sure to delete this group?\nYour operation would be irreversible")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.child("Groups").child(group.getGroupId()).removeValue();
                        onBackPressed();
                    }
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }
}

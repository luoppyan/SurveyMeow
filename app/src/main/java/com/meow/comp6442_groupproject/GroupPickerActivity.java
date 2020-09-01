package com.meow.comp6442_groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.meow.comp6442_groupproject.Adapter.GroupPickerAdapter;
import com.meow.comp6442_groupproject.Model.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroupPickerActivity extends AppCompatActivity {

    private DatabaseReference mGroupDBRef;
    private ArrayList<Group> mGroupList;
    private HashSet<String> mGroupIds;
    private String mSurveyId;
    Button backBtn, sendBtn;
    GroupPickerAdapter mAdapter;
    RecyclerView mRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_picker);
        mGroupDBRef = FirebaseDatabase.getInstance().getReference("Groups");
        mSurveyId = getIntent().getStringExtra("surveyId");
        mGroupList = new ArrayList<>();
        mGroupIds = new HashSet<>();


        readGroupsInfoOnce();

        initUI();
        setUpAdapter();
    }



    // initialize user interface
    private void initUI() {
        backBtn = findViewById(R.id.backBtn_groupPicker);
        sendBtn = findViewById(R.id.sendBtn_groupPicker);

        //set up click listener
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (final String gId: mGroupIds) {

                    mGroupDBRef.child(gId).runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            Group currGroup = mutableData.getValue(Group.class);
                            if (currGroup == null) {
                                return Transaction.success(mutableData);
                            }

                            currGroup.addSurvey(mSurveyId);
                            mutableData.setValue(currGroup);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                        }
                    });
                }

                if (!(mGroupIds.size() > 0)) {
                    Toast.makeText(GroupPickerActivity.this,
                            "Please choose your groups", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GroupPickerActivity.this,
                            "Survey has been sent", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
    }



    // set up recycle view adapter
    private void setUpAdapter() {
        mRecyclerView=findViewById(R.id.groupPickerRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GroupPickerAdapter(this, mGroupList);
        mRecyclerView.setAdapter(mAdapter);

        // set up click listener
        mAdapter.setOnItemClickListener(new GroupPickerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, boolean isChecked) {
                Log.i("CreateSurveyActivity", "Clicked item " + position);
                Group curG = mAdapter.getGroup(position);
                String curGId = curG.getGroupId();
                if (isChecked && !mGroupIds.contains(curGId)) {
                    mGroupIds.add(curGId);
                } else if (!isChecked && mGroupIds.contains(curGId)) {
                    mGroupIds.remove(curGId);
                }
            }
        });
    }



    // load group info
    public void readGroupsInfoOnce(){
        mGroupDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGroupList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    mGroupList.add(group);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("GroupActivity", "loadGroups:onCancelled", databaseError.toException());
            }
        });
    }
}

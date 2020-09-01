package com.meow.comp6442_groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.meow.comp6442_groupproject.Adapter.ShowGroupAdapter;
import com.meow.comp6442_groupproject.Adapter.ShowGroupSurveyAdapter;
import com.meow.comp6442_groupproject.Model.Group;
import com.meow.comp6442_groupproject.Model.Result;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ArrayList<Group> glist;
    FirebaseUser fUser;
    Button backBtn, createBtn;
    ShowGroupAdapter myadapter;
    RecyclerView mRecyclerView;

    // Initialize the Database object and RecyclerView on create, also the UI and load data from DB
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        fUser =  FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Groups");
        glist = new ArrayList<>();

        mRecyclerView=findViewById(R.id.groupRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        myadapter = new ShowGroupAdapter(this, glist);
        mRecyclerView.setAdapter(myadapter);
        // When click on the recyclerview element, jump to the corresponding Data
        myadapter.setOnItemClick(new ShowGroupAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Intent intent = new Intent(GroupActivity.this, GroupSurveyActivity.class);
                intent.putExtra("currentGroup", glist.get(position).getGroupId());
                startActivity(intent);
            }


        });

        // Initialize UI
        initUI();
        // Read data from DB
        readGroups();
    }


    //Initialize UI, Set up Button Listeners
    private void initUI() {
        backBtn = findViewById(R.id.backBtn_groupList);
        createBtn = findViewById(R.id.createBtn_groupList);


        //Set up listeners
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupActivity.this, NewGroupActivity.class);
                startActivity(i);
            }
        });

        // Set a filter to implement the search functionality
        EditText et = findViewById(R.id.searchBar_groupList);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
                myadapter.getFilter().filter(sequence.toString());
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    // Load data from database
    public void readGroups(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                glist.clear();
                myadapter.notifyDataSetChanged();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    if(group.getMembers().containsKey(fUser.getUid())){
                        glist.add(0, group);
                        myadapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("GroupActivity", "loadGroups:onCancelled", databaseError.toException());
            }
        });
    }



}

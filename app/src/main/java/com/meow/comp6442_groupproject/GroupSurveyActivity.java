package com.meow.comp6442_groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.meow.comp6442_groupproject.Adapter.ShowGroupSurveyAdapter;
import com.meow.comp6442_groupproject.Model.Group;
import com.meow.comp6442_groupproject.Model.Survey;
import com.meow.comp6442_groupproject.Model.User;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class GroupSurveyActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    Button backButton;
    String currentGroupID;
    TextView groupName;
    List<Survey> surveyList = new ArrayList<>();
    private DatabaseReference mDatabase;
    ShowGroupSurveyAdapter myadapter;
    private FirebaseUser fUser;

    // Initialize the UI/ Database/ RecyclerView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_survey);

        mRecyclerView=findViewById(R.id.groupSurveyRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        myadapter = new ShowGroupSurveyAdapter(this, surveyList);
        mRecyclerView.setAdapter(myadapter);
        myadapter.setOnItemClick(new ShowGroupSurveyAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                //TODO
                Survey currentSurvey = surveyList.get(position);
                Intent i = new Intent(GroupSurveyActivity.this, SummaryActivity.class);
                i.putExtra("surveyId", currentSurvey.getSurveyId());
                i.putExtra("position", position);
                fUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentSurvey.getCreaterId().equals(fUser.getUid())){
                    i.putExtra("from","mySurvey");
                }else {
                    i.putExtra("from", "home");
                }
                startActivity(i);
            }


        });



        Intent intent = getIntent();
        currentGroupID = intent.getStringExtra("currentGroup");

        initUI();
        readGroupsAndSurveys();
    }


    // Load data from the database
    public void readGroupsAndSurveys(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Group group = dataSnapshot.getValue(Group.class);

                if (group == null){
                    onBackPressed();
                }
                else{
                    groupName.setText(group.getGroupTitle()+" ("+ group.getMembers().size()+")");
                    DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Surveys");
                    groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            surveyList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Survey survey = snapshot.getValue(Survey.class);
                                if(group.getSurveys().keySet().contains(survey.getSurveyId()))
                                    surveyList.add(survey);
                            }
                            myadapter.notifyDataSetChanged();
                            Log.d("Surveycheck", "onDataChange: The surveyList is"+surveyList.size());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    // Set on the listeners for the UI
    public void initUI(){
        mDatabase = FirebaseDatabase.getInstance().getReference("Groups").child(currentGroupID);
        backButton = findViewById(R.id.backBtn_groupSurveyList);
        groupName = findViewById(R.id.text_groupName_survey);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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


    public void infoBtnClicked(View view) {
        Intent intent = new Intent(GroupSurveyActivity.this, GroupInfoActivity.class);
        intent.putExtra("groupID", currentGroupID);
        startActivity(intent);
    }
}

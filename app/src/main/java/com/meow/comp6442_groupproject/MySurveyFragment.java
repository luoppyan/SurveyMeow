package com.meow.comp6442_groupproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.meow.comp6442_groupproject.Adapter.ShowMySurveysAdapter;
import com.meow.comp6442_groupproject.Model.Survey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MySurveyFragment extends Fragment {

    private Button createBtn;
    private RecyclerView mySurveyRecyclerView;
    private ArrayList<Survey> openList;
    private ArrayList<Survey> closedList;
    private ArrayList<Survey> mSurvey;
    private FirebaseUser fUser;
    private ShowMySurveysAdapter showMySurveysAdapter;
    private DatabaseReference surveyRef;
    private SimpleDateFormat dateformat;
    private TwinklingRefreshLayout refreshLayout;



    public MySurveyFragment() {}



    static MySurveyFragment newInstance() {
        return new MySurveyFragment();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_survey, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initUI(view);
        refreshLayout.startRefresh();
    }



    private void initUI(View view){

        //Initialize Variables
        createBtn = view.findViewById(R.id.createBtn);
        mySurveyRecyclerView = view.findViewById(R.id.recyclerView_mySurvey);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        setUpRefreshListener();

        dateformat = new SimpleDateFormat(); // Date format
        dateformat.applyPattern("yyyy/MM/dd HH:mm");
        openList = new ArrayList<Survey>();
        closedList = new ArrayList<Survey>();
        mSurvey = new ArrayList<Survey>();
        setUpAdapter();


        //get user info
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        surveyRef = FirebaseDatabase.getInstance().getReference("Surveys");


        //set up recycler view
        LinearLayoutManager lManager = new LinearLayoutManager(getActivity());
        lManager.setStackFromEnd(false);
        mySurveyRecyclerView.setHasFixedSize(true);
        mySurveyRecyclerView.setLayoutManager(lManager);
        mySurveyRecyclerView.setAdapter(showMySurveysAdapter);


        //Set up listeners
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CreateSurveyActivity.class);
                startActivity(i);
            }
        });
    }


    //Read survey
    private void readMySurveyOnce(){
        surveyRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userId = fUser.getUid();
                mSurvey.clear();
                openList.clear();
                closedList.clear();

                int i = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Survey survey = snapshot.getValue(Survey.class);
                    if(survey.getCreaterId().equals(userId)){

                        Date currentDate = new Date();

                        //If pass due date, update survey status to "close"
                        try {
                            if(currentDate.compareTo(dateformat.parse(survey.getEnddate())) > 0){
                                survey.setStatus("close");
                                surveyRef.child(survey.getSurveyId()).child("status").setValue("close");
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(survey.getStatus().equals("open")){
                            openList.add(0,survey);
                        }
                        else if(survey.getStatus().equals("close")){
                            closedList.add(0,survey);

                        }
                    }
                }

                mSurvey.addAll(openList);
                mSurvey.addAll(closedList);
                showMySurveysAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Survey failed, log a message
                Log.w("MySurveyActivity", "loadSurvey:onCancelled", databaseError.toException());

            }
        });
    }



    private void setUpAdapter(){
        showMySurveysAdapter = new ShowMySurveysAdapter(getActivity(), mSurvey);

        showMySurveysAdapter.setOnItemClickListener(new ShowMySurveysAdapter.OnItemClickListener() {

            @Override
            //Click question will lead to SummaryActivity
            public void onItemClick(View view, int position) {
                Log.i("MySurveyFragment", "Clicked item " + position);
                Survey currentSurvey = showMySurveysAdapter.getItem(position);
                Intent i = new Intent(getActivity(), SummaryActivity.class);
                if(i !=null){
                    i.putExtra("surveyId", currentSurvey.getSurveyId());
                    i.putExtra("from","mySurvey");
                    i.putExtra("position", position);

                    // brings up the second activity
                    startActivity(i);
                }
            }


            @Override
            public void onItemLongClick(View view, int position) {
                //Nothing
            }
        });
    }



    //Set up pull down refresh
    private void setUpRefreshListener(){
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter(){
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        readMySurveyOnce();
                        refreshLayout.finishRefreshing();
                    }
                },2000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        readMySurveyOnce();
                        refreshLayout.finishLoadmore();
                    }
                },2000);
            }
        });

        refreshLayout.setMaxHeadHeight(80);
        refreshLayout.setHeaderHeight(60);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setHeaderView(new PullDownHeader(getActivity()));
    }



}

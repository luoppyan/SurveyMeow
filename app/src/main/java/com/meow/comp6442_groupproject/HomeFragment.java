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
import com.meow.comp6442_groupproject.Adapter.ShowAllSurveysAdapter;
import com.meow.comp6442_groupproject.Model.Survey;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private ArrayList<Survey> mSurvey;
    private ShowAllSurveysAdapter showAllSurveysAdapter;
    private RecyclerView allSurveyRecyclerView;
    private Button groupBtn;
    private FirebaseUser fUser;
    private DatabaseReference surveyRef;
    private SimpleDateFormat dateformat;
    private TwinklingRefreshLayout refreshLayout;


    private static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initUI(view);
        refreshLayout.startRefresh();
    }



    private void initUI(View view){

        //Initialize Variables
        groupBtn = view.findViewById(R.id.groupBtn);
        allSurveyRecyclerView = view.findViewById(R.id.recyclerView_home);
        refreshLayout = view.findViewById(R.id.refreshLayout_home);
        setUpRefreshListener();

        dateformat = new SimpleDateFormat(); // Date format
        dateformat.applyPattern("yyyy/MM/dd HH:mm");
        mSurvey = new ArrayList<Survey>();
        setUpAdapter();


        //get user info
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        surveyRef = FirebaseDatabase.getInstance().getReference("Surveys");


        //set up recycler view
        LinearLayoutManager lManager = new LinearLayoutManager(getActivity());
        lManager.setStackFromEnd(false);
        allSurveyRecyclerView.setHasFixedSize(true);
        allSurveyRecyclerView.setLayoutManager(lManager);
        allSurveyRecyclerView.setAdapter(showAllSurveysAdapter);


        //Set up listeners
        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), GroupActivity.class);
                startActivity(i);
            }
        });


    }


    //Read Survey
    private void readHomeSurveyOnce(){
        surveyRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userId = fUser.getUid();
                mSurvey.clear();

                int i = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Survey survey = snapshot.getValue(Survey.class);
                    if(survey.getStatus().equals("open") && survey.getType().equals("Public")){

                        Date currentDate = new Date();

                        //If pass due date, update survey status to "close"
                        try {
                            if(currentDate.compareTo(dateformat.parse(survey.getEnddate())) > 0){
                                survey.setStatus("close");
                                surveyRef.child(survey.getSurveyId()).child("status").setValue("close");
                            }else{
                                mSurvey.add(0, survey);
                                showAllSurveysAdapter.notifyDataSetChanged();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Survey failed, log a message
                Log.w("MySurveyActivity", "loadSurvey:onCancelled", databaseError.toException());

            }
        });
    }



    private void setUpAdapter(){
        showAllSurveysAdapter = new ShowAllSurveysAdapter(getActivity(), mSurvey);

        showAllSurveysAdapter.setOnItemClickListener(new ShowAllSurveysAdapter.OnItemClickListener() {

            @Override
            //Click question will lead to SummaryActivity
            public void onItemClick(View view, int position) {
                Log.i("HomeFragment", "Clicked item " + position);
                Survey currentSurvey = showAllSurveysAdapter.getItem(position);
                Intent i = new Intent(getActivity(), SummaryActivity.class);
                i.putExtra("surveyId", currentSurvey.getSurveyId());
                i.putExtra("position", position);
                if(currentSurvey.getCreaterId().equals(fUser.getUid())){
                    i.putExtra("from","mySurvey");
                }else {
                    i.putExtra("from", "home");
                }

                // brings up the second activity
                startActivity(i);
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
                        readHomeSurveyOnce();
                        refreshLayout.finishRefreshing();
                    }
                },2000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        readHomeSurveyOnce();
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

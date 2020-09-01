package com.meow.comp6442_groupproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.meow.comp6442_groupproject.Adapter.FillSurveyAdapter;
import com.meow.comp6442_groupproject.Model.Option;
import com.meow.comp6442_groupproject.Model.Question;
import com.meow.comp6442_groupproject.Model.Result;
import com.meow.comp6442_groupproject.Model.Survey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FillSurveyActivity extends AppCompatActivity {

    Survey copySurvey;
    ArrayList<Question> mQuestion;
    FillSurveyAdapter fillSurveyAdapter;
    RecyclerView fillSurveyRecyclerView;
    TextView surveyTitle;
    Button backBtn, submitBtn;
    String surveyId;
    FirebaseUser fUser;
    DatabaseReference surveyRef, resultRef;
    SimpleDateFormat dateformat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_survey);

        initUI();
        readSurveyOnce();
    }


    //Initialize UI, Set up Button Listeners
    private void initUI() {

        //Initialize Variables
        surveyTitle = findViewById(R.id.surveyTitle_fillSurvey);
        backBtn = findViewById(R.id.backBtn_fillSurvey);
        submitBtn = findViewById(R.id.btnSubmit);


        if (getIntent() != null) {
            if (getIntent().getStringExtra("surveyId") != null) {
                surveyId = getIntent().getStringExtra("surveyId");
                Log.w("FillSurveyActivity", surveyId);
            }
        }
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        surveyRef = FirebaseDatabase.getInstance().getReference("Surveys").child(surveyId);
        dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm"); // Date format
        mQuestion = new ArrayList<Question>();
        fillSurveyAdapter = new FillSurveyAdapter(FillSurveyActivity.this, mQuestion);


        //Set up listeners
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitResult();
            }
        });


        //set up recycler view
        LinearLayoutManager lManager = new LinearLayoutManager(this);
        lManager.setStackFromEnd(true);
        fillSurveyRecyclerView = findViewById(R.id.recyclerView_fillSurvey);
        fillSurveyRecyclerView.setHasFixedSize(false);
        fillSurveyRecyclerView.setNestedScrollingEnabled(false);
        fillSurveyRecyclerView.setLayoutManager(lManager);
        fillSurveyRecyclerView.setAdapter(fillSurveyAdapter);

    }



    private void readSurveyOnce() {
        surveyRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                copySurvey = dataSnapshot.getValue(Survey.class);

                //If pass due date, update survey status to "close", and return to HomeActivity
                Date currentDate = new Date();
                try {
                    if (currentDate.compareTo(dateformat.parse(copySurvey.getEnddate())) > 0) {
                        copySurvey.setStatus("close");
                        surveyRef.child("status").setValue("close");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(copySurvey != null) {
                    surveyTitle.setText(copySurvey.getTitle());
                    mQuestion.clear();
                    mQuestion.addAll(copySurvey.getQuestions());
                    fillSurveyAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Survey failed, log a message
                Log.w("FillSurveyActivity", "loadSurvey:onCancelled", databaseError.toException());

            }
        });
    }



    private void submitResult() {

        surveyRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Survey checkSurvey = dataSnapshot.getValue(Survey.class);

                if (checkSurvey == null) {  //Survey has been deleted
                    Toast.makeText(FillSurveyActivity.this,
                            "Survey has been deleted", Toast.LENGTH_SHORT).show();
                    Thread thread = new Thread(){
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000); // Wait for the toast message disappear
                                startActivity(new Intent(FillSurveyActivity.this, HomeActivity.class));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    thread.start();
                }
                else {   //Survey not null

                    //If pass due date, update survey status to "close"
                    Date currentDate = new Date();
                    try {
                        if (currentDate.compareTo(dateformat.parse(checkSurvey.getEnddate())) > 0) {
                            checkSurvey.setStatus("close");
                            surveyRef.child("status").setValue("close");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //Check if survey is closed
                    if (checkSurvey.getStatus().equals("close")) {
                        Toast.makeText(FillSurveyActivity.this, "Sorry, this survey has been closed",
                                Toast.LENGTH_SHORT).show();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000); // Wait for the toast message disappear
                                    startActivity(new Intent(FillSurveyActivity.this, HomeActivity.class));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        thread.start();
                    }
                    else {  //If Survey is still open
                        ArrayList<Question> questionList = fillSurveyAdapter.getmQuestion();
                        resultRef = FirebaseDatabase.getInstance().getReference().child("Results");

                        //If not all questions been answered
                        for (Question q : questionList) {
                            if (q.getStatus().equals("N")) {
                                Toast.makeText(FillSurveyActivity.this, "All questions have to be answered",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        //Good to go, update results
                        ProgressDialog pDialog = new ProgressDialog(FillSurveyActivity.this);
                        pDialog.setMessage("Submitting...");
                        pDialog.show();
                        updateResult(questionList);
                        updateSurveyCountAndUserId();
                        pDialog.dismiss();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Survey failed, log a message
                Log.w("FillSurveyActivity", "loadSurvey:onCancelled", databaseError.toException());
            }
        });
    }



    private void updateResult(ArrayList<Question> questionList){
        for(int i = 0; i < questionList.size(); i++){
            for(final Option o: questionList.get(i).getOptions()){

                //If option is chose, update the count of that option
                if(o.getStatus().equals("Y")){
                    String resultId = questionList.get(i).getResultId();
                    final int finalI = i;
                    resultRef.child(resultId).runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            Result result = mutableData.getValue(Result.class);

                            if (result == null) {
                                return Transaction.success(mutableData);
                            }

                            if(result.getSurveyId().equals(surveyId) &&
                                    result.getQuestionId().equals(String.valueOf(finalI))){

                                result.addKeyValue(o.getContent());
                                mutableData.setValue(result);
                                return Transaction.success(mutableData);
                            }

                            return null;
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                            // Transaction completed
                            Log.d("FillSurveyActivity", "resultTransaction:onComplete:" + databaseError);
                        }
                    });
                }
            }
        }
    }



    private void updateSurveyCountAndUserId() {
        surveyRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Survey survey = mutableData.getValue(Survey.class);
                if (survey == null) {
                    return Transaction.success(mutableData);
                }
                survey.addCount();
                if(survey.getUserList() == null){
                    survey.setUserList(new HashMap<String, Boolean>());
                }
                survey.addUser(fUser.getUid());
                mutableData.setValue(survey);
                Log.d("FillSurveyActivity", String.valueOf(survey.getCount()));
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                // Transaction completed
                Intent intent = new Intent(FillSurveyActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("from", "fillSurvey");
                startActivity(intent);
                Log.d("FillSurveyActivity", "surveyCountTransaction:onComplete:" + databaseError);
            }
        });

    }



}

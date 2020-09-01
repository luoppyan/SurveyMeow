package com.meow.comp6442_groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.comp6442_groupproject.Model.Survey;
import com.meow.comp6442_groupproject.Utils.DateUtil;
import java.text.ParseException;
import java.util.Date;



public class SummaryActivity extends AppCompatActivity {

    private TextView txvSurveyName, txvTotalResp, txvStatus, txvCreated, txvClosed, txvQcount;
    private Button btnFillin, btnDel, btnBack;
    private LinearLayout lotSend, lotAnalyze;
    private Switch swiStatus;
    private FirebaseUser fUser;
    private DatabaseReference summaryRef;
    private DatabaseReference resultRef;
    private String surveyId;
    private Boolean isMySurvey;
    private Survey currentSurvey;
    private ValueEventListener listener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        initUI();
        loadSurveySummary();
    }



    // initialize user interface
    private void initUI() {
        //initialize widget variables
        txvSurveyName = findViewById(R.id.survay_name);
        txvTotalResp = findViewById(R.id.total_resps);
        txvStatus = findViewById(R.id.txt_survey_status);
        txvCreated = findViewById(R.id.txt_created);
        txvClosed = findViewById(R.id.txt_closed);
        txvQcount = findViewById(R.id.question_count);
        lotSend = findViewById(R.id.layout_send);
        lotAnalyze = findViewById(R.id.layout_analyse);
        btnFillin = findViewById(R.id.btn_fillin);
        btnDel = findViewById(R.id.btn_more);
        btnBack = findViewById(R.id.backBtn_summary);
        swiStatus = findViewById(R.id.status_switch);

        if (getIntent() != null) {
            surveyId = getIntent().getStringExtra("surveyId");
            isMySurvey = getIntent().getStringExtra("from").equals("mySurvey");
        }

        // set up initial value
        btnDel.setVisibility(isMySurvey ? View.VISIBLE : View.GONE);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        summaryRef = FirebaseDatabase.getInstance().getReference("Surveys").child(surveyId);
        resultRef = FirebaseDatabase.getInstance().getReference("Results");

        // set up button click listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    summaryRef.removeEventListener(listener);
                onBackPressed();
            }
        });

        lotSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this, GroupPickerActivity.class);
                intent.putExtra("surveyId", surveyId);
                startActivity(intent);
            }
        });

        lotAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this, AnalyzeActivity.class);
                intent.putExtra("surveyId", surveyId);
                startActivity(intent);
            }
        });

        btnFillin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSurveyStatus()) {
                    Intent intent = new Intent(SummaryActivity.this, FillSurveyActivity.class);
                    intent.putExtra("surveyId", surveyId);
                    if(listener != null)
                        summaryRef.removeEventListener(listener);
                    startActivity(intent);
                }
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopWindow(v);
            }

        });

        // set up checked change listener
        swiStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // hidden
                if (currentSurvey != null) {
                    Date currentDate = new Date();
                    try {
                        Date endDate = DateUtil.dateToStamp(currentSurvey.getEnddate());
                        if (currentDate.compareTo(endDate) > 0) {
                            Toast.makeText(SummaryActivity.this,
                                    "Survey cannot open due to expired", Toast.LENGTH_SHORT).show();
                            swiStatus.setChecked(!b);
                            return;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (b) {
                        currentSurvey.setStatus("open");
                        summaryRef.child("status").setValue("open");
                    } else {
                        currentSurvey.setStatus("close");
                        summaryRef.child("status").setValue("close");
                    }
                }
            }
        });
    }



    // loading survey summary and set up data change listener
    private void loadSurveySummary() {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentSurvey = dataSnapshot.getValue(Survey.class);

                //Return to HomeActivity when survey is deleted
                if (currentSurvey == null) {
                    Toast.makeText(SummaryActivity.this,
                            "Survey has been deleted", Toast.LENGTH_SHORT).show();
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000); // Wait for the toast message disappear
                                Intent i = new Intent(SummaryActivity.this,
                                        HomeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                    return;
                }

                updateDisplay(currentSurvey);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        summaryRef.addValueEventListener(listener);
    }



    // check current survey status for further operation
    private boolean checkSurveyStatus() {
        if (currentSurvey == null) {
            Toast.makeText(SummaryActivity.this,
                    "Survey has been deleted", Toast.LENGTH_SHORT).show();
            return false;
        } else {   //Survey not null
            //If pass due date, update survey status to "close"
            Date currentDate = new Date();
            try {
                Date endDate = DateUtil.dateToStamp(currentSurvey.getEnddate());
                if (currentDate.compareTo(endDate) > 0) {
                    currentSurvey.setStatus("close");
                    summaryRef.child("status").setValue("close");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (currentSurvey.getStatus().equals("close")) {
                Toast.makeText(SummaryActivity.this,
                        "Survey has been closed", Toast.LENGTH_SHORT).show();
                return false;
            } else {  //Survey not closed
                if (currentSurvey.getUserList() != null && currentSurvey.getUserList().containsKey(fUser.getUid())) {
                    Toast.makeText(SummaryActivity.this,
                            "You have already filled out this survey", Toast.LENGTH_SHORT).show();
                    return false;
                } else {// All good to go
                    return true;
                }
            }
        }
    }



    // update current view display based on the updated survey
    private void updateDisplay(Survey survey) {
        txvSurveyName.setText(survey.getTitle());
        txvTotalResp.setText(String.valueOf(survey.getCount()));
        txvStatus.setText(survey.getStatus().equals("close") ? "CLOSE" : "OPEN");
        txvStatus.setTextColor(survey.getStatus().equals("close") ?
                Color.parseColor("#E91E2C") : Color.parseColor("#0AC1B0"));
        txvCreated.setText(survey.getCreatedate());
        txvClosed.setText(survey.getEnddate());
        txvQcount.setText(String.valueOf(survey.getQuestions().size()));
        swiStatus.setChecked(survey.getStatus().equals("open"));
        swiStatus.setVisibility(!isMySurvey ? View.INVISIBLE : View.VISIBLE);
        btnFillin.setVisibility(survey.getStatus().equals("close") ? View.INVISIBLE : View.VISIBLE);
    }



    // pop up window to back up remove funtion
    private void initPopWindow(View v) {
        View view = LayoutInflater.from(SummaryActivity.this).inflate(R.layout.popup_menu,
                null, false);
        Button btn_remove = view.findViewById(R.id.btn_remove_survey);

        final PopupWindow popWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setAnimationStyle(R.anim.popup);

        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popWindow.showAsDropDown(v, 50, 0);

        // set up remove survey listener
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 删除 对应 results
                summaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            Iterable<DataSnapshot> qs = dataSnapshot.child("questions").getChildren();

                            for (DataSnapshot ques : qs) {
                                String resultId = ques.child("resultId").getValue().toString();
                                resultRef.child(resultId).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // delete remove survey data
                summaryRef.removeValue();

                // jump
                popWindow.dismiss();
                Intent intent = new Intent(SummaryActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}

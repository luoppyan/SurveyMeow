package com.meow.comp6442_groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.bigkoo.pickerview.builder.TimePickerBuilder;

import com.bigkoo.pickerview.listener.OnTimeSelectListener;

import com.bigkoo.pickerview.view.TimePickerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meow.comp6442_groupproject.Model.Question;
import com.meow.comp6442_groupproject.Model.Option;
import com.meow.comp6442_groupproject.Model.Result;
import com.meow.comp6442_groupproject.Model.Survey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class BeforePostActivity extends AppCompatActivity {

    Button backBtn, postBtn;
    RadioButton radioTypeBtn;
    RadioGroup radioTypeGroup;
    TextView tvDate;
    SimpleDateFormat dateformat;
    FirebaseUser fUser;
    ArrayList<Question> questionList;
    String surveyId;
    DatabaseReference ref;
    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_post);

        initUI();
    }



    private void initUI() {

        //Initialize variables
        backBtn = findViewById(R.id.backBtn_beforePost);
        postBtn = findViewById(R.id.postBtn_beforePost);
        tvDate = findViewById(R.id.datePicker);
        radioTypeGroup = findViewById(R.id.radioSurveyType);


        dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm"); // Date format
        dateformat.applyPattern("yyyy/MM/dd HH:mm");
        Date currentDatePlusADay = new Date(); // Acquire current date and time
        Calendar c = Calendar.getInstance();
        c.setTime(currentDatePlusADay);
        c.add(Calendar.DATE, 1);  // Set date to 24hours after now
        currentDatePlusADay = c.getTime();
        String strDate = dateformat.format(currentDatePlusADay); // Convert date to string


        //Set TextView to current date and time
        tvDate.setText(strDate);

        //Set up Listeners
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    postSurvey();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    chooseDate();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }



    private void chooseDate() throws ParseException {

        Date currentDate = new Date();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.setTime(currentDate);
        endDate.set(2040,1,1);


        TimePickerView pvTime = new TimePickerBuilder(BeforePostActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tvDate.setText(dateformat.format(date));
            }
        })
                .setRangDate(startDate, endDate)
                .setTitleText("Choose a date")
                .setType(new boolean[]{true, true, true, true, true, false})
                .setLabel("","","","Hour","Min","").build();


        Calendar choseDate = Calendar.getInstance();
        String dueDate = tvDate.getText().toString();
        Date selectedDate = dateformat.parse(dueDate);
        choseDate.setTime(selectedDate);

        pvTime.setDate(choseDate);
        pvTime.show();
    }



    private void postSurvey() throws ParseException {
        Survey postSurvey = createPostSurvey();
        Date currentDate = new Date();
        if(dateformat.parse(postSurvey.getEnddate()).compareTo(currentDate) < 0){
            Toast.makeText(BeforePostActivity.this,
                    "Survey's end date cannot be earlier than current date", Toast.LENGTH_SHORT).show();

        }else {

            pDialog = new ProgressDialog(BeforePostActivity.this);
            pDialog.setMessage("Posting...");
            pDialog.show();


            ref = FirebaseDatabase.getInstance().getReference();
            surveyId = ref.child("Surveys").push().getKey();
            postSurvey.setSurveyId(surveyId);
            ref.child("Surveys").child(surveyId).setValue(postSurvey).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                postResults();

                            } else {
                                Log.w("", "createSurvey:failure",
                                        task.getException());
                                Toast.makeText(BeforePostActivity.this,
                                        (CharSequence) task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



    private Survey createPostSurvey(){
        radioTypeBtn = findViewById(radioTypeGroup.getCheckedRadioButtonId());
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        String type = radioTypeBtn.getText().toString();
        Date currentDate = new Date();
        String createrId = fUser.getUid();
        String surveyTitle = getIntent().getStringExtra("surveyTitle");
        String createDate = dateformat.format(currentDate);
        questionList = new ArrayList<Question>(
                Objects.requireNonNull(getIntent()
                        .<Question>getParcelableArrayListExtra("questionList")));

        Survey postSurvey = new Survey(type, createrId, surveyTitle,
                createDate, tvDate.getText().toString(), questionList);
        return postSurvey;

    }



    private void postResults(){
        DatabaseReference resultsRef = ref.child("Results");
        DatabaseReference questionsRef = ref.child("Surveys").child(surveyId).child("questions");

        HashMap<String, Object> updateQuestions = new HashMap<>();
        HashMap<String, Object> updateResults = new HashMap<>();

        for(int i = 0; i < questionList.size(); i++){
            HashMap<String, Integer> resList= new HashMap<>();
            for(Option o: questionList.get(i).getOptions()){
                resList.put(o.getContent(),0);
            }
            Result result = new Result(surveyId, String.valueOf(i), resList, questionList.get(i).getTitle());

            String resultId = resultsRef.push().getKey();
            String finalI = String.valueOf(i);
            String path = finalI + "/resultId";

            updateResults.put(resultId, result);
            updateQuestions.put(path, resultId);

        }

        resultsRef.updateChildren(updateResults);
        questionsRef.updateChildren(updateQuestions).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.w("", "updateQuestion:Success");
                    Intent i = new Intent(BeforePostActivity.this,
                            HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("from", "BeforePostActivity");
                    pDialog.dismiss();
                    startActivity(i);

                    overridePendingTransition(android.R.anim.fade_in,
                            android.R.anim.fade_out);

                } else {
                    Log.w("", "updateQuestion:failure",
                            task.getException());
                }
            }
        });
    }



}

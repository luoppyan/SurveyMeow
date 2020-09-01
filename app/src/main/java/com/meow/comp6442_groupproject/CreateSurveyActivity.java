package com.meow.comp6442_groupproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meow.comp6442_groupproject.Adapter.ShowQuestionsAdapter;
import com.meow.comp6442_groupproject.Model.Question;

import java.util.ArrayList;

public class CreateSurveyActivity extends AppCompatActivity {

    RecyclerView questionRecyclerView;
    Button newQuestionBtn, backBtn, nextBtn;
    ShowQuestionsAdapter showQuestionsAdapter;
    ArrayList<Question> mQuestion;
    EditText surveyTitle;

    public final int EDIT_QUESTION_REQUEST_CODE = 647;
    public final int ADD_QUESTION_CODE = 648;
    public final int EDIT_SURVEY_TITLE_CODE = 649;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_survey);

        initUI();
    }



    //Initialize UI, Set up Button Listeners
    private void initUI() {

        //Initialize Variables
        surveyTitle = findViewById(R.id.surveyTitle);
        backBtn = findViewById(R.id.backBtn_createSurvey);
        nextBtn = findViewById(R.id.nextBtn);
        newQuestionBtn = findViewById(R.id.editCreateBtn);
        mQuestion = new ArrayList<Question>();
        setUpAdapter();


        //Set up listeners
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Create new question
        newQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateSurveyActivity.this, EditQuestionActivity.class);
                startActivityForResult(intent, ADD_QUESTION_CODE);

            }
        });

        //Edit Survey's title
        surveyTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(
                            EditQuestionActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentTitle = surveyTitle.getText().toString();
                if(currentTitle == null || currentTitle.isEmpty()){
                    Toast.makeText(CreateSurveyActivity.this,
                            "Please enter survey title", Toast.LENGTH_SHORT).show();
                }
                else if(mQuestion.size() == 0){
                    Toast.makeText(CreateSurveyActivity.this,
                            "At least need one question", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(CreateSurveyActivity.this, BeforePostActivity.class);
                    intent.putExtra("surveyTitle", currentTitle);
                    intent.putExtra("questionList", mQuestion);
                    startActivity(intent);
                }
            }
        });


        //set up recycler view
        LinearLayoutManager lManager = new LinearLayoutManager(this);
        lManager.setStackFromEnd(true);
        questionRecyclerView = findViewById(R.id.recyclerView_survey);
        questionRecyclerView.setHasFixedSize(false);
        questionRecyclerView.setNestedScrollingEnabled(false);
        questionRecyclerView.setLayoutManager(lManager);
        questionRecyclerView.setAdapter(showQuestionsAdapter);

    }


    //Handle different Activity results
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //Deal with the edit survey title result
        if (requestCode == EDIT_SURVEY_TITLE_CODE) {
            if (resultCode == RESULT_OK) {
                String newTitle = data.getExtras().getString("newTitle");
                surveyTitle.setText(newTitle);
            }
        }

        //Deal with the add question result
        else if (requestCode == ADD_QUESTION_CODE){
            if (resultCode == RESULT_OK) {
                Log.w("CreateSurveyActivity", "Add new question");
                Question newQuestion = data.getParcelableExtra("newQuestion");;
                mQuestion.add(newQuestion);
                showQuestionsAdapter.notifyDataSetChanged();
            }

        }

        //Deal with the edit question result
        else if  (requestCode == EDIT_QUESTION_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                Log.w("CreateSurveyActivity", "Edit question");
                Question newQuestion = data.getParcelableExtra("newQuestion");
                int position = data.getIntExtra("position", -1);
                mQuestion.remove(position);
                mQuestion.add(position,newQuestion);
                showQuestionsAdapter.notifyDataSetChanged();
            }

        }
    }


    private void setUpAdapter(){
        showQuestionsAdapter = new ShowQuestionsAdapter(CreateSurveyActivity.this, mQuestion);


        showQuestionsAdapter.setOnItemClickListener(new ShowQuestionsAdapter.OnItemClickListener() {

            @Override
            //Click question will lead to EditQuestionActivity
            public void onItemClick(View view, int position) {
                Log.i("CreateSurveyActivity", "Clicked item " + position);
                Question currentQuestion = showQuestionsAdapter.getItem(position);
                Intent i = new Intent(CreateSurveyActivity.this, EditQuestionActivity.class);
                if(i !=null){
                    i.putExtra("question", currentQuestion);
                    i.putExtra("position", position);

                    // brings up the second activity
                    startActivityForResult(i, EDIT_QUESTION_REQUEST_CODE);

                }
            }

            @Override
            //Long click question can delete question
            public void onItemLongClick(View view, final int position) {
                Log.i("CreateSurveyActivity", "Long Clicked item " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateSurveyActivity.this);
                builder.setTitle("Delete Question")
                        .setMessage("Do you want to delete this question?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mQuestion.remove(position); // Remove item from the ArrayList
                                showQuestionsAdapter.notifyDataSetChanged(); // Notify listView adapter to update the list

                            } })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User cancelled the dialog
                                // Nothing happens
                            } });
                builder.create().show();
            }
        });
    }
}

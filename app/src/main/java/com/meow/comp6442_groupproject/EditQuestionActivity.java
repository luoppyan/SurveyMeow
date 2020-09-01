package com.meow.comp6442_groupproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.meow.comp6442_groupproject.Adapter.ShowOptionsAdapter;
import com.meow.comp6442_groupproject.Model.Option;
import com.meow.comp6442_groupproject.Model.Question;

import java.util.ArrayList;

public class EditQuestionActivity extends AppCompatActivity {


    //Define Variables
    Button backBtn, saveBtn;
    EditText etQuestionText;
    RecyclerView optionRecyclerView;
    RadioButton radioTypeButton;
    RadioGroup radioTypeGroup;
    ArrayList<Option> mOption;
    ShowOptionsAdapter editSurveyAdapter;
    String type;
    Question currentQuestion;
    int position = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        initUI();
    }

    private void initUI() {

        //Initialize Variables
        backBtn = findViewById(R.id.backBtn_editQuestion);
        saveBtn = findViewById(R.id.saveBtn_editQuestion);
        radioTypeGroup = findViewById(R.id.radioType);
        optionRecyclerView = findViewById(R.id.recyclerView_editQuestion);
        etQuestionText = findViewById(R.id.ET_question);
        mOption = new ArrayList<Option>();
        mOption.add(new Option(""));
        editSurveyAdapter = new ShowOptionsAdapter(EditQuestionActivity.this, mOption);


        //Set up options recycler view
        LinearLayoutManager lManager = new LinearLayoutManager(this);
        lManager.setStackFromEnd(true);
        optionRecyclerView.setHasFixedSize(false);
        optionRecyclerView.setNestedScrollingEnabled(false);
        optionRecyclerView.setLayoutManager(lManager);
        optionRecyclerView.setAdapter(editSurveyAdapter);


        readQuestionFromIntent();


        //Set up listeners
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        radioTypeButton = findViewById(radioTypeGroup .getCheckedRadioButtonId());
        radioTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioTypeButton = findViewById(i);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuestion();
            }
        });


        etQuestionText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(
                            EditQuestionActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

    }

    
    private void saveQuestion() {
        //Get Question title
        String title = etQuestionText.getText().toString();

        //Get Question Type
        if (radioTypeButton.getText().toString().equals("Single-select")) {
            type = "MC";
        } else if (radioTypeButton.getText().toString().equals("Multi-select")) {
            type = "CB";
        }

        //Get Options
        ArrayList<Option> returnOptions = new ArrayList<Option>();
        ArrayList<Option> trimOptions = new ArrayList<Option>();

        for(Option op : editSurveyAdapter.getOptions()){
            if(op.getContent()!=null && !op.getContent().equals("")){
                trimOptions.add(op);
            }
            if(op.getContent().contains("/") || op.getContent().contains(".") ||
                    op.getContent().contains("#") || op.getContent().contains("$") ||
                    op.getContent().contains("[") || op.getContent().contains("]")){
                Toast.makeText(this,
                        "Option must not contain '/', '.', '#', '$', '[', or ']'", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        returnOptions = trimOptions;

        //Check is all field not empty
        if (title.equals("") || title == null) {
            Toast.makeText(EditQuestionActivity.this,
                    "Question Text can not be empty!", Toast.LENGTH_SHORT).show();

        } else if (returnOptions.size() == 0) {
            Toast.makeText(EditQuestionActivity.this,
                    "At least need one choice!", Toast.LENGTH_SHORT).show();
        } else {
            Intent data = new Intent();
            if (currentQuestion == null) {
                currentQuestion = new Question(title, type, returnOptions);
            } else {
                currentQuestion.setTitle(title);
                currentQuestion.setType(type);
                currentQuestion.setOptions(returnOptions);
            }
            Log.w("EditQuestionActivity", currentQuestion.getType());
            data.putExtra("newQuestion", currentQuestion);
            data.putExtra("position", position);
            // Activity finished ok, return the data
            setResult(RESULT_OK, data); // set result code and bundle data for response
            finish(); // closes the activity, pass data to parent
        }
    }


    private void readQuestionFromIntent(){
        //Get the data from the CreateSurveyActivity
        if(getIntent().getParcelableExtra("question") != null) {
            currentQuestion = getIntent().getParcelableExtra("question");
            type = currentQuestion.getType();

            //Set type
            if(type.equals("MC")){
                radioTypeGroup.check(R.id.radioMC);
                Log.w("EditQuestionActivity", "Current question type is MC");
            }
            else if(type.equals("CB")){
                radioTypeGroup.check(R.id.radioCB);
                Log.w("EditQuestionActivity", "Current question type is CB");
            }

            position = getIntent().getIntExtra("position", -1);

            //Set title
            etQuestionText.setText(currentQuestion.getTitle());

            //Set options
            if(currentQuestion.getOptions() != null) {
                Log.w("EditQuestionActivity", "Current question have option");
                mOption.clear();
                mOption.addAll(currentQuestion.getOptions());
                editSurveyAdapter.notifyDataSetChanged();
            }
        }
    }
}

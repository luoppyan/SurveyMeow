package com.meow.comp6442_groupproject;

import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;


import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.meow.comp6442_groupproject.Adapter.ShowAnalyzeAdapter;
import com.meow.comp6442_groupproject.Model.Result;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;


public class AnalyzeActivity extends AppCompatActivity {

    // Define objectvie
    private RecyclerView recyclerView;
    private ShowAnalyzeAdapter myAdapter;
    private Button btnBack;
    private String surveyId;
    private DatabaseReference mDatabase;
    private Intent intent;
    private ArrayList<Result> rlist;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set up layout, result list and database etc
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        rlist = new ArrayList<Result>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Results");
        intent = getIntent();

        btnBack = findViewById(R.id.backBtn_Analyze);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Set up recyclerview and pass result to adapter
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(AnalyzeActivity.this,LinearLayoutManager.HORIZONTAL,false));
        myAdapter = new ShowAnalyzeAdapter(this, rlist);
        recyclerView.setAdapter(myAdapter);
        readMyResultOnce();

    }

    // Retreive result from database
    private void readMyResultOnce() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Get survey id from the last activity
                String surveyId = intent.getStringExtra("surveyId");
                rlist.clear();

                //Retreive data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Result result = snapshot.getValue(Result.class);

                    if (result.getSurveyId().equals(surveyId)) {
                        rlist.add(result);

                    }
                }

                myAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("AnalyzeActivity", "loadSurvey:onCancelled", databaseError.toException());
            }

        });
    }

    // Back bottom leads back to the summart activity
    public void backBtn(View view) {
        Intent intent = new Intent(this, SummaryActivity.class);
        finish();
    }

    // Share buttom that allows user to downloads data and export to external storage in Csv format
    public void shareBtn(View view) {

        StringBuilder data = writeResult();
        try {
            //Write and save the result file
            FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
            out.write(data.toString().getBytes());
            out.close();

            //Share file to the external place such as google drive/ gmail etc;
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(),"data.csv");
            Uri path = FileProvider.getUriForFile(context,"com.meow.comp6442_groupproject.fileprovider",filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM,path);
            //startActivity(fileIntent);
            startActivity(Intent.createChooser(fileIntent,"Share result"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    @Override
    //Back up solution to download the result to the local for future business continuity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        intent = data;
        OutputStream outputStream;
        StringBuilder result = writeResult();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK){
            try {
                outputStream = getContentResolver().openOutputStream(data.getData());
                outputStream.write(result.toString().getBytes());
                outputStream.close();

                Thread.sleep(3000);
                Uri path = data.getData();
                Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                viewIntent.addCategory(Intent.CATEGORY_DEFAULT);
                viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                viewIntent.setDataAndType(path,"text/csv");
                viewIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(viewIntent, "Open file with: "));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    // Write results into CSV
    private StringBuilder writeResult(){
        //Find out the total number of vote for each question
        ArrayList<Integer> totalVote = new ArrayList<>();
        totalVote.clear();

        for (int i = 0; i < rlist.size();i++) {
            int total = 0;
            Set content = rlist.get(i).getResList().keySet();
            for (Object k : content) {
                String text = (String) k;
                int value = rlist.get(i).getResList().get(text);
                total += value;
            }
            totalVote.add(total);
        }

        //Writing content of the file
        StringBuilder data = new StringBuilder();
        data.append("Survey result"+ "\r\n");
        for (int i = 0; i < rlist.size(); i++) {
            data.append(i+1 +": "+rlist.get(i).getQuestion());
            data.append(",");
            data.append("Vote");
            data.append(",");
            data.append("Percentage");
            data.append("\r\n");
            Set content = rlist.get(i).getResList().keySet();
            for (Object k : content) {
                String text = (String) k;
                int value = rlist.get(i).getResList().get(text);
                String cleanedText = text;
                if (text.contains(",")){
                    if (text.contains("\"")){
                        cleanedText = cleanedText.replace("\"","\"\"");
                    }
                    cleanedText = "\"" + cleanedText + "\"";
                }
                data.append(cleanedText);
                data.append(",");
                data.append(Integer.toString(value));
                data.append(",");
                if (totalVote.get(i) > 0){
                    double res = (double) value/totalVote.get(i);
                    data.append(Double.toString(res*100)).append("%");
                }
                data.append("\r\n");
            }
            data.append("\r\n");
        }
        return data;
    }



}
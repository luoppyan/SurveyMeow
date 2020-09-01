package com.meow.comp6442_groupproject.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meow.comp6442_groupproject.Model.Survey;
import com.meow.comp6442_groupproject.R;

import java.util.ArrayList;


public class ShowMySurveysAdapter extends RecyclerView.Adapter<ShowMySurveysAdapter.ViewHolder> {


    //List of surveys
    private ArrayList<Survey> mSurvey;
    private Context mContext;

    //Items on click listener
    private ShowMySurveysAdapter.OnItemClickListener onItemClickListener;


    //Constructor
    public ShowMySurveysAdapter(Context mContext, ArrayList<Survey> surveys){
        this.mContext = mContext;
        this.mSurvey = surveys;
    }



    //Define onclickListener
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    //Can be used to set onclickListener outside the adapter
    public void setOnItemClickListener(ShowMySurveysAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    //Self defined ViewHolder, the view of each question
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView surveyTitle;
        TextView createDate;
        TextView status;

        ViewHolder(View itemView) {
            super(itemView);
            surveyTitle = itemView.findViewById(R.id.survey_title);
            createDate = itemView.findViewById(R.id.survey_date);
            status = itemView.findViewById(R.id.survey_status);
        }
    }


    @NonNull
    @Override
    public ShowMySurveysAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mysurveylist_layout, parent, false);
        return new ShowMySurveysAdapter.ViewHolder(v);

    }



    @Override
    public void onBindViewHolder(final ShowMySurveysAdapter.ViewHolder holder, int position) {

        //Initialize variables
        Survey survey = mSurvey.get(position);
        String surveyTitle = survey.getTitle();
        String status = survey.getStatus();
        String createDate = "Created: %s";
        createDate = String.format(createDate, survey.getCreatedate());


        //Set view
        holder.surveyTitle.setText(surveyTitle);
        holder.createDate.setText(createDate);
        holder.status.setText(status);
        if(status.equals("close")){
            holder.status.setTextColor(Color.parseColor("#E91E2C"));
        }else{
            holder.status.setTextColor(Color.parseColor("#0AC1B0"));
        }


        //When survey item is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });


        //When survey item is long clicked
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView, pos);
                }

                return true;
            }
        });

    }



    @Override
    public int getItemCount(){
        return mSurvey.size();
    }


    public Survey getItem(int pos){
        return mSurvey.get(pos);
    }




}

package com.meow.comp6442_groupproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meow.comp6442_groupproject.Model.Survey;
import com.meow.comp6442_groupproject.R;

import java.util.ArrayList;


public class ShowAllSurveysAdapter extends RecyclerView.Adapter<ShowAllSurveysAdapter.ViewHolder> {


    //List of surveys
    private ArrayList<Survey> mSurvey;
    private Context mContext;

    //Items on click listener
    private ShowAllSurveysAdapter.OnItemClickListener onItemClickListener;


    //Constructor
    public ShowAllSurveysAdapter(Context mContext, ArrayList<Survey> surveys){
        this.mContext = mContext;
        this.mSurvey = surveys;
    }



    //Define onclickListener
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    //Can be used to set onclickListener outside the adapter
    public void setOnItemClickListener(ShowAllSurveysAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    //Self defined ViewHolder, the view of each question
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView surveyTitle;
        TextView createDate;
        TextView closeDate;

        ViewHolder(View itemView) {
            super(itemView);
            surveyTitle = itemView.findViewById(R.id.surveyTitle_home);
            createDate = itemView.findViewById(R.id.createDate_home);
            closeDate = itemView.findViewById(R.id.endDate_home);
        }
    }


    @NonNull
    @Override
    public ShowAllSurveysAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.allsurveylist_layout, parent, false);
        return new ShowAllSurveysAdapter.ViewHolder(v);

    }



    @Override
    public void onBindViewHolder(final ShowAllSurveysAdapter.ViewHolder holder, int position) {

        //Initialize variables
        Survey survey = mSurvey.get(position);
        String surveyTitle = survey.getTitle();
        String createDate = "Created: %s";
        String closeDate = "Closing: %s";
        createDate = String.format(createDate, survey.getCreatedate());
        closeDate = String.format(closeDate, survey.getEnddate());


        //Set view
        holder.surveyTitle.setText(surveyTitle);
        holder.createDate.setText(createDate);
        holder.closeDate.setText(closeDate);


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

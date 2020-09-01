package com.meow.comp6442_groupproject.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.meow.comp6442_groupproject.Model.Group;
import com.meow.comp6442_groupproject.Model.Survey;
import com.meow.comp6442_groupproject.R;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ShowGroupSurveyAdapter extends RecyclerView.Adapter<ShowGroupSurveyAdapter.GroupSurveyViewHolder> implements Filterable {

    //List of questions
    private Context mContext;
    private List<Survey> surveys;
    private List<Survey> mFilterList;
    private Survey survey;
    OnItemClickListener listener;

    @Override
    public Filter getFilter() {
        return new Filter() {
            //Act the filter operation
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                if (charString.isEmpty()) {
                    //If nothing to be filtered, use the original data
                    mFilterList = surveys;
                } else {
                    List<Survey> filteredList = new ArrayList<>();
                    for (Survey survey : surveys) {
                        //rule for matching
                        if (survey.getTitle().toLowerCase().contains(charString)) {
                            filteredList.add(survey);
                        }
                    }

                    mFilterList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilterList;
                return filterResults;
            }
            //return the result list
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilterList = (ArrayList<Survey>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener{
        void OnItemClick(View view, int pos);
    }
    public void setOnItemClick(ShowGroupSurveyAdapter.OnItemClickListener listener){
        this.listener=listener;
    }
    //Constructor
    public ShowGroupSurveyAdapter(Context mContext, List<Survey> surveys){
        this.mContext = mContext;
        this.surveys = surveys;
        this.mFilterList = surveys;
    }

    //Self defined ViewHolder, the view of each option
    class GroupSurveyViewHolder extends RecyclerView.ViewHolder {
        TextView surveyTitle;
        TextView createDate;
        TextView closeDate;

        public GroupSurveyViewHolder(View itemView) {
            super(itemView);
            surveyTitle = itemView.findViewById(R.id.surveyTitle);
            createDate = itemView.findViewById(R.id.createDate);
            closeDate = itemView.findViewById(R.id.endDate);
        }

    }

    @NonNull
    @Override
    public ShowGroupSurveyAdapter.GroupSurveyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_surveylist_layout, parent, false);
        return new ShowGroupSurveyAdapter.GroupSurveyViewHolder(v);

    }

    // Binding data
    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(final ShowGroupSurveyAdapter.GroupSurveyViewHolder holder, int position) {
        if (holder != null){

            //Initialize variables
            Survey survey = mFilterList.get(position);
            String surveyTitle = survey.getTitle();
            String createDate = "Created: %s";
            String closeDate = "Closing: %s";
            createDate = String.format(createDate, survey.getCreatedate());
            closeDate = String.format(closeDate, survey.getEnddate());


            //Set view
            holder.surveyTitle.setText(surveyTitle);
            holder.createDate.setText(createDate);
            holder.closeDate.setText(closeDate);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    /*注意参数*/
                    if(listener != null) {
                        int pos = holder.getLayoutPosition();
                        listener.OnItemClick(holder.itemView, pos);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return mFilterList.size();
    }



}

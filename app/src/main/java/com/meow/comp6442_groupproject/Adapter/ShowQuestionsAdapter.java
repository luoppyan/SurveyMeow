package com.meow.comp6442_groupproject.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meow.comp6442_groupproject.Model.Option;
import com.meow.comp6442_groupproject.Model.Question;
import com.meow.comp6442_groupproject.R;

import java.util.ArrayList;
import java.util.Locale;


public class ShowQuestionsAdapter extends RecyclerView.Adapter<ShowQuestionsAdapter.ViewHolder> {


    //List of questions
    private ArrayList<Question> mQuestion;
    private Context mContext;

    //Items on click listener
    private ShowQuestionsAdapter.OnItemClickListener onItemClickListener;


    //Constructor
    public ShowQuestionsAdapter(Context mContext, ArrayList<Question> questions){
        this.mContext = mContext;
        this.mQuestion = questions;
    }


    /**
    ** Update question list, add new question in to list, or remove question from list.
    * These actions can be handle outside the Adapter
     **/
    public void updateData(ArrayList<Question> questions){
        mQuestion.clear();
        mQuestion.addAll(questions);
        notifyDataSetChanged();
    }

    //add new question
    public void addNewQuestion(Question q){
        if(mQuestion == null){
            mQuestion = new ArrayList<Question>();
        }
        mQuestion.add(q);
        notifyItemInserted(-1);
    }

    //delete question
    public void deleteQuestion(int position){
        if(mQuestion == null || mQuestion.isEmpty()) {
            return;
        }
        mQuestion.remove(position);
        notifyItemRemoved(position);
    }
    /**
     ** Actions methods end
     **/


    //Define onclickListener
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    //Can be used to set onclickListener outside the adapter
    public void setOnItemClickListener(ShowQuestionsAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    //Self defined ViewHolder, the view of each question
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionTitle;
        LinearLayout linearOptions;

        public ViewHolder(View itemView) {
            super(itemView);
            questionTitle = itemView.findViewById(R.id.question_title);
            linearOptions = itemView.findViewById(R.id.lly_options);
        }
    }


    @NonNull
    @Override
    public ShowQuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_layout, parent, false);
        return new ShowQuestionsAdapter.ViewHolder(v);

    }



    @Override
    public void onBindViewHolder(final ShowQuestionsAdapter.ViewHolder holder, int position) {

        //Define variables
        Question question = mQuestion.get(position);
        ArrayList<Option> options = question.getOptions();
        String type = question.getType();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(30,10,70,0);
        holder.itemView.setClickable(true);
        holder.itemView.setFocusable(true);

        //Get question title and set the question title view
        String title = "%d. %s";
        title = String.format(new Locale("AU"), title,position + 1, question.getTitle());
        holder.questionTitle.setText(title);


        //Show each question's options
        if(options != null) {
            for (int i = 0; i < options.size(); i++) {
                if (type.equals("MC")) {
                    RadioButton radioBtn = new RadioButton(mContext);
                    radioBtn.setText(options.get(i).getContent());
                    radioBtn.setTextColor(Color.parseColor("#FFFFFF"));
                    radioBtn.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                    radioBtn.setLayoutParams(params);
                    radioBtn.setClickable(false);
                    holder.linearOptions.addView(radioBtn);
                } else {
                    CheckBox cbBtn = new CheckBox(mContext);
                    cbBtn.setText(options.get(i).getContent());
                    cbBtn.setTextColor(Color.parseColor("#FFFFFF"));
                    cbBtn.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                    cbBtn.setLayoutParams(params);
                    cbBtn.setClickable(false);
                    holder.linearOptions.addView(cbBtn);
                }

            }
        }


        //When question item is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });


        //When question item is long clicked
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


    //When question's options are modified, remove the old options' view
    @Override
    public void onViewRecycled(final ShowQuestionsAdapter.ViewHolder holder) {
        // delete all items
        holder.linearOptions.removeAllViews();
    }


    @Override
    public int getItemCount(){
        return mQuestion.size();
    }


    public Question getItem(int pos){
        return mQuestion.get(pos);
    }


    public ArrayList<Question> getmQuestion(){
        return this.mQuestion;
    }


}

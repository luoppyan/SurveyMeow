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
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meow.comp6442_groupproject.Model.Option;
import com.meow.comp6442_groupproject.Model.Question;
import com.meow.comp6442_groupproject.R;

import java.util.ArrayList;
import java.util.Locale;


public class FillSurveyAdapter extends RecyclerView.Adapter<FillSurveyAdapter.ViewHolder> {


    //List of questions
    private ArrayList<Question> mQuestion;
    private Context mContext;

    //Items on click listener
    private FillSurveyAdapter.OnItemClickListener onItemClickListener;


    //Constructor
    public FillSurveyAdapter(Context mContext, ArrayList<Question> questions){
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
    public void setOnItemClickListener(FillSurveyAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    //Self defined ViewHolder, the view of each question
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionTitle;
        LinearLayout linearOptions;
        RadioGroup radioGroup_options;

        ViewHolder(View itemView) {
            super(itemView);
            questionTitle = itemView.findViewById(R.id.question_title);
            linearOptions = itemView.findViewById(R.id.lly_options);
            radioGroup_options = itemView.findViewById(R.id.radioGroup_options);
        }
    }


    @NonNull
    @Override
    public FillSurveyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_layout, parent, false);
        return new FillSurveyAdapter.ViewHolder(v);

    }



    @Override
    public void onBindViewHolder(final FillSurveyAdapter.ViewHolder holder, int position) {

        //Define variables
        Question question = mQuestion.get(position);
        ArrayList<Option> options = question.getOptions();
        String type = question.getType();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(30,10,70,0);
        holder.itemView.setClickable(false);
        holder.itemView.setFocusable(false);

        //Get question title and set the question title view
        String title = "%d. %s";
        title = String.format(new Locale("AU"), title,position + 1, question.getTitle());
        holder.questionTitle.setText(title);


        //Show each question's options
        if(options != null) {
            for (int i = 0; i < options.size(); i++) {
                if (type.equals("MC")) {
                    holder.radioGroup_options.setVisibility(View.VISIBLE);
                    holder.linearOptions.setVisibility(View.GONE);
                    RadioButton radioBtn = new RadioButton(mContext);
                    radioBtn.setText(options.get(i).getContent());
                    radioBtn.setTextColor(Color.parseColor("#FFFFFF"));
                    radioBtn.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                    radioBtn.setLayoutParams(params);
                    radioBtn.setOnClickListener(new optionOnClickListener(position, i));
                    holder.radioGroup_options.addView(radioBtn);

                } else {
                    holder.radioGroup_options.setVisibility(View.GONE);
                    holder.linearOptions.setVisibility(View.VISIBLE);
                    CheckBox cbBtn = new CheckBox(mContext);
                    cbBtn.setText(options.get(i).getContent());
                    cbBtn.setTextColor(Color.parseColor("#FFFFFF"));
                    cbBtn.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                    cbBtn.setLayoutParams(params);
                    cbBtn.setOnClickListener(new optionOnClickListener(position, i));
                    holder.linearOptions.addView(cbBtn);
                }
            }
        }


    }



    class optionOnClickListener implements View.OnClickListener {

        private int position;
        private int i;


        optionOnClickListener(int position, int i) {
            this.position = position;
            this.i = i;
        }


        @Override
        public void onClick(View arg0) {

            if (mQuestion.get(position).getType().equals("CB")) {
                //CB
                if (mQuestion.get(position).getOptions().get(i).getStatus().equals("N")) {
                    //If currently not be chose
                    mQuestion.get(position).getOptions().get(i).setStatus("Y");
                    mQuestion.get(position).setStatus("Y");
                } else {
                    mQuestion.get(position).getOptions().get(i).setStatus("N");
                    mQuestion.get(position).setStatus("N");
                    for (int j = 0; j < mQuestion.get(position).getOptions().size(); j++) {
                        if(mQuestion.get(position).getOptions().get(j).getStatus().equals("Y")){
                            mQuestion.get(position).setStatus("Y");
                            break;
                        }
                    }

                }
            } else {
                //MC
                for (int j = 0; j < mQuestion.get(position).getOptions().size(); j++) {
                    mQuestion.get(position).getOptions().get(j).setStatus("N");
                }
                mQuestion.get(position).getOptions().get(i).setStatus("Y");
                mQuestion.get(position).setStatus("Y");
            }
        }
    }



    @Override
    public int getItemCount(){
        return mQuestion.size();
    }

    @Override
    public void onViewRecycled(final FillSurveyAdapter.ViewHolder holder) {
        // delete all items
        holder.linearOptions.removeAllViews();
        holder.radioGroup_options.removeAllViews();
    }

    public Question getItem(int pos){
        return mQuestion.get(pos);
    }


    public ArrayList<Question> getmQuestion(){
        return this.mQuestion;
    }


}

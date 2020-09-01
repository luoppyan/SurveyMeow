package com.meow.comp6442_groupproject.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meow.comp6442_groupproject.EditQuestionActivity;
import com.meow.comp6442_groupproject.Model.Option;
import com.meow.comp6442_groupproject.R;

import java.util.ArrayList;

public class ShowOptionsAdapter extends RecyclerView.Adapter<ShowOptionsAdapter.ViewHolder> {

    //List of options
    private ArrayList<Option> mOption;
    private Context mContext;

    //Items on click listener
    private ShowOptionsAdapter.OnItemClickListener onItemClickListener;


    //Constructor
    public ShowOptionsAdapter(Context mContext, ArrayList<Option> options){
        this.mContext = mContext;
        this.mOption = options;
    }


    /**
     ** Update option list, add new option in to list, or remove option from list.
     * These actions can be handle outside the Adapter
     **/
    public void updateData(ArrayList<Option> options){
        mOption.clear();
        mOption.addAll(options);
        notifyDataSetChanged();
    }

    //add new option
    public void addNewOption(int position){
        if(mOption == null){
            mOption = new ArrayList<Option>();
        }
        mOption.add(position, new Option());
        Log.w("ShowOptionsAdapter", "Add " + String.valueOf(position));
        notifyItemInserted(position);

    }

    //delete option
    public void deleteOption(int position){
        if(mOption == null || mOption.isEmpty()) {
            return;
        }
        mOption.remove(position);
        Log.w("ShowOptionsAdapter", "Remove " + String.valueOf(position));
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
    public void setOnItemClickListener(ShowOptionsAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    //Self defined ViewHolder, the view of each option
    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText etOptionContent;
        Button addOptionBtn, removeOptionBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            etOptionContent = itemView.findViewById(R.id.etOption_editQuestion);
            addOptionBtn = itemView.findViewById(R.id.addBtn_editQuestion);
            removeOptionBtn = itemView.findViewById(R.id.removeBtn_editQuestion);

        }
    }


    @NonNull
    @Override
    public ShowOptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.editquestion_option_layout, parent, false);
        return new ShowOptionsAdapter.ViewHolder(v);

    }


    @Override
    public void onBindViewHolder(final ShowOptionsAdapter.ViewHolder holder, int position) {

        //If question already have options, show them
        if(mOption.get(position).getContent() != null){
            holder.etOptionContent.setText(mOption.get(position).getContent());
        }else{
            holder.etOptionContent.setText("");
        }


        //Add new option EditText under the current option
        holder.addOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                addNewOption(pos + 1);
            }
        });


        //Remove option EditText
        holder.removeOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOption.size() == 1){
                    Toast.makeText(mContext,
                            "At least need one choice!", Toast.LENGTH_SHORT).show();
                }
                else {
                    int pos = holder.getLayoutPosition();
                    deleteOption(pos);
                }
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }

            }
        });


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


        //Add monitor to option's EditText, when text is changed, change the related option's content
        holder.etOptionContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int pos = holder.getLayoutPosition();
                mOption.get(pos).setContent(editable.toString());
            }
        });


        //UI related
        holder.etOptionContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager =(InputMethodManager)mContext.getSystemService(
                            mContext.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

    }


    @Override
    public int getItemCount(){
        return mOption.size();
    }


    public Option getItem(int pos){
        return mOption.get(pos);

    }

    public ArrayList<Option> getOptions(){
        return mOption;

    }


    //Remove empty options and return the trimed option list.
    public ArrayList<Option> getTrimOptions(){
        ArrayList<Option> trimOptions = new ArrayList<Option>();
        for(Option op : mOption){
            if(op.getContent()!=null && !op.getContent().equals("")){
                trimOptions.add(op);
            }
            if(op.getContent().contains("/") || op.getContent().contains(".") ||
                    op.getContent().contains("#") || op.getContent().contains("$") ||
                    op.getContent().contains("[") || op.getContent().contains("]")){
                Toast.makeText(mContext,
                        "Option must not contain '/', '.', '#', '$', '[', or ']'", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return trimOptions;
    }

}

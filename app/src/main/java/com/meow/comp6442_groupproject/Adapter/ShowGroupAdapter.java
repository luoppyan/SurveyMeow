package com.meow.comp6442_groupproject.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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

import com.google.firebase.database.DatabaseReference;
import com.meow.comp6442_groupproject.Model.Group;
import com.meow.comp6442_groupproject.R;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ShowGroupAdapter extends RecyclerView.Adapter<ShowGroupAdapter.GroupViewHolder> implements Filterable {

    private Context mContext;
    private List<Group> groups;
    private List<Group> mFilterList;
    OnItemClickListener listener;
    private DatabaseReference mDatabase;// ...

    @Override
    public Filter getFilter() {
        return new Filter() {
            //Act the filter operation
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                if (charString.isEmpty()) {
                    //If nothing to be filtered, use the original data
                    mFilterList = groups;
                } else {
                    List<Group> filteredList = new ArrayList<>();
                    for (Group group : groups) {
                        //rule for matching
                        if (group.getGroupTitle().toLowerCase().contains(charString)) {
                            filteredList.add(group);
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
                mFilterList = (ArrayList<Group>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

    public interface OnItemClickListener{
        public void OnItemClick(View view, int position);
    }
    public void setOnItemClick(OnItemClickListener listener){
        this.listener=listener;
    }

    private Group data;
    //Constructor
    public ShowGroupAdapter(Context mContext, List<Group> groups){
        this.mContext = mContext;
        this.groups = groups;
        this.mFilterList = groups;
    }

    //Self defined ViewHolder, the view of each option
    class GroupViewHolder extends RecyclerView.ViewHolder {
        private TextView groupName;
        private TextView latestMessage;

        public GroupViewHolder(View itemView) {
            super(itemView);
            this.groupName = itemView.findViewById(R.id.groupName_groupList);
            this.latestMessage = itemView.findViewById(R.id.latestMessage_groupList);
        }

    }

    @NonNull
    @Override
    public ShowGroupAdapter.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list, parent, false);
        return new ShowGroupAdapter.GroupViewHolder(v);

    }
    // Binding data
    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(final ShowGroupAdapter.GroupViewHolder holder, int position) {
        if (holder instanceof GroupViewHolder){
            data = mFilterList.get(position);
            ((GroupViewHolder)holder).groupName.setText(data.getGroupTitle()+" ("+data.getMembers().size()+")");
            ((GroupViewHolder)holder).latestMessage.setText(data.getGroupDescr());


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int pos = holder.getLayoutPosition();
                        listener.OnItemClick(holder.itemView, pos);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return mFilterList.size();
    }
}

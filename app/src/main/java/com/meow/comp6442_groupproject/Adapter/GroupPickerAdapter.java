package com.meow.comp6442_groupproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.meow.comp6442_groupproject.Model.Group;
import com.meow.comp6442_groupproject.R;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class GroupPickerAdapter extends RecyclerView.Adapter<GroupPickerAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Group> mGroups;

    private GroupPickerAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position, boolean isChecked);
    }

    public void setOnItemClickListener(GroupPickerAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    //Constructor
    public GroupPickerAdapter(Context mContext, ArrayList<Group> groups){
        this.mContext = mContext;
        this.mGroups = groups;
    }

    @NonNull
    @Override
    public GroupPickerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_picker_list, parent, false);
        return new GroupPickerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final GroupPickerAdapter.ViewHolder holder, final int position) {
        Group currGroup = mGroups.get(position);

        holder.groupName.setText(currGroup.getGroupTitle());

        holder.groupPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos, ((CheckBox)view).isChecked());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public Group getGroup(int position) {
        return mGroups.get(position);
    }

    //Self defined ViewHolder, the view of each question
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView groupName;
        private CheckBox groupPicker;

        ViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName_group_picker);
            groupPicker = itemView.findViewById(R.id.group_picker);
        }
    }
}
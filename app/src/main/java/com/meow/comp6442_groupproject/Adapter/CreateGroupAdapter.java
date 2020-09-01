package com.meow.comp6442_groupproject.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.meow.comp6442_groupproject.Model.User;
import com.meow.comp6442_groupproject.R;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class CreateGroupAdapter extends RecyclerView.Adapter<CreateGroupAdapter.MemberViewHolder> {

    private Context mContext;
    private List<User> datas;
    User data;
    OnItemClickListener listener;
    OnItemLongClickListener longClickListener;
    public interface OnItemClickListener{
        void OnItemClick();
    }
    public interface OnItemLongClickListener{
        void OnItemLongClick(View view, int pos);
    }
    public void setOnItemClick(OnItemClickListener listener){
        this.listener=listener;
    }
    public void setOnItemLongClick(OnItemLongClickListener listener){
        this.longClickListener=listener;
    }

    //Constructor
    public CreateGroupAdapter(Context mContext, List<User> datas){
        this.mContext = mContext;
        this.datas = datas;
    }



    //Self defined ViewHolder, the view of each option
    class MemberViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView memberName;

        public MemberViewHolder(View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.icon_groupMemberAvatar);
            this.memberName = itemView.findViewById(R.id.groupMemberName);
        }

    }
    // Create the view holder
    @NonNull
    @Override
    public CreateGroupAdapter.MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_member_list,
                parent, false);
        return new CreateGroupAdapter.MemberViewHolder(v);

    }

    //Binding data to the viewholder
    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(final CreateGroupAdapter.MemberViewHolder holder, int position) {
        if (holder != null){
            data = datas.get(position);
            ((MemberViewHolder)holder).memberName.setText(data.getUsername());

            if (data.getGender().equalsIgnoreCase("female"))
                ((MemberViewHolder)holder).icon.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),R.drawable.avatar_female));
            else
                ((MemberViewHolder)holder).icon.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),R.drawable.avatar_male));
//            when needed click listener
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        listener.OnItemClick();
                    }
                }
            });

            //When survey item is long clicked
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(longClickListener != null) {
                        int pos = holder.getLayoutPosition();
                        longClickListener.OnItemLongClick(holder.itemView, pos);
                    }

                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return datas.size();
    }



}


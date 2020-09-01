package com.meow.comp6442_groupproject.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.meow.comp6442_groupproject.R;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ShowMemberAdapter extends RecyclerView.Adapter<ShowMemberAdapter.MemberViewHolder> {

    private Context mContext;
    private List<dataDemo> datas = new ArrayList<>();
    OnItemClickListener listener;
    public interface OnItemClickListener{
        /*注意参数*/
        public void OnItemClick();
    }
    public void setOnItemClick(OnItemClickListener listener){
        this.listener=listener;
    }

    private dataDemo data;
    //Constructor
    public ShowMemberAdapter(Context mContext){
        this.mContext = mContext;
        setText();
    }

    class dataDemo{
        private String name;
        private String imageURL = "..//res//image//IMG_6110.JPG";

        public dataDemo(String name){
            this.name = name;
        }

        public String getImageURL() {
            return imageURL;
        }

        public String getName() {
            return name;
        }
    }

    public void setText(){
        this.datas.add(new dataDemo("name1"));
        this.datas.add(new dataDemo("name2"));
        this.datas.add(new dataDemo("name3"));
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

    @NonNull
    @Override
    public ShowMemberAdapter.MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_member_list,
                parent, false);
        return new ShowMemberAdapter.MemberViewHolder(v);

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(final ShowMemberAdapter.MemberViewHolder holder, int position) {
        if (holder instanceof MemberViewHolder){
            data = datas.get(position);
            ((MemberViewHolder)holder).memberName.setText(data.getName());
            ((MemberViewHolder)holder).icon.setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),R.drawable.avatar_male));
//            when needed click listener
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        /*注意参数*/
                        listener.OnItemClick();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return datas.size();
    }



}

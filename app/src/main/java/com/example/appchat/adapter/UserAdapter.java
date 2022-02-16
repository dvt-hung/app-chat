package com.example.appchat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.R;
import com.example.appchat.model.User;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> listUser;
    private Context mContext;
    private IUserAdapter iUserAdapter;

    public interface IUserAdapter{
        void onClickUser(User user);
    }

    public UserAdapter(Context mContext,IUserAdapter iUserAdapter) {
        this.mContext = mContext;
        this.iUserAdapter = iUserAdapter;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataUser(List<User> list){
        this.listUser = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = listUser.get(position);
        if (user != null)
        {

            holder.txt_NameUser.setText(user.getNameUser());
            holder.txt_StatusUser.setText(user.getStatusUser());
            Glide.with(mContext).load(user.getImgUser()).into(holder.img_Avatar);
            holder.layout_User.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iUserAdapter.onClickUser(user);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (listUser != null)
        {
            return listUser.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img_Avatar;
        RelativeLayout layout_User;
        TextView txt_NameUser;
        TextView txt_StatusUser;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            img_Avatar = itemView.findViewById(R.id.img_Avatar);
            layout_User = itemView.findViewById(R.id.layout_User);
            txt_NameUser = itemView.findViewById(R.id.txt_NameUser);
            txt_StatusUser = itemView.findViewById(R.id.txt_StatusUser);
        }
    }
}

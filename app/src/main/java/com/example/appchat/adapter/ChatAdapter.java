package com.example.appchat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.R;
import com.example.appchat.activity.AccountActivity;
import com.example.appchat.activity.ChatActivity;
import com.example.appchat.activity.MainActivity;
import com.example.appchat.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter {

    private List<Message> listMessage;
    private Context mContext;
    private FirebaseUser senderUser;
    private final int SENDER_TYPE = 1;
    private final int RECEIVED_TYPE = 2;


    public ChatAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataMessage(List<Message> list){
        this.listMessage = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_TYPE)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right,parent,false);
            return new ChatRightViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left,parent,false);
            return new ChatLeftViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = listMessage.get(position);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");

        if (message != null)
        {
            if (holder.getClass() == ChatRightViewHolder.class)
            {
                ChatRightViewHolder rightHolder = (ChatRightViewHolder) holder;
                rightHolder.txtMessageRight.setText(message.getMessage());
                rightHolder.txtTimeRight.setText(dateFormat.format(message.getTime()));

            }else {
                ChatLeftViewHolder leftHolder = (ChatLeftViewHolder) holder;
                leftHolder.txtMessageLeft.setText(message.getMessage());
                leftHolder.txtTimeLeft.setText(dateFormat.format(message.getTime()));
            }
        }

    }

    @Override
    public int getItemCount() {
        if (listMessage != null)
        {
            return listMessage.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        Message mess = listMessage.get(position);
        senderUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mess.getSenderID().equals(senderUser.getUid()))
        {
            return SENDER_TYPE;
        }
        else {
            return RECEIVED_TYPE;
        }
    }

    public static class ChatLeftViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessageLeft,txtTimeLeft;
        public ChatLeftViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessageLeft = itemView.findViewById(R.id.txt_Message_Left);
            txtTimeLeft = itemView.findViewById(R.id.txt_Time_Left);
        }
    }

    public static class ChatRightViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessageRight,txtTimeRight;
        public ChatRightViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessageRight = itemView.findViewById(R.id.txt_Message_Right);
            txtTimeRight = itemView.findViewById(R.id.txt_Time_Right);
        }
    }
}

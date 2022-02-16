package com.example.appchat.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.R;
import com.example.appchat.adapter.ChatAdapter;
import com.example.appchat.model.Message;
import com.example.appchat.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private TextView txt_Name_Chat;
    private ImageButton btn_Finish_Chat, btn_Send_Chat;
    private RecyclerView recycler_Chat;
    private EditText edt_Message_Chat;
    private FirebaseUser currentUser;
    private List<Message> listMessage;
    private ChatAdapter chatAdapter;
    private User receivedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        // init view 
        initView();

        // received user
        getReceiverUser(getIntent().getStringExtra("idFriend"));
        // CurrentUser
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Adapter recycler chat
        chatAdapter = new ChatAdapter(this);
        recycler_Chat.setAdapter(chatAdapter);
        // Layout manager recycler chat
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        layoutManager.setStackFromEnd(true);
        recycler_Chat.setLayoutManager(layoutManager);

        // Load message
        loadMessage();

    }

    // ======= GET RECEIVER USER  =======

    private void getReceiverUser(String id)
    {
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("User");
        referenceUser.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                        receivedUser = user;
                        txt_Name_Chat.setText(receivedUser.getNameUser());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // ======= LOAD MESSAGE  =======

    private void loadMessage() {
        listMessage = new ArrayList<>();
        DatabaseReference referenceChat = FirebaseDatabase.getInstance().getReference("Chats");
        referenceChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMessage.clear();
                for (DataSnapshot data : snapshot.getChildren())
                {
                    Message message = data.getValue(Message.class);
                    if (message != null)
                    {
                        if (message.getSenderID().equals(currentUser.getUid()) && message.getReceiverID().equals(receivedUser.getIdUser())
                        || message.getSenderID().equals(receivedUser.getIdUser()) && message.getReceiverID().equals(currentUser.getUid()))
                        {
                            listMessage.add(message);
                            chatAdapter.setDataMessage(listMessage);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Button finish chat
        btn_Finish_Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Button send message
        btn_Send_Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = edt_Message_Chat.getText().toString().trim();
                if (mess.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Hãy nhập gì đó đi", Toast.LENGTH_SHORT).show();
                }else {
                    sendMessage(mess);
                    edt_Message_Chat.setText("");
                    closeKeyboard();
                }
            }
        });
    }

    // ======= CLOSE KEYBOARD =======
    private void closeKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    // ======= SEND MESSAGE =======
    private void sendMessage(String mess) {
        Date date = new Date();
        Message message = new Message(currentUser.getUid(),receivedUser.getIdUser(),mess,date.getTime(),false);
        DatabaseReference referenceChat = FirebaseDatabase.getInstance().getReference("Chats");
        referenceChat.push().setValue(message);

    }

    private void initView() {
        txt_Name_Chat       = findViewById(R.id.txt_Name_Chat);
        btn_Finish_Chat     = findViewById(R.id.btn_Finish_Chat);
        btn_Send_Chat       = findViewById(R.id.btn_Send_Chat);
        recycler_Chat       = findViewById(R.id.recycler_Chat);
        edt_Message_Chat    = findViewById(R.id.edt_Message_Chat);

    }
}
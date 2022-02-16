package com.example.appchat.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.appchat.ChatApplication;
import com.example.appchat.R;
import com.example.appchat.activity.ChatActivity;
import com.example.appchat.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChatService extends Service {

    private DatabaseReference referenceChat;
    private FirebaseUser currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        referenceChat = FirebaseDatabase.getInstance().getReference("Chats");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getMessage();
        return START_NOT_STICKY;
    }

    private void getMessage() {

        referenceChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                assert message != null;
                if (message.getReceiverID().equals(currentUser.getUid()) && !message.isSeen())
                {
                    sendNotificationNewMessage(message);
                    HashMap<String,Object> seen = new HashMap<>();
                    seen.put("seen",true);
                    snapshot.getRef().updateChildren(seen);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotificationNewMessage(Message message) {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        Intent intentChat = new Intent(this, ChatActivity.class);
        intentChat.putExtra("idFriend",message.getSenderID());

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntentChat = PendingIntent.getActivity(this,0,intentChat,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ChatApplication.CHANNEL_CHAT)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(bitmap)
                .setContentTitle("Tin nhắn mới")
                .setAutoCancel(true)
                .setContentIntent(pendingIntentChat)
                .setContentText(message.getMessage());

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify((int) message.getTime(),builder.build());
    }
}

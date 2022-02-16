package com.example.appchat.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.appchat.R;
import com.example.appchat.Service.ChatService;
import com.example.appchat.adapter.UserAdapter;
import com.example.appchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnSetting;
    private RecyclerView recyclerUser;
    private UserAdapter userAdapter;
    private DatabaseReference referenceUser;
    private List<User> listUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init view
        initView();


        // init Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        referenceUser = database.getReference("User");
        // User adapter
        userAdapter = new UserAdapter(this, new UserAdapter.IUserAdapter() {
            @Override
            public void onClickUser(User user) {
                // Next to activity chat

                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("idFriend",user.getIdUser());
                startActivity(intent);
            }
        });
        recyclerUser.setAdapter(userAdapter);

        // Layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerUser.setLayoutManager(layoutManager);

        // Load list user
        loadListUser();

        // Call service
        Intent intentService = new Intent(this, ChatService.class);
        startService(intentService);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Button Setting
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSetting();
            }
        });
    }
    // ========= Show dialog setting =========
    private void showDialogSetting() {

        Dialog dialogSetting = new Dialog(this);
        dialogSetting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSetting.setContentView(R.layout.dialog_setting);

        Window window = dialogSetting.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.DialogAnimation);
        // View
        Button btnAccount = dialogSetting.findViewById(R.id.btn_Setting_Account);
        Button btnLogOut = dialogSetting.findViewById(R.id.btn_Setting_LogOut);
        Button btnCancel = dialogSetting.findViewById(R.id.btn_Setting_Cancel);


        // Button Account
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AccountActivity.class));
                dialogSetting.dismiss();
            }
        });

        // Button Log Out
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialogLogOut();
            }

        });

        // Button Cancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetting.dismiss();
            }
        });

        dialogSetting.show();

    }

    // ========= Show dialog log out =========
    private void showDialogLogOut() {
        Dialog dialogLogOut = new Dialog(MainActivity.this);
        dialogLogOut.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLogOut.setContentView(R.layout.dialog_log_out);

        Window windowLogOut = dialogLogOut.getWindow();
        windowLogOut.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        windowLogOut.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // view in dialog log out
        Button btn_Cancel_LogOut = dialogLogOut.findViewById(R.id.btn_Cancel_Logout);
        Button btn_Confirm_LogOut = dialogLogOut.findViewById(R.id.btn_Confirm_Logout);


        // Click Button Cancel
        btn_Cancel_LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLogOut.dismiss();
            }
        });

        // Click Button Confirm
        btn_Confirm_LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,SignInActivity.class));
                finishAffinity();
            }
        });
        dialogLogOut.show();
    }

    // ========= Load list user =========
    private void loadListUser() {

        ProgressDialog dialogLoadUser = new ProgressDialog(this);
        dialogLoadUser.setMessage("Đang tải dữ liệu vui lòng chờ");
        dialogLoadUser.show();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        listUser = new ArrayList<>();

        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();
                dialogLoadUser.dismiss();
                for (DataSnapshot data : snapshot.getChildren())
                {
                    User user = data.getValue(User.class);
                    if (currentUser != null && user != null && !user.getIdUser().equals(currentUser.getUid()))
                    {
                        listUser.add(user);
                        userAdapter.setDataUser(listUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // ========= Init view =========
    private void initView() {
        btnSetting      = findViewById(R.id.btn_Setting);
        recyclerUser    = findViewById(R.id.recycler_User);
    }
}
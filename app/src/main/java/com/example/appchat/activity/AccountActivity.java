package com.example.appchat.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appchat.R;
import com.example.appchat.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    private String nameUser, emailUser,statusUser;
    private String imgUser;
    private Uri imgGallery;
    private CircleImageView img_AvatarAccount, imgAvatarChangeInfo;
    private TextView txt_NameAccount, txt_EmailAccount, txt_StatusAccount;
    private ImageButton btn_BackAccount, btn_ChangeAccount;
    private StorageReference referenceStorage;

    private final ActivityResultLauncher<String> requestGallery = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {

                    if (result){
                        openGallery();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Không cho phép quyền chọn ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> chooseImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        imgGallery = result.getData().getData();
                        Glide.with(AccountActivity.this).load(imgGallery).into(imgAvatarChangeInfo);
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Init view
        initVew();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        referenceStorage = storage.getReference();
        // Get current user

        getCurrentUser();
    }



    @Override
    protected void onResume() {
        super.onResume();

        // Button Back
        btn_BackAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Button Change Info
        btn_ChangeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSettingAccount();
            }
        });

    }
    // ========= GET INFO CURRENT USER =========
    private void getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("User");
        if (user != null) {
            referenceUser.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    nameUser = Objects.requireNonNull(snapshot.child("nameUser").getValue()).toString();
                    emailUser = Objects.requireNonNull(snapshot.child("emailUser").getValue()).toString();
                    imgUser = Objects.requireNonNull(snapshot.child("imgUser").getValue()).toString();
                    statusUser = Objects.requireNonNull(snapshot.child("statusUser").getValue()).toString();

                    Glide.with(AccountActivity.this).load(imgUser).into(img_AvatarAccount);
                    txt_NameAccount.setText(nameUser);
                    txt_EmailAccount.setText(emailUser);
                    txt_StatusAccount.setText(statusUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    // ========= SHOW DIALOG SETTING ACCOUNT =========
    private void showDialogSettingAccount() {

        Dialog dialogSettingAccount = new Dialog(this);
        dialogSettingAccount.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSettingAccount.setContentView(R.layout.dialog_account);

        Window windowAccount = dialogSettingAccount.getWindow();
        windowAccount.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        windowAccount.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        windowAccount.setWindowAnimations(R.style.DialogAnimation);
        windowAccount.setGravity(Gravity.BOTTOM);

        // init view in dialog
        Button btn_Change_Info = dialogSettingAccount.findViewById(R.id.btn_Change_Info);
        Button btn_Change_Password = dialogSettingAccount.findViewById(R.id.btn_Change_Password);
        Button btn_Account_Cancel = dialogSettingAccount.findViewById(R.id.btn_Account_Cancel);


        // Button Change Info
        btn_Change_Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChangeInfo(dialogSettingAccount);
            }
        });

        // Button Change Password
        btn_Change_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChangePassword(dialogSettingAccount);
            }
        });

        // Button Cancel
        btn_Account_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSettingAccount.dismiss();
            }
        });

        dialogSettingAccount.show();
    }

    // ========= SHOW DIALOG CHANGE PASSWORD =========
    private void showDialogChangePassword(Dialog dialogSettingAccount) {
        Dialog dialogChangePass = new Dialog(this);
        dialogChangePass.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChangePass.setContentView(R.layout.dialog_change_password);
        dialogChangePass.setCanceledOnTouchOutside(false);


        Window windowAccount = dialogChangePass.getWindow();
        windowAccount.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        windowAccount.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        windowAccount.setWindowAnimations(R.style.DialogAnimation);

        // init view in dialog
        Button btn_ChangePass_Confirm           = dialogChangePass.findViewById(R.id.btn_ChangePass_Confirm);
        Button btn_ChangePass_Cancel            = dialogChangePass.findViewById(R.id.btn_ChangePass_Cancel);
        TextInputLayout layoutEdt_NewPass       = dialogChangePass.findViewById(R.id.layoutEdt_Password_New);
        TextInputLayout layoutEdt_ConfirmPass   = dialogChangePass.findViewById(R.id.layoutEdt_Password_Confirm);
        TextInputEditText edt_Password_New      = dialogChangePass.findViewById(R.id.edt_Password_New);
        TextInputEditText edt_Password_Confirm  = dialogChangePass.findViewById(R.id.edt_Password_Confirm);

        //Check values edit text
        checkEmptyEditText(layoutEdt_NewPass,edt_Password_New);
        checkEmptyEditText(layoutEdt_ConfirmPass,edt_Password_Confirm);

        // Button Confirm
        btn_ChangePass_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passNew = Objects.requireNonNull(edt_Password_New.getText()).toString().trim();
                String passConfirm = Objects.requireNonNull(edt_Password_Confirm.getText()).toString().trim();
                changePassWord(passNew,passConfirm,dialogChangePass);
                dialogSettingAccount.dismiss();
            }
        });

        // Button cancel
        btn_ChangePass_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChangePass.dismiss();
            }
        });


        dialogChangePass.show();
    }

    // ========= CHANGE PASSWORD =========
    private void changePassWord(String passNew, String passConfirm, Dialog dialogChangePass) {

        if (passNew.isEmpty() || passConfirm.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Không được bỏ trống dữ liệu", Toast.LENGTH_SHORT).show();
        }
        else if (!passNew.equals(passConfirm))
        {
            Toast.makeText(getApplicationContext(), "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ProgressDialog progressDialogPass = new ProgressDialog(this);
            progressDialogPass.setMessage("Đang cập nhật dữ liệu vui lòng chờ");
            progressDialogPass.show();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.updatePassword(passNew)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialogPass.dismiss();
                                dialogChangePass.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        }


    }

    // ========= CHECK EMPTY EDIT TEXT =========
    private void checkEmptyEditText(TextInputLayout layout, TextInputEditText edt){

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                {
                    layout.setError("Không được bỏ trống");
                    edt.setFocusable(true);
                }
                else {
                    layout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    // ========= SHOW DIALOG CHANGE INFO =========
    private void showDialogChangeInfo(Dialog dialogSettingAccount) {

        Dialog dialogChangeInfo = new Dialog(this);
        dialogChangeInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChangeInfo.setContentView(R.layout.dialog_change_info);
        dialogChangeInfo.setCanceledOnTouchOutside(false);

        Window windowChangeInfo = dialogChangeInfo.getWindow();
        windowChangeInfo.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        windowChangeInfo.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        // init view
        imgAvatarChangeInfo = dialogChangeInfo.findViewById(R.id.img_Avatar_ChangeInfo);
        TextInputLayout layout_Name_ChangeInfo = dialogChangeInfo.findViewById(R.id.layoutEdt_Name_ChangeInfo);
        TextInputLayout layout_Status_ChangeInfo = dialogChangeInfo.findViewById(R.id.layoutEdt_Status_ChangeInfo);
        TextInputEditText edt_Name_ChangeInfo = dialogChangeInfo.findViewById(R.id.edt_Name_ChangeInfo);
        TextInputEditText edt_Status_ChangeInfo = dialogChangeInfo.findViewById(R.id.edt_Status_ChangeInfo);
        Button btn_ChangeInfo_Cancel = dialogChangeInfo.findViewById(R.id.btn_ChangeInfo_Cancel);
        Button btn_ChangeInfo_Confirm = dialogChangeInfo.findViewById(R.id.btn_ChangeInfo_Confirm);

        // Check Error Edit Text
        checkEmptyEditText(layout_Name_ChangeInfo,edt_Name_ChangeInfo);
        checkEmptyEditText(layout_Status_ChangeInfo,edt_Status_ChangeInfo);

        // Set original data
        Glide.with(this).load(imgUser).into(imgAvatarChangeInfo);
        edt_Name_ChangeInfo.setText(nameUser);
        edt_Status_ChangeInfo.setText(statusUser);

        // Choose Image
        imgAvatarChangeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionGallery();
            }
        });

        // Button Cancel
        btn_ChangeInfo_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChangeInfo.dismiss();
            }
        });

        // Button Confirm
        btn_ChangeInfo_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nName = Objects.requireNonNull(edt_Name_ChangeInfo.getText()).toString().trim();
                String nStatus = Objects.requireNonNull(edt_Status_ChangeInfo.getText()).toString().trim();
                updateProfile(nName,nStatus,dialogChangeInfo);
            }
        });
        dialogChangeInfo.show();
    }

    // ========= UPDATE PROFILE USER =========
    private void updateProfile(String name, String status, Dialog dialogChangeInfo) {

        ProgressDialog progressUpdate = new ProgressDialog(this);
        progressUpdate.setMessage("Đang cập nhật vui lòng chờ");
        progressUpdate.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Update Image Storage
        StorageReference referenceStorage_User = referenceStorage.child("imgUser/" + user.getUid());
        if (imgGallery == null)
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
            databaseReference.child(user.getUid()).child("statusUser").setValue(status);
            databaseReference.child(user.getUid()).child("nameUser").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressUpdate.dismiss();
                    dialogChangeInfo.dismiss();
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else {
            referenceStorage_User.putFile(imgGallery).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        referenceStorage_User.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progressUpdate.dismiss();
                                // Update database
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
                                databaseReference.child(user.getUid()).child("imgUser").setValue(uri.toString());
                                databaseReference.child(user.getUid()).child("nameUser").setValue(name);
                                databaseReference.child(user.getUid()).child("statusUser").setValue(status);
                                Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });

        }
    }

    // ========= REQUEST PERMISSION GALLERY =========
    private void requestPermissionGallery() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            openGallery();
        }
        else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
        {

            new AlertDialog.Builder(AccountActivity.this)
                    .setMessage("Cần cấp quyền để chọn ảnh từ thư viện")
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestGallery.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    })
                    .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }else {
            requestGallery.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    // ========= OPEN GALLERY =========
    private void openGallery() {
        Intent intentGallery = new Intent();
        intentGallery.setType("image/*");
        intentGallery.setAction(Intent.ACTION_GET_CONTENT);
        chooseImage.launch(intentGallery);
    }

    // ========= INIT VIEW =========
    private void initVew() {
        img_AvatarAccount   = findViewById(R.id.img_Avatar_Account);
        txt_NameAccount     = findViewById(R.id.txt_NameAccount);
        txt_EmailAccount    = findViewById(R.id.txt_EmailAccount);
        txt_StatusAccount    = findViewById(R.id.txt_StatusAccount);
        btn_BackAccount     = findViewById(R.id.btn_Back_Account);
        btn_ChangeAccount   = findViewById(R.id.btn_Change_Account);

    }

}
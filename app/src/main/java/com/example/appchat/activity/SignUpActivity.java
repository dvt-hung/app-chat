package com.example.appchat.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.appchat.R;
import com.example.appchat.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout layoutEdt_Name,layoutEdt_Email,layoutEdt_Pass,layoutEdt_ConfirmPass;
    private TextInputEditText edt_Name_SignUp,edt_Email_SignUp,edt_Pass_SignUp,edt_ConfirmPass_SignUp;
    private Button btnSignUp;
    private FirebaseAuth auth;
    private final String imgUser = "https://firebasestorage.googleapis.com/v0/b/astronautchat.appspot.com/o/logo.jpg?alt=media&token=cb1c123c-ef73-4d20-945f-ca05613daee3";
    private final String statusUser = "Hello, i'm newbie";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        
        // init view 
        initView();
        auth = FirebaseAuth.getInstance();
        // check empty edit text
        checkEmptyEditText(layoutEdt_Name,edt_Name_SignUp);
        checkEmptyEditText(layoutEdt_Email,edt_Email_SignUp);
        checkEmptyEditText(layoutEdt_Pass,edt_Pass_SignUp);
        checkEmptyEditText(layoutEdt_ConfirmPass,edt_ConfirmPass_SignUp);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Button Sign Up
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }
    // ========= Create Account =========
    private void createAccount() {
        String name = Objects.requireNonNull(edt_Name_SignUp.getText()).toString().trim();
        String email = Objects.requireNonNull(edt_Email_SignUp.getText()).toString().trim();
        String pass = Objects.requireNonNull(edt_Pass_SignUp.getText()).toString().trim();
        String confirmPass = Objects.requireNonNull(edt_ConfirmPass_SignUp.getText()).toString().trim();


        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(!email.matches(emailPattern))
        {
            Toast.makeText(SignUpActivity.this , "Không đúng định dạng Email", Toast.LENGTH_SHORT).show();
        }
        else if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()){
            Toast.makeText(SignUpActivity.this , "Không được bỏ trống dữ liệu", Toast.LENGTH_SHORT).show();
        }
        else if (!pass.equals(confirmPass)){
            Toast.makeText(SignUpActivity.this , "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
        }
        else if (pass.length() < 6) {
            Toast.makeText(SignUpActivity.this , "Mật khẩu không được dưới 6 ký tự", Toast.LENGTH_SHORT).show();
        }else {
            // progress dialog
            ProgressDialog dialogCreate = new ProgressDialog(this);
            dialogCreate.setMessage("Vui lòng chờ trong giây lát");
            dialogCreate.show();

            auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("User");
                        User user = new User(auth.getUid(),name,email,imgUser,statusUser);
                        referenceUser.child(auth.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialogCreate.dismiss();
                                if (task.isSuccessful())
                                {
                                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                    Toast.makeText(getApplicationContext(), "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            });
        }




    }




    // ========= Init view =========
    private void initView() {
        layoutEdt_Name              = findViewById(R.id.layoutEdt_Name_SignUp);
        layoutEdt_Email             = findViewById(R.id.layoutEdt_Email_SignUp);
        layoutEdt_Pass              = findViewById(R.id.layoutEdt_Password_SignUp);
        layoutEdt_ConfirmPass       = findViewById(R.id.layoutEdt_ConfirmPassword_SignUp);
        edt_Name_SignUp             = findViewById(R.id.edt_Name_SignUp);
        edt_Email_SignUp            = findViewById(R.id.edt_Email_SignUp);
        edt_Pass_SignUp             = findViewById(R.id.edt_Password_SignUp);
        edt_ConfirmPass_SignUp      = findViewById(R.id.edt_ConfirmPassword_SignUp);
        btnSignUp                   = findViewById(R.id.btn_SignUp);
    }

    // ========= Check Empty EditText =========
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
}
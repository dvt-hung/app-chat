package com.example.appchat.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    private RelativeLayout layoutCreateAccount;
    private TextInputLayout layoutEdt_Email,layoutEdt_Password;
    private TextInputEditText edt_Email_SignIn, edt_Password_SignIn;
    private Button btnSignIn;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Init View
        initView();

        // Check empty edit text
        checkEmptyEditText(layoutEdt_Email,edt_Email_SignIn);
        checkEmptyEditText(layoutEdt_Password,edt_Password_SignIn);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Layout create account
        layoutCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
            }
        });

        // Button sign in
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                String email = Objects.requireNonNull(edt_Email_SignIn.getText()).toString().trim();
                String pass = Objects.requireNonNull(edt_Password_SignIn.getText()).toString().trim();

                if (!email.matches(emailPattern))
                {
                    Toast.makeText(getApplicationContext(), "Không đúng định dạng Email", Toast.LENGTH_SHORT).show();
                }
                else if (email.isEmpty() || pass.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "không được bỏ trống dữ liệu", Toast.LENGTH_SHORT).show();
                }else {
                    signIn(email,pass);
                }


            }
        });
    }

    private void signIn(String email, String pass) {

        ProgressDialog dialogSignIn = new ProgressDialog(this);
        dialogSignIn.setMessage("Vui lòng chờ trong giây lát");
        dialogSignIn.show();
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dialogSignIn.dismiss();
                if (task.isSuccessful())
                {
                    startActivity(new Intent(SignInActivity.this,MainActivity.class));
                    finishAffinity();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Vui lòng kiểm tra Email hoặc Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // ========== Init view ==========
    private void initView() {
        auth  = FirebaseAuth.getInstance();
        layoutCreateAccount = findViewById(R.id.layoutCreateAccount);
        layoutEdt_Email = findViewById(R.id.layoutEdt_Email);
        layoutEdt_Password = findViewById(R.id.layoutEdt_Password);
        edt_Email_SignIn = findViewById(R.id.edt_Email_SignIn);
        edt_Password_SignIn = findViewById(R.id.edt_Password_SignIn);
        btnSignIn = findViewById(R.id.btn_SignIn);
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
package com.example.appchat.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class IntroActivity extends AppCompatActivity {

    TextView txtAppName,txtSlogan;
    ImageView imgSplash1,imgSplash2,imgSplash3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        imgSplash1 = findViewById(R.id.imgSplash1);
        imgSplash2 = findViewById(R.id.imgSplash2);
        imgSplash3 = findViewById(R.id.imgSplash3);
        txtAppName = findViewById(R.id.txtAppName);
        txtSlogan = findViewById(R.id.txtSlogan);

        Animation animLogo = AnimationUtils.loadAnimation(this,R.anim.splash_anim);
        Animation animTitle = AnimationUtils.loadAnimation(this,R.anim.title_splash_anim);
        imgSplash1.startAnimation(animLogo);
        imgSplash2.startAnimation(animLogo);
        imgSplash3.startAnimation(animLogo);
        txtAppName.startAnimation(animTitle);
        txtSlogan.startAnimation(animTitle);
        


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                nextActivity();
                finish();
            }
        },2300);
    }

    private void nextActivity() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null)
        {
            startActivity(new Intent(IntroActivity.this,SignInActivity.class));
        }else {
            startActivity(new Intent(IntroActivity.this,MainActivity.class));
        }
    }
}
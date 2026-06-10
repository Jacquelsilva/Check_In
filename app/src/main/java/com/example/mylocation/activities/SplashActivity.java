package com.example.mylocation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mylocation.LoginActivity;
import com.example.mylocation.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = new SessionManager(this);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (sessionManager.estaLogado()) {
                startActivity(new Intent(this, com.example.mylocation.CheckinActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 1500);
    }
}

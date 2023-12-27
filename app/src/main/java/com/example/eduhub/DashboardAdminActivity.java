package com.example.eduhub;

import android.content.Intent;
import android.os.Bundle;

import com.example.eduhub.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.eduhub.databinding.ActivityDashboardAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardAdminActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    //view binding
    private ActivityDashboardAdminBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        //handle click, logout
//        binding.logoutBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                firebaseAuth.signOut();
//                checkUser();
//            }
//        });
    }

    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            //not logged in, go to main screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else{
            //logged in, get user info
            String email = firebaseUser.getEmail();
            //binding.subTitleTv.setText(email);
        }
    }
}
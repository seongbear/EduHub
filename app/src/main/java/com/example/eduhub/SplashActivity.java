package com.example.eduhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    //firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //init firebase auth
        firebaseAuth = firebaseAuth.getInstance();
        //start main screen after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run(){
                //stuck checkUser()
                checkUser();
                //startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        },3000); //3000 means 3 secs
    }

    private void checkUser() {
        // get current user, if logged in
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        // Temporary set user to null
//         firebaseUser = null;
        startActivity(new Intent(SplashActivity.this, user_DashboardActivity.class));
        if (firebaseUser == null){
            //user not logged in
            //start main screen
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish(); //finish this activity
        } else{
            //user logged in check user type, same as done in login screen
            //check in db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //get user type
                            String userType = String.format("%s", snapshot.child("userType").getValue());
                            //check user type
                            if (userType.equals("user")){
                                //this is simple view, open user dashboard
                                startActivity(new Intent(SplashActivity.this, user_DashboardActivity.class));
                                finish();
                            } else if (userType.equals("admin")){
                                //this is admin, open admin dashboard
                                startActivity(new Intent(SplashActivity.this, DashboardAdminActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}
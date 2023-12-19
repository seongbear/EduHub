package com.example.eduhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.eduhub.databinding.ActivityUserUploadNotesBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class user_uploadNotes extends AppCompatActivity {
    EditText notesName;
    EditText notesDescription;
    Button uploadNotesBtn;
    ImageButton uploadDocumentBtn;
    AppCompatButton chooseCategoryBtn;

    //set view binding
    private ActivityUserUploadNotesBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //uri of picked pdf
    private Uri pdfUri = null;
    private static final int PDF_PICK_CODE = 1000;

    //TAG for debugging
    private static final String TAG = "ADD_PDF_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserUploadNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //handle click, go to previous activity
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        //handle click, attach pdf 
        uploadDocumentBtn = findViewById(R.id.uploadDocument);
        uploadDocumentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfPickIntent();
            }
        });

        //handle click, pick category
        chooseCategoryBtn = findViewById(R.id.chooseCategoryBtn);
        chooseCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(user_uploadNotes.this,user_CategoryAdd.class));
            }
        });
    }

    private void pdfPickIntent() {
        Log.d(TAG,"pdfPickIntent: starting pdf pick intent");
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF"),PDF_PICK_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==RESULT_OK){
            if (requestCode == PDF_PICK_CODE){
                Log.d(TAG,"onActivityresult: PDF Picked");
                pdfUri = data.getData();
                Log.d(TAG, "onActivityResult: URI "+pdfUri);
            }
        }
        else {
            Log.d(TAG,"onActivityResult: Cancelled picking pdf");
            Toast.makeText(this,"Cancelled picking pdf",Toast.LENGTH_SHORT).show();
        }
    }
}
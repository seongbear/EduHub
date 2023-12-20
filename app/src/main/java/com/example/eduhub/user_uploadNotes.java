package com.example.eduhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eduhub.databinding.ActivityUserUploadNotesBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class user_uploadNotes extends AppCompatActivity {

    ImageButton uploadDocumentBtn;
    AppCompatButton chooseCategoryBtn;
    String categoryName;
    TextView pdfName;

    //set view binding
    private ActivityUserUploadNotesBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

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

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

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
                // Start the user_CategoryAdd activity for result
                startActivityForResult(new Intent(user_uploadNotes.this, user_CategoryAdd.class), 1);
            }
        });

        //Retrieve the data from Intent
        Intent intent = getIntent();
        if(intent != null){
            categoryName = intent.getStringExtra("CATEGORY_NAME");
            //Now we can use the categoryName as needed
            if (categoryName != null){
                //Do something with the categoryName
                chooseCategoryBtn.setText(categoryName);
                chooseCategoryBtn.setTextColor(0xFF000000);
            }
        }

        //handle click, upload pdf
        binding.uploadNotesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate data;
                validateData();
            }
        });

    }

    private String tilte = "", description = "", category = "";
    private void validateData() {
        //Step 1: Validate data
        Log.d(TAG,"validateData: validating data...");

        //get data
        tilte = binding.notesNameEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();
        category = categoryName;

        //validate data
        if (TextUtils.isEmpty(tilte)){
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this,"Enter description",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(category)){
            Toast.makeText(this,"Pick category",Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null){
            Toast.makeText(this, "Pick pdf", Toast.LENGTH_SHORT).show();
        } else{
            //all data is valid, can upload now
            uploadNotesToStorage();
        }

    }

    private void uploadNotesToStorage() {
        //Step 2: Upload Notes to firebase storage
        Log.d(TAG,"uploadNotesToStorage: uploading to storage...");

        //show progress
        progressDialog.setMessage("Upload notes");
        progressDialog.show();

        //timestamp
        long timestamp = System.currentTimeMillis();
        
        //Retrieve the name of the PDF file
        pdfName = findViewById(R.id.pdfName);
        String pdf = pdfName.getText().toString().trim();
        pdf = getPDFDisplayName(pdfUri);
        
        //Set the name to the TextView
        binding.pdfName.setText(pdf);
        
        //path of notes in firebase storage
        String filePathAndName = "Notes/"+timestamp;
        //storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: notes uploaded to storage...");
                        Log.d(TAG, "onSuccess: getting note url");

                        //get note url 
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadNoteUrl = ""+uriTask.getResult();
                        
                        //upload to firebase db
                        uploadNoteInfoToDb(uploadNoteUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Note upload failed due to "+e.getMessage());
                        Toast.makeText(user_uploadNotes.this, "Note upload failed due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getPDFDisplayName(Uri pdfUri) {
        String displayName = null;

        // Try to query the file name from the content resolver
        Cursor cursor = getContentResolver().query(pdfUri, null, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                // Check if the DISPLAY_NAME column is available
                int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (displayNameIndex != -1) {
                    displayName = cursor.getString(displayNameIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // If the display name is still null, extract it from the Uri
        if (displayName == null) {
            displayName = pdfUri.getLastPathSegment();
        }

        return displayName;
    }

    private void uploadNoteInfoToDb(String uploadNoteUrl, long timestamp) {
        //Step 3: Upload notes info to firebase db
        Log.d(TAG, "uploadNoteToStorage: uploading note info to firebase db...");
        progressDialog.setMessage("Uploading note info");

        String uid = firebaseAuth.getUid();

        //setup data to upload
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("author_uid",uid);
        hashMap.put("id",""+timestamp);
        hashMap.put("title",""+tilte);
        hashMap.put("description",""+description);
        hashMap.put("category",""+categoryName);
        hashMap.put("url",""+uploadNoteUrl);
        hashMap.put("timestamp",timestamp);

        //db reference: DB > Notes
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notes");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSuccess: Successfully uploaded...");
                        Toast.makeText(user_uploadNotes.this,"Successfully uploaded",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Failed to upload to db due to "+e.getMessage());
                        Toast.makeText(user_uploadNotes.this,"Failed to upload to db due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent");
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Log.d(TAG, "onActivityResult: PDF Picked");
            pdfUri = data.getData();
            Log.d(TAG, "onActivityResult: URI " + pdfUri);
        } else {
            Log.d(TAG, "onActivityResult: Cancelled picking pdf");
            Toast.makeText(this, "Cancelled picking pdf", Toast.LENGTH_SHORT).show();
        }
    }
}

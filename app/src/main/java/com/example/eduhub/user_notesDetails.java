package com.example.eduhub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class user_notesDetails extends AppCompatActivity {
    private String title, description, authorName, dateUploaded, categoryName, size;
    private int views, downloads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notes_details);

        //Retrieve the noteID from the intent
        String noteId = getIntent().getStringExtra("noteId");
        loadNoteDetails(noteId);
        Toast.makeText(this, "noteID: "+noteId, Toast.LENGTH_SHORT).show();
    }

    private void loadNoteDetails(String noteId) {
        Log.d("Note details", "Receiver noteId: "+noteId);
    }
}
package com.example.eduhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class user_notesDetails extends AppCompatActivity {
    private String title, description, authorName, dateUploaded, categoryName, size;
    private int views, downloads;
    TextView noteTitle, noteDescription, noteCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notes_details);

        //Retrieve the noteID from the intent
        String noteId = getIntent().getStringExtra("noteId");
        loadNoteDetails(noteId);
        Toast.makeText(this, "noteID: "+noteId, Toast.LENGTH_SHORT).show();

        noteTitle = findViewById(R.id.titleTv);
        noteDescription = findViewById(R.id.noteDescription);
        noteCategory = findViewById(R.id.categoryName);
    }

    private void loadNoteDetails(String noteId) {
        Log.d("Note details", "Receiver noteId: "+noteId);

        DatabaseReference noteRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(noteId);
        noteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //Note exists, retrieve its data
                    title= snapshot.child("title").getValue(String.class);
                    description = snapshot.child("description").getValue(String.class);
                    categoryName = snapshot.child("category").getValue(String.class);

                    //Set the title after data is loaded
                    noteTitle.setText(title);
                    noteDescription.setText(description);
                    noteCategory.setText(categoryName);
                }else{
                    //Note with the given ID does not exist
                    Log.d("Note details", "Note with ID  "+noteId+" does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Handle errors if any
                Log.e("Note details", "Error loading note details: "+error.getMessage());
            }
        });
    }
}
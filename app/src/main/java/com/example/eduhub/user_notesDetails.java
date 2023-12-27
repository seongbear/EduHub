package com.example.eduhub;

import static com.example.eduhub.Constants.MAX_BYTES_PDF;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

public class user_notesDetails extends AppCompatActivity {
    private String title, description, authorName, dateUploaded, categoryName, size, authorID, url;
    private int views, downloads;
    private long timestamp;
    TextView noteTitle, noteDescription, noteCategory, noteDate, author,sizeTv, numberOfViews, numberOfDownloads;
    PDFView noteImg;

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
        noteDate = findViewById(R.id.date);
        author = findViewById(R.id.authorName);
        sizeTv = findViewById(R.id.size);
        noteImg = findViewById(R.id.pdfView);
        numberOfDownloads = findViewById(R.id.numberOfDownloads);
        numberOfViews = findViewById(R.id.numberOfViews);
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
                    //date
                    timestamp = snapshot.child("timestamp").getValue(Long.class);
                    dateUploaded = MyApplication.formatTimestamp(timestamp);
                    //author
                    authorID= snapshot.child("author_uid").getValue(String.class);
                    loadAuthor(authorID);
                    //Pdf url
                    url = snapshot.child("url").getValue(String.class);
                    loadPdfSize(url);
                    loadPdfUrl(url);
//                    //number of views and downloads
                    Integer views = snapshot.child("Views").getValue(Integer.class);
                    Integer downloads = snapshot.child("Download").getValue(Integer.class);

                    // Convert integer values to strings with null checks
                    String viewsString = views != null ? String.valueOf(views) : "0";
                    String downloadsString = downloads != null ? String.valueOf(downloads) : "0";

                    // Set the values to TextViews
                    numberOfDownloads.setText(downloadsString);
                    numberOfViews.setText(viewsString);

                    //Set the title after data is loaded
                    noteTitle.setText(title);
                    noteDescription.setText(description);
                    noteCategory.setText(categoryName);
                    noteDate.setText(dateUploaded);
//                    numberOfDownloads.setText(downloads);
//                    numberOfViews.setText(views);
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

    private void loadPdfUrl(String url) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d("Picture", "onSuccess: "+title+" successfully got the file");
                        //set to pdfView
                        noteImg.fromBytes(bytes)
                                .pages(0) //show only the first page
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Log.d("Picture", "onError: "+t.getMessage());
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Log.d("Picture", "onPageError: "+t.getMessage());
                                    }
                                })
                                .load();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.d(TAG, "onFailure: failed getting file from url due to "+e.getMessage());
                    }
                });
    }

    private void loadPdfSize(String url) {
        //using url we can get file and its metadata from firebase storage
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        //get size in bytes
                        double bytes = storageMetadata.getSizeBytes();
                        //Log.d(TAG, "onSuccess: "+note.getTitle()+ " "+bytes);
                        //convert bytes to KB, MB
                        double kb = bytes/1024;
                        double mb = kb/1024;
                        if (mb>=1){
                            sizeTv.setText(String.format("%.2f",mb)+" MB");
                        } else if (kb>=1) {
                            sizeTv.setText(String.format("%.2f",kb)+" KB");
                        } else{
                            sizeTv.setText(String.format("%.2f",bytes)+" bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed getting metadata
                        Toast.makeText(user_notesDetails.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAuthor(String authorID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(authorID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get author Name
                        authorName = ""+snapshot.child("name").getValue();
                        //set to author text view
                        author.setText(authorName);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Unable to load author name due to ",error.getMessage());
                    }
                });
    }
}
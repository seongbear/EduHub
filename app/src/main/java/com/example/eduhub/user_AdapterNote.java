package com.example.eduhub;

import static com.example.eduhub.Constants.MAX_BYTES_PDF;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.databinding.RowNotesBinding;
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

import java.util.ArrayList;
import java.util.List;

public class user_AdapterNote extends RecyclerView.Adapter<user_AdapterNote.ViewHolder> {
    private Context context;
    private List<user_modelPdf> noteList;
    private static final String TAG = "PDF_ADAPTER_TAG";

    public user_AdapterNote(Context context, List<user_modelPdf> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    public void setNoteList(List<user_modelPdf> noteList) {
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull user_AdapterNote.ViewHolder holder, int position) {
        user_modelPdf note = noteList.get(position);
        holder.noteTitle.setText(note.getTitle());
        holder.noteDescription.setText(note.getDescription());
        holder.noteCategory.setText(note.getCategory());

        long timestamp = note.getTimestamp();
        String formattedDate = MyApplication.formatTimestamp(timestamp);
        holder.timestamp.setText(formattedDate);

        //load further details like author, pdf from url, pdf size in seperate functions
        loadPdfUrl(note, holder);
        loadPdfSize(note, holder);
        loadAuthor(note, holder);
    }

    private void loadAuthor(user_modelPdf note, ViewHolder holder) {
        //get author using authorUid
        String author_uid = note.getAuthor_uid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(author_uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get author Name
                        String authorName = ""+snapshot.child("name").getValue();
                        //set to author text view
                        holder.noteAuthor.setText(authorName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadPdfSize(user_modelPdf note, ViewHolder holder) {
        //using url we can get file and its metadata from firebase storage
        String pdfUrl = note.getUrl();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        //get size in bytes
                        double bytes = storageMetadata.getSizeBytes();
                        Log.d(TAG, "onSuccess: "+note.getTitle()+ " "+bytes);
                        //convert bytes to KB, MB
                        double kb = bytes/1024;
                        double mb = kb/1024;
                        if (mb>=1){
                            holder.noteSize.setText(String.format("%.2f",mb)+" MB");
                        } else if (kb>=1) {
                            holder.noteSize.setText(String.format("%.2f",kb)+" KB");
                        } else{
                            holder.noteSize.setText(String.format("%.2f",bytes)+" bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed getting metadata
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadPdfUrl(user_modelPdf note, ViewHolder holder) {
        //using url we can get file and its metadata from firebase storage
        String pdfUrl = note.getUrl();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG, "onSuccess: "+note.getTitle()+" successfully got the file");
                        //set to pdfView
                        holder.pdfView.fromBytes(bytes)
                                .pages(0) //show only the first page
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Log.d(TAG, "onError: "+t.getMessage());
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Log.d(TAG, "onPageError: "+t.getMessage());
                                    }
                                })
                                .load();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failed getting file from url due to "+e.getMessage());
                    }
                });

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView noteTitle, noteDescription, noteCategory;
        private TextView timestamp, noteSize, noteAuthor;
        private PDFView pdfView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titleTv);
            noteDescription = itemView.findViewById(R.id.descriptionTv);
            noteCategory = itemView.findViewById(R.id.categoryTv);
            timestamp = itemView.findViewById(R.id.dateTv);
            noteSize = itemView.findViewById(R.id.sizeTv);
            pdfView = itemView.findViewById(R.id.pdfView);
            noteAuthor = itemView.findViewById(R.id.authorTv);
        }
    }
}
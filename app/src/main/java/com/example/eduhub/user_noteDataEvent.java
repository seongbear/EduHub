package com.example.eduhub;

import android.net.Uri;

public class user_noteDataEvent {
    private String title;
    private String description;
    private Uri pdfUri;
    public user_noteDataEvent(String title,String description, Uri pdfUri) {
        this.title = title;
        this.description = description;
        this.pdfUri = pdfUri;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public Uri getPdfUri(){
        return pdfUri;
    }
}

package com.example.eduhub;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class user_UploadNotesViewModel extends ViewModel {
    private MutableLiveData<String> titleLiveData = new MutableLiveData<>();
    private MutableLiveData<String> descriptionLiveDate = new MutableLiveData<>();
    private MutableLiveData<String> categoryLiveData = new MutableLiveData<>();
    private MutableLiveData<String> categoryIdLiveDate = new MutableLiveData<>();
    private MutableLiveData<Uri> pdfUriLiveData = new MutableLiveData<>();

    public void setTitleLiveData(String titleLiveData){
        this.titleLiveData.setValue(titleLiveData);
    }

    public MutableLiveData<String> getTitleLiveData(){
        return titleLiveData;
    }

    public void setDescriptionLiveDate(String descriptionLiveDate) {
        this.descriptionLiveDate.setValue(descriptionLiveDate);
    }

    public MutableLiveData<String> getDescriptionLiveData() {
        return descriptionLiveDate;
    }

    public void setCategoryLiveData(String categoryLiveData) {
        this.categoryLiveData.setValue(categoryLiveData);
    }

    public MutableLiveData<String> getCategoryLiveData() {
        return categoryLiveData;
    }

    public void setCategoryIdLiveData(String categoryIdLiveDate) {
        this.categoryIdLiveDate.setValue(categoryIdLiveDate);
    }

    public MutableLiveData<String> getCategoryIdLiveData() {
        return categoryIdLiveDate;
    }

    public void setPdfUriLiveData(MutableLiveData<Uri> pdfUriLiveData) {
        this.pdfUriLiveData = pdfUriLiveData;
    }

    public MutableLiveData<Uri> getPdfUriLiveData() {
        return pdfUriLiveData;
    }
}

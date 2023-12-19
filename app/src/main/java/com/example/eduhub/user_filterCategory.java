package com.example.eduhub;

import java.util.ArrayList;
import java.util.Locale;

import android.widget.Filter;
public class user_filterCategory extends Filter {
    // ArrayList in which we want to search
    ArrayList<user_ModelCategory> filterList;
    // Adapter in which filter needs to be implemented
    user_AdapterCategory adapterCategory;

    // Constructor
    public user_filterCategory(ArrayList<user_ModelCategory> filterList, user_AdapterCategory adapterCategory) {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        // Value should not be null
        if (constraint != null && constraint.length() > 0) {
            // Change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<user_ModelCategory> filteredModels = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                // Validate
                if (filterList.get(i).getCategory().toUpperCase().contains(constraint)) {
                    // Add to filtered list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
            }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        // Clear the existing items in the adapter's categoryArrayList
        //adapterCategory.categoryArrayList.clear();
        // Add filtered items to the categoryArrayList
        adapterCategory.categoryArrayList = (ArrayList<user_ModelCategory>) results.values;
        // Notify the adapter that the data set has changed
        adapterCategory.notifyDataSetChanged();
    }
}


package com.example.eduhub;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.eduhub.databinding.FragmentHomeBinding;

public class user_HomeFragment extends Fragment {
    String[] item = {"All", "Most downloaded", "Most views", "Most likes"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        autoCompleteTextView = view.findViewById(R.id.filter);
        adapterItem = new ArrayAdapter<>(requireContext(), R.layout.filter_list, item);
        autoCompleteTextView.setAdapter(adapterItem);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(requireContext(), "Item: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}


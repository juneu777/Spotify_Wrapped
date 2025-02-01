package com.cs2340.project2.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.cs2340.project2.MainActivity;
import com.cs2340.project2.R;
import com.cs2340.project2.SpotifyWrappedData;
import com.cs2340.project2.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView tokenTextView, codeTextView, profileTextView, testtext;
    public static HomeFragment instance;
    public ArrayList<SpotifyWrappedData> tasteEntries;
    public static String selectedTerm = "Long Term";
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        instance = this;
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        tasteEntries = new ArrayList<>();


//        // Initialize the buttons
////        final Button codeBtn = binding.codeBtn;
        final Button profileBtn = binding.profileBtn;


        final Spinner spinner = binding.spinner;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selectedTerm = parent.getItemAtPosition(position).toString();
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        profileBtn.setOnClickListener((v) -> {
            MainActivity.mainActivity.onGetUserProfileClicked();

        });

        return root;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


        @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




}
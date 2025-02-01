package com.cs2340.project2.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cs2340.project2.MainActivity;
import com.cs2340.project2.R;
import com.cs2340.project2.SpotifyWrappedData;
import com.cs2340.project2.databinding.FragmentDashboardBinding;
import com.cs2340.project2.ui.dashboard.DashboardViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//        ListView recommendedSongs= binding.recommendedSongs;
        MainActivity.mainActivity.refreshRecommendedArtists();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
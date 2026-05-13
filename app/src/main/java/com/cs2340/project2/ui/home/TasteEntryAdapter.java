package com.cs2340.project2.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cs2340.project2.MainActivity;
import com.cs2340.project2.R;
import com.cs2340.project2.SpotifyWrappedData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TasteEntryAdapter extends BaseAdapter {

    private ArrayList<SpotifyWrappedData> data;
    private Context context;

    public TasteEntryAdapter(ArrayList<SpotifyWrappedData> data, Context context) {
        this.data = data;
        this.context = context;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int pos) {
        return data.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.taste_entry, null);
        }
        SpotifyWrappedData tasteEntry = data.get(position);
        TextView artists = view.findViewById(R.id.TopArtists);
        TextView songs = view.findViewById(R.id.TopSongs);
        ImageView iv = view.findViewById(R.id.imageView);

        artists.setText(tasteEntry.topArtistString());
        songs.setText(tasteEntry.topTrackString());
        TextView termText = view.findViewById(R.id.termText);
        termText.setText(tasteEntry.getTermLength());


        MainActivity.mainActivity.runOnUiThread(() -> {
            try {
                String url = tasteEntry.getTopArtists().getJSONObject(0).getJSONArray("images").getJSONObject(0).getString("url");
                Glide.with(MainActivity.mainActivity.context).load(url).into(iv);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        WebView song1 = view.findViewById(R.id.spotifyWebView1);
        WebView song2 = view.findViewById(R.id.spotifyWebView2);
        WebView song3 = view.findViewById(R.id.spotifyWebView3);
        loadSong(song1, 0);
        loadSong(song2, 1);
        loadSong(song3, 2);


        return view;
    }

    public void loadSong(WebView webView, int index) {
        MainActivity.mainActivity.configureWebView(webView);
        WebSettings webSettings = webView.getSettings();
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });


        if (true) {
            if (!MainActivity.data.isEmpty() && MainActivity.data.get(MainActivity.data.size() - 1).getTopTracks() != null && MainActivity.data.get(MainActivity.data.size() - 1).getTopTracks().length() > 0) {
                try {
                    JSONObject firstTrack = MainActivity.data.get(MainActivity.data.size() - 1).getTopTracks().getJSONObject(index);
                    String trackId = firstTrack.getString("id");
                    MainActivity.mainActivity.loadWebViewContent(trackId, webView);
                    webView.setVisibility(View.VISIBLE);
//                    webView.isWebViewVisible = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
//                    Toast.makeText(MainActivity.this, "Top Tracks not loaded yet!", Toast.LENGTH_SHORT).show();
            }
        } else {
            webView.setVisibility(View.GONE);
//            MainActivity.isWebViewVisible = false;
        }
    }





}

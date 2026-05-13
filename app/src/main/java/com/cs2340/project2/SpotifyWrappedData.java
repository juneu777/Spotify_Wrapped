package com.cs2340.project2;

import android.util.Log;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SpotifyWrappedData implements Serializable {
    private JSONArray topArtists;
    private JSONArray topTracks;
    private List<String> trackUris;

    private String termLength;




    public SpotifyWrappedData(JSONArray topArtists, JSONArray topTracks) {
        this.topArtists = topArtists;
        this.topTracks = topTracks;
        termLength = "Medium Term";

    }

    public SpotifyWrappedData(JSONArray topArtists, JSONArray topTracks, String length) {
        this (topArtists, topTracks);
        this.termLength = length;
    }

    public SpotifyWrappedData() {
        this(null, null);
    }

    public SpotifyWrappedData(JSONArray topArtists) {
        this.topArtists = topArtists;
    }

    public String topArtistString() {
        List<String> topArtists = getTopArtistList();
        String topArtistString = "";
        for (int i = 0; i < topArtists.size(); i++) {
           topArtistString += topArtists.get(i) + '\n';
        }
        return topArtistString;
    }

    public List<String> getTopArtistList() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < topArtists.length(); i++) {
                try {
                    names.add(topArtists.getJSONObject(i).getString("name"));
//                    Log.i("LOOK HERE", topArtists.getJSONObject(i).getString("id"));
                } catch (Exception e) {
                    Log.d("JSON ERROR", e.getMessage());
                }
        }
        return names;
    }

    public String topTrackString() {
        List<String> topTracks = getTopTrackList();
        String topTrackString = "";
        for (int i = 0; i < topTracks.size(); i++) {
            topTrackString += topTracks.get(i) + '\n';
        }
        return topTrackString;
    }

    public List<String> getTopTrackList() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < topTracks.length(); i++) {
            try {
                names.add(topTracks.getJSONObject(i).getString("name"));
            } catch (Exception e) {
                Log.d("JSON ERROR", e.getMessage());
            }
        }
        return names;
    }
    public JSONArray getTopArtists() {
        return topArtists;
    }
    public void setTopArtists(JSONArray topArtists) {
        this.topArtists = topArtists;
    }

    public JSONArray getTopTracks() {
        return topTracks;
    }

    public void setTopTracks(JSONArray topTracks) {
        this.topTracks = topTracks;
    }

    public void setTrackUris(List<String> trackUris) {
        this.trackUris = trackUris;
    }

    public List<String> getTrackUris() {
        return trackUris;
    }

    public String getTermLength() {
        return termLength;
    }

    public void setTermLength(String termLength) {
        this.termLength = termLength;
    }

    public boolean isParseable() {
        return topArtists != null && topTracks != null;
    }

    public List<String> topArtistSeed() {
        List<String> seeds = new ArrayList<>();
        for (int i = 0; i < topArtists.length() && i < 5; i++) {
            try {
                seeds.add(topArtists.getJSONObject(i).getString("id"));
            } catch (Exception e) {
                Log.d("JSON ERROR", e.getMessage());
            }
        }
        return seeds;
    }
}




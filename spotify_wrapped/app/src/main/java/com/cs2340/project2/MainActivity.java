package com.cs2340.project2;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
//<<<<<<< HEAD
import android.widget.ArrayAdapter;
import android.widget.ListView;
//=======
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cs2340.project2.databinding.ActivityMainBinding;
import com.cs2340.project2.ui.home.HomeFragment;
import com.cs2340.project2.ui.home.TasteEntryAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    public static MainActivity mainActivity;
    public ListView tasteEntriesListView;
    public TasteEntryAdapter adapter;
    public ArrayAdapter dropDownAdapter;
    private ActivityMainBinding binding;
    public static final String CLIENT_ID = "f9583a9b221e429f9b61e8a278cb679d";
    public static final String REDIRECT_URI = "project2://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;
    public Context context;
    public static ArrayList<SpotifyWrappedData> data;

    public static List<String> recommendations;

    public static Map<String, Object> fireBaseData;
    public FirebaseFirestore db;
    private FirebaseAuth myAuth;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mainActivity = this;
        context = getApplicationContext();
        db = FirebaseFirestore.getInstance();
        myAuth = FirebaseAuth.getInstance();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        getToken();
        data = new ArrayList<>();
        tasteEntriesListView = findViewById(R.id.tastesList);
        adapter = new TasteEntryAdapter(data, context);
        tasteEntriesListView.setAdapter(adapter);

        List<String> categories = new ArrayList<String>();
        categories.add("Long Term");
        categories.add("Medium Term");
        categories.add("Short Term");

        dropDownAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        dropDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(MainActivity.mainActivity.dropDownAdapter);

        fireBaseData = new HashMap<>();
        retrieveFireBaseData();
    }

    public void retrieveFireBaseData() {
        DocumentReference docRef = db.collection(FirebaseAuth.getInstance().getUid()).document("data");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> temp = document.getData();
                    if (temp != null) {
                        for (int i = 0; i < temp.keySet().size(); i++) {
                            Map<String, String> curr = (Map<String, String>) temp.get(String.valueOf(i));

                            try {
                                SpotifyWrappedData currData = new SpotifyWrappedData(new JSONArray(curr.get("topArtists")), new JSONArray(curr.get("topTracks")));
                                if (curr.containsKey("termLength")) {
                                    currData.setTermLength(curr.get("termLength"));
                                }
                                data.add(0, currData);
                                runOnUiThread(() -> adapter.notifyDataSetChanged());
                            } catch (JSONException j) {
                                System.out.println(j);
                            }
                        }
                    }
                    if (temp != null)
                        fireBaseData.putAll(temp);


                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void loadWebViewContent(String trackId, WebView spotifyWebView) {
        String iframehtml = "<!DOCTYPE html>"
                + "<html>"
                + "<body>"
                + "<iframe src=\"https://open.spotify.com/embed/track/" + trackId + "?utm_source=generator\""
                + " width=\"70%\" height=\"100\" frameborder=\"0\" allowtransparency=\"true\""
                + "allowfullscreen=\"\""
                + " allow=\"autoplay; clipboard-write; encrypted-media; encrypted-media; fullscreen; picture-in-picture\">loading=\"lazy\"</iframe>"
                + "</body>"
                + "</html>";
        spotifyWebView.loadData(iframehtml, "text/html", "UTF-8");
        spotifyWebView.setBackgroundColor(Color.TRANSPARENT);
    }
    public void configureWebView(WebView spotifyWebView) {
        WebSettings webSettings = spotifyWebView.getSettings();

        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);

        webSettings.setDomStorageEnabled(true);

        spotifyWebView.setWebChromeClient(new WebChromeClient());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        spotifyWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_CODE_REQUEST_CODE, request);
    }

    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
        }
    }

    /**
     * Get user profile
     * This method will get the user profile using the token
     */
    public void onGetUserProfileClicked() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }
        WebView spotifyWebView = new WebView(context);
        SpotifyWrappedData temp = new SpotifyWrappedData();
        refreshTopArtists(temp);
        refreshTopTracks(spotifyWebView, temp);
    }

    public void update(SpotifyWrappedData d) {

        if (d.isParseable()) {
            data.add(0, d);

            // Saving the values into firestore
            Map<String, String> map = new HashMap<>();
            map.put("topArtists", d.getTopArtists().toString());
            map.put("topTracks", d.getTopTracks().toString());
            map.put("termLength", d.getTermLength());
            fireBaseData.put(String.valueOf(data.size() - 1), map);

            db.collection(FirebaseAuth.getInstance().getUid()).document("data").set(fireBaseData);

            //updating view
            runOnUiThread( () -> adapter.notifyDataSetChanged());
        }
    }

    public static String getTimeFrame() {
        if (HomeFragment.selectedTerm.equals("Long Term")) {
            return "long_term";
        } else if (HomeFragment.selectedTerm.equals("Short Term")) {
            return "short_term";
        }
        return "medium_term";
    }
    // i need to change jsonarray to individual jsonobejcts and then store them i think
    public void refreshTopArtists(SpotifyWrappedData d) {
        // Create a request to get the user's top artists
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range=" + getTimeFrame() + "&limit=5")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    d.setTopArtists(jsonObject.getJSONArray("items"));
                    d.setTermLength(HomeFragment.selectedTerm);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void refreshRecommendedArtists() {
        // Create a request to get the user's top artists
        if (data.size() == 0) {
            onGetUserProfileClicked();
        }
        List<String> artists = new ArrayList<>();
        List<String> seed = data.get(0).topArtistSeed();
        List<String> topArtists = data.get(0).getTopArtistList();
        Log.i("testing", seed.toString());
        for (int i = 0; i < 1; i++) {
            Log.i("testing", seed.get(i));
            Request request = new Request.Builder()
                    .url("https://api.spotify.com/v1/artists/" + seed.get(i) + "/related-artists")
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .build();

            cancelCall();
            mCall = mOkHttpClient.newCall(request);
            mCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("HTTP", "Failed to fetch data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        Log.i("testing", "call finished");
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        JSONArray artistsJSON = jsonObject.getJSONArray("artists");
                        for (int i = 0; i < artistsJSON.length(); i++) {
                            String name = artistsJSON.getJSONObject(i).getString("name");
                            if (!artists.contains(name) && !topArtists.contains(name)) {
                                artists.add(artistsJSON.getJSONObject(i).getString("name"));
                            }
                        }
                        recommendations = artists;
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.textcenter, R.id.textItem, recommendations);
                            ListView recommendedSongs = findViewById(R.id.recommendedSongs);
                            recommendedSongs.setAdapter(adapter);
                        });

                    } catch (JSONException e) {
                        Log.d("JSON", "Failed to parse data: " + e);
                        Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    public void refreshTopTracks(WebView spotifyWebView, SpotifyWrappedData d) {
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=short_term&limit=5")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().string());
                    d.setTopTracks(jsonObject.getJSONArray("items"));
                    update(d);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();

                    JSONArray items = null;
                    try {
                        items = jsonObject.getJSONArray("items");
                    } catch (JSONException ex) {
                        throw new RuntimeException(ex);
                    }
                    if (items.length() > 0) {
                        // Playing the first top track
                        String firstTrackId = null;
                        try {
                            firstTrackId = items.getJSONObject(0).getString("id");
                        } catch (JSONException ex) {
                            throw new RuntimeException(ex);
                        }

                        // Update the UI and load the track in WebView
                        String finalFirstTrackId = firstTrackId;
                        runOnUiThread(() -> {
                            loadTrackInWebView(finalFirstTrackId, spotifyWebView);
                        });
                    }
                }
            }
        });
    }
    private void loadTrackInWebView(String trackId, WebView spotifyWebView) {
        runOnUiThread(() -> {
            String script = "playTrack('" + trackId + "');";
            spotifyWebView.evaluateJavascript(script, null);
        });
    }



    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }

}
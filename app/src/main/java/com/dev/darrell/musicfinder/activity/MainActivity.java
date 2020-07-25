package com.dev.darrell.musicfinder.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.darrell.musicfinder.R;
import com.dev.darrell.musicfinder.adapter.TrackAdapter;
import com.dev.darrell.musicfinder.api.DeezerApiService;
import com.dev.darrell.musicfinder.model.Track;
import com.dev.darrell.musicfinder.model.TrackResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String BASE_API_URL = "https://api.deezer.com";
    public static Retrofit retrofit = null;
    private ProgressBar mPbLoading;
    private RecyclerView recyclerView = null;
    private TextView mtvError;
    private String mSearchQuery = null;

    //Used by retrofit
    private Call<TrackResponse> mcall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPbLoading = findViewById(R.id.pb_loading);
        mtvError = findViewById(R.id.tv_error);

        recyclerView = findViewById(R.id.track_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the intent, verify the action and get the query
//        Intent intent = getIntent();
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            mSearchQuery = intent.getStringExtra(SearchManager.QUERY);
//        }
//
//            connectAndGetApiData();
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    private void connectAndGetApiData () {
            mPbLoading.setVisibility(View.VISIBLE);
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }

            DeezerApiService deezerApiService = retrofit.create(DeezerApiService.class);

            if (mSearchQuery.isEmpty()) {
                mcall = deezerApiService.findTrack("The Chainsmokers");
            } else {
                mcall = deezerApiService.findTrack(mSearchQuery);
            }
            mcall.enqueue(new Callback<TrackResponse>() {
                @Override
                public void onResponse(Call<TrackResponse> call, Response<TrackResponse> response) {
                    Log.d(TAG, "onResponse: API data retrieved");
                    mPbLoading.setVisibility(View.INVISIBLE);

                    ArrayList<Track> tracks = (ArrayList<Track>) response.body().getData();
                    recyclerView.setAdapter(new TrackAdapter(tracks));
                    Log.d(TAG, "Number of songs retrieved: " + tracks.size());
                }

                @Override
                public void onFailure(Call<TrackResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: API call failed");
                    Log.e(TAG, t.toString());
                    mPbLoading.setVisibility(View.INVISIBLE);
                    mtvError.setText("An error occurred while retrieving data.");
                }
            });
        }

    }

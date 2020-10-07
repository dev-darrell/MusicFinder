package com.dev.darrell.musicfinder.activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
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
    private String mSearchQuery;

    //Used by retrofit
    private Call<TrackResponse> mcall;
    public static SearchRecentSuggestions msuggestions;
    public static ArrayList<Track> mTrackArrayList;

//    TODO: Add progressbar to show homepage load progress
//    TODO: Check if internet is available and show a message/diagram if it isn't
//    TODO: Add ability to refresh track list on homepage or show next section of track list
//    TODO: Restructure activity to have separation of concerns implemented. [UI actions, API actions, data actions separated]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPbLoading = findViewById(R.id.pb_loading);
        mtvError = findViewById(R.id.tv_error);

        recyclerView = findViewById(R.id.track_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//         Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mSearchQuery = intent.getStringExtra(SearchManager.QUERY);
            msuggestions = new SearchRecentSuggestions(this,
                    MySearchSuggestionProvider.AUTHORITY, MySearchSuggestionProvider.MODE);
            msuggestions.saveRecentQuery(mSearchQuery, null);
        }

        connectAndGetApiData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        MenuItem clearSearchHistory = menu.findItem(R.id.clr_prev_search);
        clearSearchHistory.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                ClearSearchHistory clearHistory = new ClearSearchHistory();
                clearHistory.show(fragmentManager, "clearSearch");
                return true;
            }
        });
        return true;
    }

    private void connectAndGetApiData() {
        mPbLoading.setVisibility(View.VISIBLE);
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        DeezerApiService deezerApiService = retrofit.create(DeezerApiService.class);

        if (mSearchQuery == null) {
            mcall = deezerApiService.fromTopCharts(20);
        } else {
            mcall = deezerApiService.findTrack(mSearchQuery);
        }
        mcall.enqueue(new Callback<TrackResponse>() {
            @Override
            public void onResponse(Call<TrackResponse> call, Response<TrackResponse> response) {
                Log.d(TAG, "onResponse: API data retrieved");
                mPbLoading.setVisibility(View.INVISIBLE);
                if (!response.body().getData().isEmpty()) {
                    mTrackArrayList = (ArrayList<Track>) response.body().getData();
                    recyclerView.setAdapter(new TrackAdapter(mTrackArrayList));
                    Log.d(TAG, "Number of songs retrieved: " + mTrackArrayList.size());
                } else {
                    mtvError.setText(R.string.track_not_in_database);
                }
            }

            @Override
            public void onFailure(Call<TrackResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: API call failed");
                Log.e(TAG, t.toString());
                mPbLoading.setVisibility(View.INVISIBLE);
                mtvError.setText(R.string.error_getting_data);
            }
        });
    }

    public static class ClearSearchHistory extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.clear_history_dialog_message)
                    .setPositiveButton(R.string.dialog_positive_message,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    msuggestions.clearHistory();
                                }
                            })
                    .setNegativeButton(R.string.dialog_negative_message,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d(TAG, "Dialog Negative Button: User canceled clear search request");
                                }
                            });
            return builder.create();
        }
    }
}

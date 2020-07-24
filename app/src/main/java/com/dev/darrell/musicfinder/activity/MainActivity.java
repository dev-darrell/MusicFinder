package com.dev.darrell.musicfinder.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

//import com.dev.darrell.musicfinder.api.ApiUtil;
import com.dev.darrell.musicfinder.R;
import com.dev.darrell.musicfinder.api.DeezerApiService;
import com.dev.darrell.musicfinder.model.Track;
import com.dev.darrell.musicfinder.model.TrackResponse;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String BASE_API_URL = "https://api.deezer.com";
    public static Retrofit retrofit = null;

    private TextView mTvResult;
    private ProgressBar mPbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPbLoading = findViewById(R.id.pb_loading);
        mTvResult = findViewById(R.id.tv_result);


        connectAndGetApiData();
//        String searchItem = "The Chainsmokers";
//        URL url = ApiUtil.BuildUrl(searchItem);
//        new getMusic().execute(url);
    }

    private void connectAndGetApiData() {
        mPbLoading.setVisibility(View.VISIBLE);
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        DeezerApiService deezerApiService = retrofit.create(DeezerApiService.class);

        Call<TrackResponse> call = deezerApiService.findTrack("The Chainsmokers");

        call.enqueue(new Callback<TrackResponse>() {
            @Override
            public void onResponse(Call<TrackResponse> call, Response<TrackResponse> response) {
                Log.d(TAG, "onResponse: API data retrieved");
                List<Track> tracks = response.body().getData();
                mPbLoading.setVisibility(View.INVISIBLE);
                if (tracks != null)
                    mTvResult.setText((CharSequence) tracks);
            }

            @Override
            public void onFailure(Call<TrackResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                mPbLoading.setVisibility(View.INVISIBLE);
                mTvResult.setText("An error has occurred. Check Logs");
            }
        });
    }


//    public class getMusic extends AsyncTask<URL, Void, String> {
//
//        @Override
//        protected String doInBackground(URL... urls) {
//            URL currentUrl = urls[0];
//            String result = null;
//            try {
//                result = ApiUtil.GetJson(currentUrl);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            mPbLoading.setVisibility(View.VISIBLE);
//            mTvResult.setVisibility(View.INVISIBLE);
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            TextView tvError = findViewById(R.id.tv_error);
//            mPbLoading.setVisibility(View.INVISIBLE);
//            if (result == null || result.isEmpty()) {
//                tvError.setText("Error retrieving music list from Deezer");
//                tvError.setVisibility(View.VISIBLE);
//            } else {
//                mTvResult.setText(result);
//                mTvResult.setVisibility(View.VISIBLE);
//            }
//
//            super.onPostExecute(result);
//        }
//    }
}
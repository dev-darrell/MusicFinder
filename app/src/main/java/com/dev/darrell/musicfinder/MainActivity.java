package com.dev.darrell.musicfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mTvResult;
    private ProgressBar mPbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPbLoading = findViewById(R.id.pb_loading);
        mTvResult = findViewById(R.id.tv_result);
        String searchItem = "The Chainsmokers";
        URL url = ApiUtil.BuildUrl(searchItem);
        new getMusic().execute(url);


    }

    public class getMusic extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL currentUrl = urls[0];
            String result = null;
            try {
                result = ApiUtil.GetJson(currentUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            mPbLoading.setVisibility(View.VISIBLE);
            mTvResult.setVisibility(View.INVISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            TextView tvError = findViewById(R.id.tv_error);
            mPbLoading.setVisibility(View.INVISIBLE);
            if (result == null || result.isEmpty()) {
                tvError.setText("Error retrieving music list from Deezer");
                tvError.setVisibility(View.VISIBLE);
            } else {
                mTvResult.setText(result);
                mTvResult.setVisibility(View.VISIBLE);
            }

            super.onPostExecute(result);
        }
    }
}
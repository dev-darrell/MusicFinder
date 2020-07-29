package com.dev.darrell.musicfinder.activity;

import android.content.SearchRecentSuggestionsProvider;

public class MySearchSuggestionProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "com.dev.darrell.musicfinder.activity.MySearchSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
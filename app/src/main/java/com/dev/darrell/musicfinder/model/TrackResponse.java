package com.dev.darrell.musicfinder.model;

import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.util.List;

public class TrackResponse {
    @SerializedName("data")
    private List<Track> data;

    @SerializedName("next")
    private URL nextPage;

    public TrackResponse(List<Track> data, URL nextPage) {
        this.data = data;
        this.nextPage = nextPage;
    }

    public List<Track> getData() {
        return data;
    }

    public void setData(List<Track> data) {
        this.data = data;
    }

    public URL getNextPage() {
        return nextPage;
    }

    public void setNextPage(URL nextPage) {
        this.nextPage = nextPage;
    }
}

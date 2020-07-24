package com.dev.darrell.musicfinder.model;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public class Track {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("duration")
    private int duration;
    @SerializedName("explicit_lyrics")
    private boolean explicitLyrics;
    @SerializedName("preview")
    private URL previewLink;
    @SerializedName("artist.name")
    private String artistName;
    @SerializedName("album.cover")
    private String albumCover;

    public Track(int id, String title, int duration, boolean explicitLyrics, URL previewLink,
                 String artistName, String albumCover) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.explicitLyrics = explicitLyrics;
        this.previewLink = previewLink;
        this.artistName = artistName;
        this.albumCover = albumCover;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isExplicitLyrics() {
        return explicitLyrics;
    }

    public void setExplicitLyrics(boolean explicitLyrics) {
        this.explicitLyrics = explicitLyrics;
    }

    public URL getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(URL previewLink) {
        this.previewLink = previewLink;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

}

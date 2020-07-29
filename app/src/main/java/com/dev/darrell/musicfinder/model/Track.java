package com.dev.darrell.musicfinder.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class Track implements Parcelable {
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
    @SerializedName("artist")
    private Artist artist;
    @SerializedName("album")
    private Album album;

    public Track(int id, String title, int duration, boolean explicitLyrics, URL previewLink,
                 Artist artist, Album album) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.explicitLyrics = explicitLyrics;
        this.previewLink = previewLink;
        this.artist = artist;
        this.album = album;
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            try {
                return new Track(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    protected Track(Parcel in) throws MalformedURLException {
        id = in.readInt();
        title = in.readString();
        duration = in.readInt();
        explicitLyrics = in.readByte() != 0;
        previewLink = URI.create(in.readString()).toURL();
        artist = in.readParcelable(Artist.class.getClassLoader());
        album = in.readParcelable(Album.class.getClassLoader());
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

    public String getArtist() {
        return artist.getName();
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getAlbumCover() {
        return album.getCover();
    }

    public void setAlbumCover(Album album) {
        this.album = album;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeInt(duration);
        parcel.writeByte((byte) (explicitLyrics ? 1 : 0));
        parcel.writeString(previewLink.toString());
        parcel.writeParcelable(artist, PARCELABLE_WRITE_RETURN_VALUE);
        parcel.writeParcelable(album, PARCELABLE_WRITE_RETURN_VALUE);
    }


}

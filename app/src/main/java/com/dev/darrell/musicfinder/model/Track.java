package com.dev.darrell.musicfinder.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

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

    protected Track(Parcel in) {
        id = in.readInt();
        title = in.readString();
        duration = in.readInt();
        explicitLyrics = in.readByte() != 0;
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

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
    }


    public class Album {

        @SerializedName("id")
        private Integer id;
        @SerializedName("title")
        private String title;
        @SerializedName("cover")
        private String cover;
        @SerializedName("cover_small")
        private String coverSmall;
        @SerializedName("cover_medium")
        private String coverMedium;
        @SerializedName("cover_big")
        private String coverBig;
        @SerializedName("cover_xl")
        private String coverXl;
        @SerializedName("tracklist")
        private String tracklist;
        @SerializedName("type")
        private String type;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getCoverSmall() {
            return coverSmall;
        }

        public void setCoverSmall(String coverSmall) {
            this.coverSmall = coverSmall;
        }

        public String getCoverMedium() {
            return coverMedium;
        }

        public void setCoverMedium(String coverMedium) {
            this.coverMedium = coverMedium;
        }

        public String getCoverBig() {
            return coverBig;
        }

        public void setCoverBig(String coverBig) {
            this.coverBig = coverBig;
        }

        public String getCoverXl() {
            return coverXl;
        }

        public void setCoverXl(String coverXl) {
            this.coverXl = coverXl;
        }

        public String getTracklist() {
            return tracklist;
        }

        public void setTracklist(String tracklist) {
            this.tracklist = tracklist;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }



    public class Artist {

        @SerializedName("id")
        private Integer id;
        @SerializedName("name")
        private String name;
        @SerializedName("link")
        private String link;
        @SerializedName("picture")
        private String picture;
        @SerializedName("picture_small")
        private String pictureSmall;
        @SerializedName("picture_medium")
        private String pictureMedium;
        @SerializedName("picture_big")
        private String pictureBig;
        @SerializedName("picture_xl")
        private String pictureXl;
        @SerializedName("tracklist")
        private String tracklist;
        @SerializedName("type")
        private String type;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getPictureSmall() {
            return pictureSmall;
        }

        public void setPictureSmall(String pictureSmall) {
            this.pictureSmall = pictureSmall;
        }

        public String getPictureMedium() {
            return pictureMedium;
        }

        public void setPictureMedium(String pictureMedium) {
            this.pictureMedium = pictureMedium;
        }

        public String getPictureBig() {
            return pictureBig;
        }

        public void setPictureBig(String pictureBig) {
            this.pictureBig = pictureBig;
        }

        public String getPictureXl() {
            return pictureXl;
        }

        public void setPictureXl(String pictureXl) {
            this.pictureXl = pictureXl;
        }

        public String getTracklist() {
            return tracklist;
        }

        public void setTracklist(String tracklist) {
            this.tracklist = tracklist;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}

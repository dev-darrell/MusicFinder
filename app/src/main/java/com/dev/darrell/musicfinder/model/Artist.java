package com.dev.darrell.musicfinder.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Artist implements Parcelable {

    public static final Creator<com.dev.darrell.musicfinder.model.Artist> CREATOR = new Creator<com.dev.darrell.musicfinder.model.Artist>() {
        @Override
        public com.dev.darrell.musicfinder.model.Artist createFromParcel(Parcel in) {
            return new com.dev.darrell.musicfinder.model.Artist(in);
        }

        @Override
        public com.dev.darrell.musicfinder.model.Artist[] newArray(int size) {
            return new com.dev.darrell.musicfinder.model.Artist[size];
        }
    };
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

    protected Artist(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        link = in.readString();
        picture = in.readString();
        pictureSmall = in.readString();
        pictureMedium = in.readString();
        pictureBig = in.readString();
        pictureXl = in.readString();
        tracklist = in.readString();
        type = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(name);
        parcel.writeString(link);
        parcel.writeString(picture);
        parcel.writeString(pictureSmall);
        parcel.writeString(pictureMedium);
        parcel.writeString(pictureBig);
        parcel.writeString(pictureXl);
        parcel.writeString(tracklist);
        parcel.writeString(type);
    }
}


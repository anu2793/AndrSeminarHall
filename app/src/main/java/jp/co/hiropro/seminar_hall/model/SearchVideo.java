package jp.co.hiropro.seminar_hall.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Tuấn Sơn on 3/8/2017.
 */

public class SearchVideo implements Parcelable {
    private String video, thumbnail;
    private int price, isSpecial, isBought;
    private int videoType;

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(int isSpecial) {
        this.isSpecial = isSpecial;
    }


    public int getVideoType() {
        return videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public int getIsBought() {
        return isBought;
    }

    public void setIsBought(int isBought) {
        this.isBought = isBought;
    }

    public static SearchVideo parse(JSONObject object) {
        SearchVideo item = new SearchVideo();
        item.setVideo(object.optString("movie_url"));
        item.setThumbnail(object.optString("thumbnail_url"));
        int price = object.optInt("price");
        int isSpecial = object.optInt("is_special");
        item.setVideoType(object.optInt("markType"));
        item.setIsSpecial(isSpecial);
        item.setPrice(price);
        item.setIsBought(object.optInt("is_bought"));
        return item;
    }

    public SearchVideo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.video);
        dest.writeString(this.thumbnail);
        dest.writeInt(this.price);
        dest.writeInt(this.isSpecial);
        dest.writeInt(this.videoType);
    }

    protected SearchVideo(Parcel in) {
        this.video = in.readString();
        this.thumbnail = in.readString();
        this.price = in.readInt();
        this.isSpecial = in.readInt();
        this.videoType = in.readInt();
    }

    public static final Creator<SearchVideo> CREATOR = new Creator<SearchVideo>() {
        @Override
        public SearchVideo createFromParcel(Parcel source) {
            return new SearchVideo(source);
        }

        @Override
        public SearchVideo[] newArray(int size) {
            return new SearchVideo[size];
        }
    };
}

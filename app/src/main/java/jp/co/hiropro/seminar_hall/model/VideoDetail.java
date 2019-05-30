package jp.co.hiropro.seminar_hall.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import jp.co.hiropro.seminar_hall.util.KeyParser;

/**
 * Created by Tuấn Sơn on 21/7/2017.
 */

public class VideoDetail implements Parcelable {

    public static final int VIDEO_PAID = 0;
    public static final int VIDEO_FREE = 1;
    public static final int VIDEO_FREE_TYPE_2 = 2;
    public static final int VIDEO_LIMITED_FREE = 3;
    public static final int VIDEO_SPECIAL = 4;
    public static final int VIDEO_LIMITED_SPECIAL = 5;

    private int id, type, idOwner;
    private int price, priceDiscount;
    private String image, video;
    private String title, description, date, code, duration;
    private int hasBoughtVideo;

    public VideoDetail(int type, String image) {
        this.type = type;
        this.image = image;
    }

    public VideoDetail(int type, String image, String title, String description) {
        this.type = type;
        this.image = image;
        this.title = title;
        this.description = description;
    }

    public VideoDetail() {
    }

    /*1: free; 2: fee; 3: special*/
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getHasBoughtVideo() {
        return hasBoughtVideo;
    }

    public void setHasBoughtVideo(int hasBoughtVideo) {
        this.hasBoughtVideo = hasBoughtVideo;
    }

    public int getPrice() {
        if (priceDiscount > 0) {
            return priceDiscount;
        } else
            return price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getPriceDiscount() {
        return priceDiscount;
    }

    public void setPriceDiscount(int priceDiscount) {
        this.priceDiscount = priceDiscount;
    }

    public int getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(int idOwner) {
        this.idOwner = idOwner;
    }

    public static VideoDetail parse(JSONObject object) {
        VideoDetail item = new VideoDetail();
        item.setId(object.optInt("id"));
        int price = object.optInt("price");
        item.setType(object.optInt("markType"));
        item.setTitle(object.optString("title"));
        item.setDescription(object.optString(KeyParser.KEY.DESCRIPTION.toString()));
        item.setImage(object.optString("thumbnail_url"));
        item.setPrice(price);
        item.setPriceDiscount(object.optInt("price_discount"));
        item.setCode(object.optString("price_code"));
        item.setDate(object.optString("created_at"));
        item.setVideo(object.optString("movie_url"));
        item.setDuration(object.optString("duration"));
        item.setHasBoughtVideo(object.optInt("is_bought"));
        item.setIdOwner(object.optInt("user_member_update", 0));
        return item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.type);
        dest.writeInt(this.price);
        dest.writeInt(this.priceDiscount);
        dest.writeString(this.image);
        dest.writeString(this.video);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.date);
        dest.writeString(this.code);
        dest.writeInt(this.hasBoughtVideo);
    }

    protected VideoDetail(Parcel in) {
        this.id = in.readInt();
        this.type = in.readInt();
        this.price = in.readInt();
        this.priceDiscount = in.readInt();
        this.image = in.readString();
        this.video = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.date = in.readString();
        this.code = in.readString();
        this.hasBoughtVideo = in.readInt();
    }

    public static final Creator<VideoDetail> CREATOR = new Creator<VideoDetail>() {
        @Override
        public VideoDetail createFromParcel(Parcel source) {
            return new VideoDetail(source);
        }

        @Override
        public VideoDetail[] newArray(int size) {
            return new VideoDetail[size];
        }
    };
}

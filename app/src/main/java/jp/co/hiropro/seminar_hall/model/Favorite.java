package jp.co.hiropro.seminar_hall.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import jp.co.hiropro.seminar_hall.util.KeyParser;

/**
 * Created by Tuấn Sơn on 25/7/2017.
 */

public class Favorite implements Parcelable {
    private int id, cateId, SubCateId, favorite;
    private String image, title, category, cate_title, subcate_title;


    public Favorite(String image, String title, String category) {
        this.image = image;
        this.title = title;
        this.category = category;
    }

    public Favorite() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCateTitle() {
        return cate_title;
    }

    public void setCateTitle(String cate_title) {
        this.cate_title = cate_title;
    }

    public String getSubcateTitle() {
        return subcate_title;
    }

    public void setSubcateTitle(String subcate_title) {
        this.subcate_title = subcate_title;
    }

    public int getCateId() {
        return cateId;
    }

    public void setCateId(int cateId) {
        this.cateId = cateId;
    }

    public int getSubCateId() {
        return SubCateId;
    }

    public void setSubCateId(int subCateId) {
        SubCateId = subCateId;
    }


    public boolean isFavorite() {
        return favorite == 1;
    }

    public void setFavorite(int value) {
        favorite = value;
    }

    public static Favorite parse(JSONObject object) {
        Favorite item = new Favorite();
        item.setId(object.optInt(KeyParser.KEY.ID.toString()));
        item.setTitle(object.optString(KeyParser.KEY.TITLE.toString()));
        item.setImage(object.optString(KeyParser.KEY.IMAGE.toString()));
        item.setCateTitle(object.optString(KeyParser.KEY.CATE_TITLE.toString()));
        item.setSubcateTitle(object.optString(KeyParser.KEY.SUBCATE_TITLE.toString()));
        item.setCateId(object.optInt("cate_id"));
        item.setSubCateId(object.optInt("subcate_id"));
        item.setFavorite(object.optInt("is_favorite", 0));
        return item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.cateId);
        dest.writeInt(this.SubCateId);
        dest.writeString(this.image);
        dest.writeString(this.title);
        dest.writeString(this.category);
        dest.writeString(this.cate_title);
        dest.writeString(this.subcate_title);
        dest.writeInt(this.favorite);
    }

    protected Favorite(Parcel in) {
        this.id = in.readInt();
        this.cateId = in.readInt();
        this.SubCateId = in.readInt();
        this.image = in.readString();
        this.title = in.readString();
        this.category = in.readString();
        this.cate_title = in.readString();
        this.subcate_title = in.readString();
        this.favorite = in.readInt();
    }

    public static final Creator<Favorite> CREATOR = new Creator<Favorite>() {
        @Override
        public Favorite createFromParcel(Parcel source) {
            return new Favorite(source);
        }

        @Override
        public Favorite[] newArray(int size) {
            return new Favorite[size];
        }
    };
}

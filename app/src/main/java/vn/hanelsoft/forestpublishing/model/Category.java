package vn.hanelsoft.forestpublishing.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import vn.hanelsoft.forestpublishing.util.KeyParser;

/**
 * Created by Tuấn Sơn on 18/7/2017.
 */

public class Category implements Parcelable {
    int id;
    String image, nameJp, nameEng, description;

    public Category(String nameJp, String nameEng) {
        this.nameJp = nameJp;
        this.nameEng = nameEng;
    }

    public Category() {
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

    public String getNameJp() {
        return nameJp;
    }

    public void setNameJp(String nameJp) {
        this.nameJp = nameJp;
    }

    public String getNameEng() {
        return nameEng;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Category parse(JSONObject object) {
        Category category = new Category();
        category.setImage(object.optString(KeyParser.KEY.IMAGE.toString()));
        category.setNameEng(object.optString("name_en"));
        category.setNameJp(object.optString("name_jp"));
        category.setDescription(object.optString(KeyParser.KEY.DESCRIPTION.toString()));
        category.setId(object.optInt(KeyParser.KEY.ID.toString()));
        return category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.image);
        dest.writeString(this.nameJp);
        dest.writeString(this.nameEng);
        dest.writeString(this.description);
    }

    protected Category(Parcel in) {
        this.id = in.readInt();
        this.image = in.readString();
        this.nameJp = in.readString();
        this.nameEng = in.readString();
        this.description = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}

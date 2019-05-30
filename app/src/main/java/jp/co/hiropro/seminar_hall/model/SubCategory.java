package jp.co.hiropro.seminar_hall.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import jp.co.hiropro.seminar_hall.util.KeyParser;

/**
 * Created by Tuấn Sơn on 24/7/2017.
 */

public class SubCategory implements Parcelable {
    int id;
    String image, description, title, titleJp;
    int isFavorite;

    public SubCategory() {
    }

    public SubCategory(String image, String description, int isFavorite) {
        this.image = image;
        this.description = description;
        this.isFavorite = isFavorite;
    }

    public SubCategory(String image, String description, String title, int isFavorite) {
        this.image = image;
        this.description = description;
        this.isFavorite = isFavorite;
        this.title = title;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleJp() {
        return titleJp;
    }

    public void setTitleJp(String titleJp) {
        this.titleJp = titleJp;
    }

    public static SubCategory parse(JSONObject object) {
        SubCategory item = new SubCategory();
        item.setId(object.optInt(KeyParser.KEY.ID.toString()));
        item.setImage(object.optString(KeyParser.KEY.IMAGE.toString()));
        item.setDescription(object.optString(KeyParser.KEY.DESCRIPTION.toString()));
        item.setIsFavorite(object.optInt("is_favorite"));
        item.setTitle(object.optString(KeyParser.KEY.TITLE.toString()));
        return item;
    }

    public static SubCategory parserJson(JSONObject object) {
        SubCategory category = new SubCategory();
        category.setId(object.optInt(KeyParser.KEY.ID.toString()));
        category.setImage(object.optString(KeyParser.KEY.IMAGE.toString()));
        category.setTitle(object.optString("name_en"));
        category.setTitleJp(object.optString("name_jp"));
        category.setIsFavorite(object.optInt("is_favorite"));
        category.setDescription(object.optString(KeyParser.KEY.DESCRIPTION.toString()));
        category.setId(object.optInt(KeyParser.KEY.ID.toString()));
        category.setTitle(category.getTitleJp().length() > 0 ? category.getTitleJp() : category.getTitle());
        return category;
    }

    public static SubCategory parserDataSub(JSONObject object) {
        SubCategory category = new SubCategory();
        category.setId(object.optInt(KeyParser.KEY.ID.toString()));
        category.setImage(object.optString(KeyParser.KEY.IMAGE.toString()));
        category.setTitle(object.optString("name_en"));
        category.setTitleJp(object.optString("name_jp"));
        category.setIsFavorite(object.optInt("is_favorite"));
        category.setDescription(object.optString(KeyParser.KEY.DESCRIPTION.toString()));
        category.setId(object.optInt(KeyParser.KEY.ID.toString()));
        category.setTitle(object.optString(KeyParser.KEY.TITLE.toString()));
//        category.setTitle(category.getTitleJp().length() > 0 ? category.getTitleJp() : category.getTitle());
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
        dest.writeString(this.description);
        dest.writeInt(this.isFavorite);
        dest.writeString(this.title);
        dest.writeString(this.titleJp);
    }

    protected SubCategory(Parcel in) {
        this.id = in.readInt();
        this.image = in.readString();
        this.description = in.readString();
        this.isFavorite = in.readInt();
        this.title = in.readString();
        this.titleJp = in.readString();
    }

    public static final Parcelable.Creator<SubCategory> CREATOR = new Parcelable.Creator<SubCategory>() {
        @Override
        public SubCategory createFromParcel(Parcel source) {
            return new SubCategory(source);
        }

        @Override
        public SubCategory[] newArray(int size) {
            return new SubCategory[size];
        }
    };
}

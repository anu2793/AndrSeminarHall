package jp.co.hiropro.seminar_hall.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import jp.co.hiropro.seminar_hall.util.KeyParser;

/**
 * Created by Tuấn Sơn on 3/8/2017.
 */

public class SearchSubCategory implements Parcelable {
    String image, category, subCategory, book;
    int isFavorite, cateId, subCateId, bookId;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public int getCateId() {
        return cateId;
    }

    public void setCateId(int cateId) {
        this.cateId = cateId;
    }

    public int getSubCateId() {
        return subCateId;
    }

    public void setSubCateId(int subCateId) {
        this.subCateId = subCateId;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public static SearchSubCategory parse(JSONObject object) {
        SearchSubCategory item = new SearchSubCategory();
        item.setImage(object.optString(KeyParser.KEY.IMAGE.toString()));
        // category
        String cateTitle = object.optString("cate_title", "");
        String cateName = object.optString("cate_name", "");
        item.setCategory(cateTitle.length() > 0 ? cateTitle : cateName);
        // subcate
        String subcate = object.optString("subcate_title", "");
        String subtitle = object.optString("sub_title", "");
//        item.setSubCategory(object.optString("subcate_title"));
        item.setSubCategory(subcate.length() > 0 ? subcate : subtitle);
//        item.setCategory(object.optString("cate_title"));


        item.setCateId(object.optInt("cate_id"));
        item.setSubCateId(object.optInt("subcate_id"));
        item.setBook(object.optString("book_title"));
        item.setBookId(object.optInt("book_id"));
        item.setIsFavorite(object.optInt("is_favorite"));
        return item;
    }

    public SearchSubCategory() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image);
        dest.writeString(this.category);
        dest.writeString(this.subCategory);
        dest.writeInt(this.isFavorite);
        dest.writeInt(this.cateId);
        dest.writeInt(this.subCateId);
    }

    protected SearchSubCategory(Parcel in) {
        this.image = in.readString();
        this.category = in.readString();
        this.subCategory = in.readString();
        this.isFavorite = in.readInt();
        this.cateId = in.readInt();
        this.subCateId = in.readInt();
    }

    public static final Creator<SearchSubCategory> CREATOR = new Creator<SearchSubCategory>() {
        @Override
        public SearchSubCategory createFromParcel(Parcel source) {
            return new SearchSubCategory(source);
        }

        @Override
        public SearchSubCategory[] newArray(int size) {
            return new SearchSubCategory[size];
        }
    };
}

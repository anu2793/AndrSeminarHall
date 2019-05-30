package jp.co.hiropro.seminar_hall.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import jp.co.hiropro.seminar_hall.util.AppConstants;

/**
 * Created by dinhdv on 1/26/2018.
 */

public class CateSeminarObject implements Parcelable {
    String id;
    String name;
    String color;
    String name_en;
    String description;
    String image;

    public CateSeminarObject() {
    }

    public CateSeminarObject(String id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    protected CateSeminarObject(Parcel in) {
        id = in.readString();
        name = in.readString();
        color = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public static final Creator<CateSeminarObject> CREATOR = new Creator<CateSeminarObject>() {
        @Override
        public CateSeminarObject createFromParcel(Parcel in) {
            return new CateSeminarObject(in);
        }

        @Override
        public CateSeminarObject[] newArray(int size) {
            return new CateSeminarObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(color);
    }

    public static CateSeminarObject parserData(JSONObject object) {
        CateSeminarObject result = new CateSeminarObject();
        result.setId(object.optString(AppConstants.KEY_PARAMS.ID.toString()));
        result.setName(object.optString(AppConstants.KEY_PARAMS.NAME_JP.toString()));
        String color = object.optString(AppConstants.KEY_PARAMS.COLOR.toString());
        result.setColor(color.length() > 0 ? "#" + color : color);
        return result;
    }
}

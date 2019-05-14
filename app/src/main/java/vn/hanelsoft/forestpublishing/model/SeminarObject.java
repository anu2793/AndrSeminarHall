package vn.hanelsoft.forestpublishing.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by dinhdv on 1/22/2018.
 */

public class SeminarObject implements Parcelable {
    String id;
    String link;
    String name;

    public SeminarObject() {
    }

    public SeminarObject(String values) {
        this.link = values;
    }

    protected SeminarObject(Parcel in) {
        id = in.readString();
        link = in.readString();
        name = in.readString();
    }

    public static final Creator<SeminarObject> CREATOR = new Creator<SeminarObject>() {
        @Override
        public SeminarObject createFromParcel(Parcel in) {
            return new SeminarObject(in);
        }

        @Override
        public SeminarObject[] newArray(int size) {
            return new SeminarObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(link);
        dest.writeString(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static SeminarObject parserData(JSONObject object) {
        SeminarObject result = new SeminarObject();
        return result;
    }
}

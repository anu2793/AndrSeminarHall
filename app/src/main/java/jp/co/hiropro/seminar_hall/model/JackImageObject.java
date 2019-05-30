package jp.co.hiropro.seminar_hall.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import jp.co.hiropro.seminar_hall.util.AppConstants;

/**
 * Created by dinhdv on 11/9/2017.
 */

public class JackImageObject implements Parcelable {
    String id;
    String url;
    String link;
    String content;

    public JackImageObject() {
    }

    protected JackImageObject(Parcel in) {
        id = in.readString();
        url = in.readString();
        link = in.readString();
        content = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static JackImageObject parserData(JSONObject object) {
        JackImageObject result = new JackImageObject();
        result.setId(String.valueOf(object.optInt(AppConstants.KEY_PARAMS.ORDER.toString())));
        result.setLink(object.optString(AppConstants.KEY_PARAMS.LINK.toString(), ""));
        result.setUrl(object.optString(AppConstants.KEY_PARAMS.IMAGE.toString(), ""));
        result.setContent(object.optString(AppConstants.KEY_PARAMS.BODY.toString(), ""));
        return result;
    }

    public static final Creator<JackImageObject> CREATOR = new Creator<JackImageObject>() {
        @Override
        public JackImageObject createFromParcel(Parcel in) {
            return new JackImageObject(in);
        }

        @Override
        public JackImageObject[] newArray(int size) {
            return new JackImageObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(link);
        dest.writeString(content);
    }
}

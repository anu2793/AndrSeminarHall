package jp.co.hiropro.seminar_hall.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import jp.co.hiropro.seminar_hall.util.KeyParser;

/**
 * Created by Kienmt on 1/25/2018.
 */

public class Contact implements Parcelable {
    int id;
    int status;
    String fullname, avatar, user_member_id, contact_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser_member_id() {
        return user_member_id;
    }

    public void setUser_member_id(String user_member_id) {
        this.user_member_id = user_member_id;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public Contact() {
    }

    public static Contact parse(JSONObject object) {
        Contact contact = new Contact();
        contact.setId(object.optInt(KeyParser.KEY.ID.toString()));
        contact.setStatus(object.optInt(KeyParser.KEY.STATUS.toString()));
        contact.setFullname(object.optString(KeyParser.KEY.FULL_NAME.toString()));
        contact.setAvatar(object.optString(KeyParser.KEY.AVATAR.toString()));
        contact.setUser_member_id(object.optString(KeyParser.KEY.USER_MEMBER_ID.toString()));
        contact.setContact_id(object.optString(KeyParser.KEY.CONTACT_ID.toString()));
        return contact;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.status);
        dest.writeString(this.fullname);
        dest.writeString(this.avatar);
        dest.writeString(this.user_member_id);
        dest.writeString(this.contact_id);
    }

    protected Contact(Parcel in) {
        this.id = in.readInt();
        this.status = in.readInt();
        this.fullname = in.readString();
        this.avatar = in.readString();
        this.user_member_id = in.readString();
        this.contact_id = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}

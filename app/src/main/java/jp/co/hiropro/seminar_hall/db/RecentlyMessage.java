package jp.co.hiropro.seminar_hall.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class RecentlyMessage {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "user_id")
    public String userId;

    @ColumnInfo(name = "count")
    public String count;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "message")
    public String message;

    @ColumnInfo(name = "avatar")
    public String avatar;

    @ColumnInfo(name = "dateTime")
    public String dateTime;

    public RecentlyMessage(String userId, String count, String name, String message, String avatar, String dateTime) {
        this.userId = userId;
        this.count = count;
        this.name = name;
        this.message = message;
        this.avatar = avatar;
        this.dateTime = dateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}

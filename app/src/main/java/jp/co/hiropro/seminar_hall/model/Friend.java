package jp.co.hiropro.seminar_hall.model;

import java.io.Serializable;

/**
 * Created by dinhdv on 1/31/2018.
 */

public class Friend implements Serializable {
    private int friendId;
    private String mName;
    private String mAvatar;

    public Friend(int friendId, String mName, String mAvatar) {
        this.friendId = friendId;
        this.mName = mName;
        this.mAvatar = mAvatar;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmAvatar() {
        return mAvatar;
    }

    public void setmAvatar(String mAvatar) {
        this.mAvatar = mAvatar;
    }

}

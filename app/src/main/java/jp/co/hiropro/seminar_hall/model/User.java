package jp.co.hiropro.seminar_hall.model;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.BaseActivity;
import jp.co.hiropro.seminar_hall.controller.activity.LoginActivity;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.KeyParser;

/**
 * Created by Tuấn Sơn on 31/7/2017.
 */

public class User implements Parcelable {
    int id;
    String email;
    int remainPremium;
    boolean isPremiumUser;
    String userId;
    double speed = 1;
    int status = AppConstants.STATUS_USER.NORMAL;
    int isSocialType;
    int role_user;
    String introduction_video_url;
    String fullname;
    String profile;
    String avatar;
    String youtubeLink;
    String googleLink;
    String facebookLink;
    String twitterLink;
    String instagramLink;
    String created_at;
    String updated_at;
    int user_client_id;
    boolean isNewRegister;
    int point;

    public User() {
    }

    private static User instance = new User();

    protected User(Parcel in) {
        id = in.readInt();
        email = in.readString();
        remainPremium = in.readInt();
        isPremiumUser = in.readByte() != 0;
        userId = in.readString();
        status = in.readInt();
        isSocialType = in.readInt();
        role_user = in.readInt();
        introduction_video_url = in.readString();
        fullname = in.readString();
        profile = in.readString();
        avatar = in.readString();
        youtubeLink = in.readString();
        googleLink = in.readString();
        facebookLink = in.readString();
        twitterLink = in.readString();
        instagramLink = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
        user_client_id = in.readInt();
        point = in.readInt();
        isNewRegister = in.readByte() != 0;
        currentUser = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static User getInstance() {
        return instance;
    }

    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPremiumUser() {
        return isPremiumUser;
    }

    public void setPremiumUser(boolean premiumUser) {
        isPremiumUser = premiumUser;
    }

    public int getRemainPremium() {
        return remainPremium;
    }

    public void setRemainPremium(int remainPremium) {
        this.remainPremium = remainPremium;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getIsSocialType() {
        return isSocialType;
    }

    public void setIsSocialType(int isSocialType) {
        this.isSocialType = isSocialType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRoleUser() {
        return role_user;
    }

    public void setRoleUser(int role_user) {
        this.role_user = role_user;
    }

    public String getIntroductionVideoUrl() {
        return introduction_video_url;
    }

    public void setIntroductionVideoUrl(String introduction_video_url) {
        this.introduction_video_url = introduction_video_url;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String getGoogleLink() {
        return googleLink;
    }

    public void setGoogleLink(String googleLink) {
        this.googleLink = googleLink;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getUserClientId() {
        return user_client_id;
    }

    public void setUserClientId(int user_client_id) {
        this.user_client_id = user_client_id;
    }

    public boolean isNewRegister() {
        return isNewRegister;
    }

    public void setNewRegister(boolean newRegister) {
        isNewRegister = newRegister;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    /**
     * Using if user register when user is skip user.
     * Khi sử dụng hàm này. User đã hoàn thành việc đăng kí || Login để sử dụng app. chuyển vể User bình thường.
     *
     * @return
     */
    public boolean isSkipUser() {
        User user = getInstance().getCurrentUser();
        return user != null && user.getEmail() != null && user.getEmail().length() > 0 && user.getEmail().equalsIgnoreCase(AppConstants.TEST.USER_NAME);
    }

    public static User parse(JSONObject item) {
        User user = new User();
        user.setIsSocialType(0);
        user.setId(item.optInt(KeyParser.KEY.ID.toString()));
        user.setUserId(item.optString(KeyParser.KEY.ID.toString()));
        user.setEmail(item.optString("email"));
        user.setRoleUser(item.optInt("role_user"));
        user.setIntroductionVideoUrl(item.optString("introduction_video"));
        user.setFullname(item.optString("fullname"));
        user.setProfile(item.optString("profile"));
        user.setAvatar(item.optString("avatar"));
        user.setYoutubeLink(item.optString("social_youtube"));
        user.setGoogleLink(item.optString("social_google"));
        user.setFacebookLink(item.optString("social_face"));
        user.setTwitterLink(item.optString("social_twitter"));
        user.setInstagramLink(item.optString("social_instagram"));
        user.setCreated_at(item.optString("created_at"));
        user.setUpdatedAt(item.optString("updated_at"));
        user.setUserClientId(item.optInt("user_client_id"));
        user.setStatus(item.optInt("status"));
        user.setPoint(item.optInt(AppConstants.KEY_PARAMS.POINT.toString(), 0));
        user.setNewRegister(item.optBoolean("isNewRegister", false));
        int premiumRemain = item.optInt("premium_remain");
        user.setRemainPremium(premiumRemain);
        // Bay gio khong con user premium.
//        if (premiumRemain >= 0)
//            user.setPremiumUser(true);
//        else
        user.setPremiumUser(false);
        return user;
    }

    public static User parseContact(JSONObject item) {
        User user = new User();
        user.setIsSocialType(0);
        user.setId(item.optInt(KeyParser.KEY.CONTACT_ID.toString()));
        user.setUserId(item.optString(KeyParser.KEY.ID.toString()));
        user.setEmail(item.optString("email"));
        user.setRoleUser(item.optInt("role_user"));
        user.setIntroductionVideoUrl(item.optString("introduction_video"));
        user.setFullname(item.optString("fullname"));
        user.setProfile(item.optString("profile"));
        user.setAvatar(item.optString("avatar"));
        user.setYoutubeLink(item.optString("social_youtube"));
        user.setGoogleLink(item.optString("social_google"));
        user.setFacebookLink(item.optString("social_face"));
        user.setTwitterLink(item.optString("social_twitter"));
        user.setInstagramLink(item.optString("social_instagram"));
        user.setCreated_at(item.optString("created_at"));
        user.setUpdatedAt(item.optString("updated_at"));
        user.setUserClientId(item.optInt("user_client_id"));
        user.setStatus(item.optInt("status"));
        user.setNewRegister(item.optBoolean("isNewRegister", false));
        int premiumRemain = item.optInt("premium_remain");
        user.setRemainPremium(premiumRemain);
        // Bay gio khong con user premium.
//        if (premiumRemain >= 0)
//            user.setPremiumUser(true);
//        else
        user.setPremiumUser(false);
        return user;
    }

    public static User parseProfile(JSONObject item, User user) {
        user.setRoleUser(item.optInt(KeyParser.KEY.ROLE_USER.toString()));
        user.setProfile(item.optString(KeyParser.KEY.PROFILE.toString()));
        user.setIntroductionVideoUrl(item.optString(KeyParser.KEY.VIDEO_INTRO.toString()));
        user.setFullname(item.optString(KeyParser.KEY.FULL_NAME.toString()));
        user.setAvatar(item.optString(KeyParser.KEY.AVATAR.toString()));
        user.setYoutubeLink(item.optString(KeyParser.KEY.S_YOUTUBE.toString()));
        user.setGoogleLink(item.optString(KeyParser.KEY.S_GOOGLE.toString()));
        user.setFacebookLink(item.optString(KeyParser.KEY.S_FACE.toString()));
        user.setTwitterLink(item.optString(KeyParser.KEY.S_TWITTER.toString()));
        user.setInstagramLink(item.optString(KeyParser.KEY.S_INSTAGRAM.toString()));
        user.setNewRegister(item.optBoolean(KeyParser.KEY.IS_NEW_REGISTER.toString()));
        return user;
    }

    public void logout(Activity activity) {
        AccountManager mAccountManager = AccountManager.get(activity);
        Account[] accounts = mAccountManager.getAccountsByType(activity.getString(R.string.account_type));
        if (accounts.length > 0) {
            if (android.os.Build.VERSION.SDK_INT >= 22) {
                // use new account manager code
                mAccountManager.removeAccount(accounts[0], activity, null, null);
            } else {
                mAccountManager.removeAccount(accounts[0], null, null);
            }

        }
        ShortcutBadger.applyCount(activity, 0);
        User.getInstance().setCurrentUser(null);
        BaseActivity.user = null;
        activity.startActivity(new Intent(activity, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeInt(remainPremium);
        dest.writeByte((byte) (isPremiumUser ? 1 : 0));
        dest.writeString(userId);
        dest.writeInt(status);
        dest.writeInt(isSocialType);
        dest.writeInt(role_user);
        dest.writeString(introduction_video_url);
        dest.writeString(fullname);
        dest.writeString(profile);
        dest.writeString(avatar);
        dest.writeString(youtubeLink);
        dest.writeString(googleLink);
        dest.writeString(facebookLink);
        dest.writeString(twitterLink);
        dest.writeString(instagramLink);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeInt(user_client_id);
        dest.writeByte((byte) (isNewRegister ? 1 : 0));
        dest.writeParcelable(currentUser, flags);
        dest.writeInt(point);
    }
}

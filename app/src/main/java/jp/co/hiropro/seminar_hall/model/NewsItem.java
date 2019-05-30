package jp.co.hiropro.seminar_hall.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.Log;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.KeyParser;

/**
 * Clone from Ipost
 */
public class NewsItem implements Parcelable {
    private int mId;
    private String mTitle;
    private String mBody;
    private String mFileName;
    private String mNotice;
    private String mNoticeStatus;
    private String mSendAt;
    private String mUpdatedAt;
    private String mCreatedAt;

    private String mDescription;
    private String mDate;
    private String mImage;
    private String mType;
    private String mLinkDetail;
    private String mSummary = "";
    private String mThumbNail;
    private int read = AppConstants.STATUS_NEW.UNREAD;
    private boolean mIsFromNotification = false;

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setBody(String body) {
        mBody = body;
    }


    public void setDescription(String description) {
        mDescription = description;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }


    public void setNotice(String notice) {
        mNotice = notice;
    }

    public void setNoticeStatus(String noticeStatus) {
        mNoticeStatus = noticeStatus;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public void setCreatededAt(String createdAt) {
        mCreatedAt = createdAt;
    }


    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }


    public String getFileName() {
        return mFileName;
    }


    public String getNotice() {
        return mNotice;
    }


    public String getSendAt() {
        return mSendAt;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getDate() {
        //Long unix = Long.parseLong(mDate.toString()) * 1000;
        //String date = (String) DateFormat.format("yyyy年MM月dd日 kk:mm", unix);
        //return date;
        return mDate;
    }

    public boolean isFromNotification() {
        return mIsFromNotification;
    }

    public void setmIsFromNotification(boolean mIsFromNotification) {
        this.mIsFromNotification = mIsFromNotification;
    }

    public String getDateWithFomatJP() {
        String date = "";
        if (mDate.length() > 0) {
            //2016/02/04 20:32
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.JAPAN);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.JAPAN);
            Date dateTime = new Date();
            try {
                dateTime = dateFormat.parse(mDate.toString());
                String dayOfTheWeek = sdf.format(dateTime);
                Log.e("DAYYYYYYY", "VAlues --- :" + dayOfTheWeek);
                date = (String) DateFormat.format("yyyy年MM月dd日", dateTime);
                if (dayOfTheWeek.contains("曜日")) {
                    dayOfTheWeek = dayOfTheWeek.replace("曜日", "");
                }
                date += "(" + dayOfTheWeek + ")";
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public int getId() {
        return mId;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }

    public void setId(int id) {
        mId = id;
    }


    public String getNoticeStatus(String noticeStatus) {
        return noticeStatus;
    }


    public String getLinkDetail() {
        return mLinkDetail;
    }

    public void setLinkDetail(String mLinkDetail) {
        this.mLinkDetail = mLinkDetail;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String mSummary) {
        this.mSummary = mSummary;
    }

    public String getThumbNail() {
        return mThumbNail;
    }

    public void setThumbNail(String mThumbNail) {
        this.mThumbNail = mThumbNail;
    }

    public int getRead() {
        return read;
    }

    public boolean isRead() {
        return getRead() == AppConstants.STATUS_NEW.READ;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public static NewsItem parser(JSONObject object) {
        NewsItem item = new NewsItem();
        item.setId(object.optInt(KeyParser.KEY.ID.toString()));
        item.setTitle(object.optString(KeyParser.KEY.TITLE.toString(), ""));
        item.setDescription(object.optString(KeyParser.KEY.DESCRIPTION.toString(), ""));
        item.setBody(object.optString(KeyParser.KEY.BODY.toString()));
        item.setSummary(object.optString(KeyParser.KEY.SUMMARY.toString(), ""));
        item.setThumbNail(object.optString(KeyParser.KEY.THUMBNAIL.toString(), ""));
        item.setImage(object.optString(KeyParser.KEY.IMAGE.toString(), ""));
        item.setCreatededAt(object.optString(KeyParser.KEY.SEND_AT.toString(), ""));
        item.setDate(object.optString(KeyParser.KEY.SEND_AT.toString(), ""));
        item.setRead(object.optInt(KeyParser.KEY.READ.toString()));
        return item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mBody);
        dest.writeString(this.mFileName);
        dest.writeString(this.mNotice);
        dest.writeString(this.mNoticeStatus);
        dest.writeString(this.mSendAt);
        dest.writeString(this.mUpdatedAt);
        dest.writeString(this.mCreatedAt);
        dest.writeString(this.mDescription);
        dest.writeString(this.mDate);
        dest.writeString(this.mImage);
        dest.writeString(this.mType);
        dest.writeString(this.mLinkDetail);
        dest.writeString(this.mSummary);
        dest.writeString(this.mThumbNail);
        dest.writeInt(this.read);
        dest.writeByte((byte) (mIsFromNotification ? 1 : 0));
    }

    public NewsItem() {
    }

    protected NewsItem(Parcel in) {
        this.mId = in.readInt();
        this.mTitle = in.readString();
        this.mBody = in.readString();
        this.mFileName = in.readString();
        this.mNotice = in.readString();
        this.mNoticeStatus = in.readString();
        this.mSendAt = in.readString();
        this.mUpdatedAt = in.readString();
        this.mCreatedAt = in.readString();
        this.mDescription = in.readString();
        this.mDate = in.readString();
        this.mImage = in.readString();
        this.mType = in.readString();
        this.mLinkDetail = in.readString();
        this.mSummary = in.readString();
        this.mThumbNail = in.readString();
        this.read = in.readInt();
        this.mIsFromNotification = in.readByte() != 0;
    }

    public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel source) {
            return new NewsItem(source);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };
}
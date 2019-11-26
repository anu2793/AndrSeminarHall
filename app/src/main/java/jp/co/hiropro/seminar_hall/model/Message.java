package jp.co.hiropro.seminar_hall.model;

/**
 * Created by dinhdv on 1/31/2018.
 */

public class Message {
    public static final int TYPE_MESSAGE_RECEIVED = 0;
    public static final int TYPE_MESSAGE_SENT = 1;
    private int mType;
    private String mMessage;
    private String mCreateTime;

    public Message() {
    }

    public Message(int mType, String mMessage, String mCreateTime) {
        this.mType = mType;
        this.mMessage = mMessage;
        this.mCreateTime = mCreateTime;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getmCreateTime() {
        return mCreateTime;
    }

    public void setmCreateTime(String mCreateTime) {
        this.mCreateTime = mCreateTime;
    }
}

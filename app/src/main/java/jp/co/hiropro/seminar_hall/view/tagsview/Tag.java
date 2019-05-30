package jp.co.hiropro.seminar_hall.view.tagsview;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;


public class Tag implements Parcelable {

    public int id;
    public String text;
    public int tagTextColor;
    public float tagTextSize;
    public int layoutColor;
    public int layoutColorPress;
    public boolean isDeletable;
    public int deleteIndicatorColor;
    public float deleteIndicatorSize;
    public float radius;
    public String deleteIcon;
    public float layoutBorderSize;
    public int layoutBorderColor;
    public Drawable background;


    public Tag(String text) {
        init(0, text, Constants.DEFAULT_TAG_TEXT_COLOR, Constants.DEFAULT_TAG_TEXT_SIZE, Constants.DEFAULT_TAG_LAYOUT_COLOR, Constants.DEFAULT_TAG_LAYOUT_COLOR_PRESS,
                Constants.DEFAULT_TAG_IS_DELETABLE, Constants.DEFAULT_TAG_DELETE_INDICATOR_COLOR, Constants.DEFAULT_TAG_DELETE_INDICATOR_SIZE, Constants.DEFAULT_TAG_RADIUS, Constants.DEFAULT_TAG_DELETE_ICON, Constants.DEFAULT_TAG_LAYOUT_BORDER_SIZE, Constants.DEFAULT_TAG_LAYOUT_BORDER_COLOR);
    }

    private void init(int id, String text, int tagTextColor, float tagTextSize,
                      int layoutColor, int layoutColorPress, boolean isDeletable,
                      int deleteIndicatorColor, float deleteIndicatorSize, float radius,
                      String deleteIcon, float layoutBorderSize, int layoutBorderColor) {
        this.id = id;
        this.text = text;
        this.tagTextColor = tagTextColor;
        this.tagTextSize = tagTextSize;
        this.layoutColor = layoutColor;
        this.layoutColorPress = layoutColorPress;
        this.isDeletable = isDeletable;
        this.deleteIndicatorColor = deleteIndicatorColor;
        this.deleteIndicatorSize = deleteIndicatorSize;
        this.radius = radius;
        this.deleteIcon = deleteIcon;
        this.layoutBorderSize = layoutBorderSize;
        this.layoutBorderColor = layoutBorderColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.text);
        dest.writeInt(this.tagTextColor);
        dest.writeFloat(this.tagTextSize);
        dest.writeInt(this.layoutColor);
        dest.writeInt(this.layoutColorPress);
        dest.writeByte(this.isDeletable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.deleteIndicatorColor);
        dest.writeFloat(this.deleteIndicatorSize);
        dest.writeFloat(this.radius);
        dest.writeString(this.deleteIcon);
        dest.writeFloat(this.layoutBorderSize);
        dest.writeInt(this.layoutBorderColor);
    }

    protected Tag(Parcel in) {
        this.id = in.readInt();
        this.text = in.readString();
        this.tagTextColor = in.readInt();
        this.tagTextSize = in.readFloat();
        this.layoutColor = in.readInt();
        this.layoutColorPress = in.readInt();
        this.isDeletable = in.readByte() != 0;
        this.deleteIndicatorColor = in.readInt();
        this.deleteIndicatorSize = in.readFloat();
        this.radius = in.readFloat();
        this.deleteIcon = in.readString();
        this.layoutBorderSize = in.readFloat();
        this.layoutBorderColor = in.readInt();
    }

    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };
}

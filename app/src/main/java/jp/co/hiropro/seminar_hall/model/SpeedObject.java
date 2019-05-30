package jp.co.hiropro.seminar_hall.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SpeedObject implements Parcelable {
    private int id;
    private String name;
    private double values;

    public SpeedObject() {
    }

    public SpeedObject(int id, String name, double values) {
        this.id = id;
        this.name = name;
        this.values = values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValues() {
        return values;
    }

    public void setValues(float values) {
        this.values = values;
    }

    protected SpeedObject(Parcel in) {
        id = in.readInt();
        name = in.readString();
        values = in.readFloat();
    }

    public static final Creator<SpeedObject> CREATOR = new Creator<SpeedObject>() {
        @Override
        public SpeedObject createFromParcel(Parcel in) {
            return new SpeedObject(in);
        }

        @Override
        public SpeedObject[] newArray(int size) {
            return new SpeedObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(values);
    }
}

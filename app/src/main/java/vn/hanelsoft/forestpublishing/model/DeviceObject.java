package vn.hanelsoft.forestpublishing.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import vn.hanelsoft.forestpublishing.util.AppConstants;

/**
 * Created by dinhdv on 11/1/2017.
 */

public class DeviceObject implements Parcelable {
    int id;
    String name;
    String code;
    int type = AppConstants.TYPE_DEVICE.ANDROID;
    String version;
    String device_id;
    int isCurrent = 0;

    public DeviceObject() {
    }

    protected DeviceObject(Parcel in) {
        id = in.readInt();
        name = in.readString();
        code = in.readString();
        type = in.readInt();
        version = in.readString();
        device_id = in.readString();
        isCurrent = in.readInt();
    }


    public static final Creator<DeviceObject> CREATOR = new Creator<DeviceObject>() {
        @Override
        public DeviceObject createFromParcel(Parcel in) {
            return new DeviceObject(in);
        }

        @Override
        public DeviceObject[] newArray(int size) {
            return new DeviceObject[size];
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
        dest.writeString(code);
        dest.writeInt(type);
        dest.writeString(version);
        dest.writeString(device_id);
        dest.writeInt(isCurrent);
    }

    public static DeviceObject parserData(JSONObject object) {
        DeviceObject result = new DeviceObject();
        result.setId(object.optInt(AppConstants.KEY_PARAMS.ID.toString()));
        result.setType(object.optInt(AppConstants.KEY_PARAMS.TYPE.toString()));
        result.setVersion(object.optString(AppConstants.KEY_PARAMS.DEVICE_OS.toString()));
        result.setName(object.optString(AppConstants.KEY_PARAMS.DEVICE_NAME.toString()));
        result.setDeviceId(object.optString(AppConstants.KEY_PARAMS.DEVICE_ID.toString()));
        result.setIsCurrent(object.optInt(AppConstants.KEY_PARAMS.IS_CURRENT_DEVICE.toString()));
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeviceId() {
        return device_id;
    }

    public void setDeviceId(String device_id) {
        this.device_id = device_id;
    }

    public int getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(int isCurrent) {
        this.isCurrent = isCurrent;
    }

    public boolean isCurrentDevice() {
        return getIsCurrent() == 1;
    }
}

package jp.co.hiropro.seminar_hall.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import jp.co.hiropro.seminar_hall.util.AppConstants;

public class Point implements Parcelable {
    private String id;
    private int point = 0;
    private int bonus = 0;
    private int money = 0;
    private String currency = "円";
    private String code;

    private Point(Parcel in) {
        id = in.readString();
        point = in.readInt();
        bonus = in.readInt();
        money = in.readInt();
        currency = in.readString();
        code = in.readString();
    }

    public Point() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    public static Point parserData(JSONObject object) {
        Point result = new Point();
        result.setId(object.optString(AppConstants.KEY_PARAMS.ID.toString()));
        result.setCode(object.optString(AppConstants.KEY_PARAMS.PRICE_CODE.toString()));
        result.setMoney(object.optInt(AppConstants.KEY_PARAMS.PRICE.toString()));
        result.setPoint(object.optInt(AppConstants.KEY_PARAMS.POINT.toString()));
        result.setBonus(object.optInt(AppConstants.KEY_PARAMS.DISCOUNT_POINT.toString()));
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(point);
        dest.writeInt(bonus);
        dest.writeInt(money);
        dest.writeString(currency);
        dest.writeString(code);
    }
}

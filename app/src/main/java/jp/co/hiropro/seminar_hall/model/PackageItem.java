package jp.co.hiropro.seminar_hall.model;

import org.json.JSONObject;

import jp.co.hiropro.seminar_hall.util.AppConstants;

/**
 * Created by dinhdv on 8/10/2017.
 */

public class PackageItem {
    int id;
    int user_client_id;
    int price_id;
    String name;
    String month;
    int status;
    String created_at;
    String updated_at;
    String price;
    String price_code;
    String current_price;
    String current_enddate;
    String next_price;
    String next_startdate;

    public PackageItem() {
    }

    public PackageItem(int id, int user_client_id, int price_id, String name, String month, int status, String created_at, String updated_at, String price, String price_code) {
        this.id = id;
        this.user_client_id = user_client_id;
        this.price_id = price_id;
        this.name = name;
        this.month = month;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.price = price;
        this.price_code = price_code;
    }

    public static PackageItem parser(JSONObject object) {
        PackageItem item = new PackageItem();
        item.setId(object.optInt(AppConstants.KEY_PARAMS.ID.toString(), 0));
        item.setUser_client_id(object.optInt(AppConstants.KEY_PARAMS.USER_CLIENT_ID.toString(), 0));
        item.setPrice_id(object.optInt(AppConstants.KEY_PARAMS.PRICE_ID.toString(), 0));
        item.setName(object.optString(AppConstants.KEY_PARAMS.NAME.toString()));
        item.setMonth(object.optString(AppConstants.KEY_PARAMS.MONTH.toString()));
        item.setStatus(object.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 0));
        item.setCreated_at(object.optString(AppConstants.KEY_PARAMS.CREATE_AT.toString()));
        item.setUpdated_at(object.optString(AppConstants.KEY_PARAMS.UPDATE_AT.toString()));
        item.setPrice(object.optString(AppConstants.KEY_PARAMS.PRICE.toString()));
        item.setPrice_code(object.optString(AppConstants.KEY_PARAMS.PRICE_CODE.toString()));
        item.setCurrent_price(object.optString(AppConstants.KEY_PARAMS.CURRENT_PRICE.toString()));
        item.setCurrent_enddate(object.optString(AppConstants.KEY_PARAMS.CURRENT_END_DATE.toString()));
        item.setNext_price(object.optString(AppConstants.KEY_PARAMS.NEXT_PRICE.toString()));
        item.setNext_startdate(object.optString(AppConstants.KEY_PARAMS.NEXT_START_DATE.toString()));

        return item;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_client_id() {
        return user_client_id;
    }

    public void setUser_client_id(int user_client_id) {
        this.user_client_id = user_client_id;
    }

    public int getPrice_id() {
        return price_id;
    }

    public void setPrice_id(int price_id) {
        this.price_id = price_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_code() {
        return price_code;
    }

    public void setPrice_code(String price_code) {
        this.price_code = price_code;
    }

    public String getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(String current_price) {
        this.current_price = current_price;
    }

    public String getCurrent_enddate() {
        return current_enddate;
    }

    public void setCurrent_enddate(String current_enddate) {
        this.current_enddate = current_enddate;
    }

    public String getNext_price() {
        return next_price;
    }

    public void setNext_price(String next_price) {
        this.next_price = next_price;
    }

    public String getNext_startdate() {
        return next_startdate;
    }

    public void setNext_startdate(String next_startdate) {
        this.next_startdate = next_startdate;
    }
}

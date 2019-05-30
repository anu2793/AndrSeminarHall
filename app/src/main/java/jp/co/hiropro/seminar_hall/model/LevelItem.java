package jp.co.hiropro.seminar_hall.model;

import org.json.JSONObject;

import jp.co.hiropro.seminar_hall.util.AppConstants;

/**
 * Created by dinhdv on 7/27/2017.
 */

public class LevelItem {
    String name;
    int progress;
    boolean isGroup = false;

    public LevelItem() {
    }

    public LevelItem(String name, int progress) {
        this.name = name;
        this.progress = progress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public static LevelItem parser(JSONObject object) {
        LevelItem result = new LevelItem();
        String name = object.optString(AppConstants.KEY_PARAMS.TITLE.toString());
        int total = object.optInt(AppConstants.KEY_PARAMS.TOTAL.toString(), 1);
        int viewed = object.optInt(AppConstants.KEY_PARAMS.VIEWED.toString(), 0);
        result.setName(name);
        result.setProgress(total == 0 ? 0 : viewed * 100 / total);
        return result;
    }
}

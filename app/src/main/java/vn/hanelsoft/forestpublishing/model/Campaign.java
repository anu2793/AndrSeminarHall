package vn.hanelsoft.forestpublishing.model;

import org.json.JSONObject;

import vn.hanelsoft.forestpublishing.util.KeyParser;

/**
 * Created by Tuấn Sơn on 18/7/2017.
 */

public class Campaign {
    String image, description;
    String link;
    int id;

    public Campaign(String image, String description) {
        this.image = image;
        this.description = description;
    }

    public Campaign() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public static Campaign parse(JSONObject object) {
        Campaign item = new Campaign();
        item.setId(object.optInt(KeyParser.KEY.ID.toString()));
        item.setImage(object.optString(KeyParser.KEY.IMAGE.toString()));
        item.setLink(object.optString(KeyParser.KEY.LINK.toString()));
        return item;
    }
}

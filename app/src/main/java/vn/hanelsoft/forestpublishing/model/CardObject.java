package vn.hanelsoft.forestpublishing.model;

/**
 * Created by dinhdv on 1/31/2018.
 */

public class CardObject {
    String id;
    String url;

    public CardObject(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public CardObject() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

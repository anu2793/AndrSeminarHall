package vn.hanelsoft.forestpublishing.model;

import org.json.JSONObject;

/**
 * Created by Tuấn Sơn on 21/7/2017.
 */

public class SearchResult {

    public static final int TYPE_CONTENT = 1;
    public static final int TYPE_SUBCATEGORY = 2;

    private int id, type;
    private String title, description;
    private SearchVideo searchVideo;
    private SearchSubCategory searchSubCategory;

    public SearchResult() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SearchVideo getSearchVideo() {
        return searchVideo;
    }

    public void setSearchVideo(SearchVideo searchVideo) {
        this.searchVideo = searchVideo;
    }

    public SearchSubCategory getSearchSubCategory() {
        return searchSubCategory;
    }

    public void setSearchSubCategory(SearchSubCategory searchSubCategory) {
        this.searchSubCategory = searchSubCategory;
    }

    public static SearchResult parse(JSONObject object) {
        SearchResult item = new SearchResult();
        item.setId(object.optInt("id"));
        item.setTitle(object.optString("title"));
        item.setDescription(object.optString("description"));
        item.setType(object.optInt("item_type"));
        item.setSearchVideo(SearchVideo.parse(object));
        item.setSearchSubCategory(SearchSubCategory.parse(object));
        return item;
    }
}

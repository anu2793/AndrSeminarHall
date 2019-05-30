package jp.co.hiropro.seminar_hall.model;

/**
 * Created by Tuấn Sơn on 18/7/2017.
 */

public class Menu {
    private String title;
    private int id, icon;

    public Menu(int id, String title, int icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}

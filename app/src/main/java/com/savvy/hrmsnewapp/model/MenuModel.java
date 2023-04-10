package com.savvy.hrmsnewapp.model;

/**
 * Created by AM00347646 on 30-08-2016.
 */
public class MenuModel {

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public String getNotification_count() {
        return notification_count;
    }

    public void setNotification_count(String notification_count) {
        this.notification_count = notification_count;
    }

    public String menu_name;
    public String menu_id;
    public String notification_count;
}

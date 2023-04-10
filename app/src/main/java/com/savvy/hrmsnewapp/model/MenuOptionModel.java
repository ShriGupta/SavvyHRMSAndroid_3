package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by AM00347646 on 30-08-2016.
 */
public class MenuOptionModel {

    public ArrayList<MenuModel> getMenu_item() {
        return menu_item;
    }

    public void setMenu_item(ArrayList<MenuModel> menu_item) {
        this.menu_item = menu_item;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<MenuModel> menu_item;
    public String status;
    public String code;
    public String message;
}

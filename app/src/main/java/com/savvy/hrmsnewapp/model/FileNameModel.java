package com.savvy.hrmsnewapp.model;

import java.io.Serializable;

public class FileNameModel implements Serializable {
    String file_name;
    int position;

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

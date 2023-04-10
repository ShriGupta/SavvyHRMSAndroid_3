package com.savvy.hrmsnewapp.model;

/**
 * Created by Hari Om on 8/15/2016.
 */
public class VideoslistModel {

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getVideo_file_type() {
        return video_file_type;
    }

    public void setVideo_file_type(String video_file_type) {
        this.video_file_type = video_file_type;
    }

    public String video_id;
    public String video_name;
    public String video_file_type;

}

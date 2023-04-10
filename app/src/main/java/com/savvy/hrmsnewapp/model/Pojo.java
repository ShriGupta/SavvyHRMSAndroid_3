package com.savvy.hrmsnewapp.model;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by orapc7 on 7/5/2017.
 */

public class Pojo {
    Context context;

    Bitmap pic;


    public Pojo(Context context){
        this.context = context;
    }

    public Bitmap getPic() { return pic; }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }
}

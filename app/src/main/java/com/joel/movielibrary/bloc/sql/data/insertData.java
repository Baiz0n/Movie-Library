package com.joel.movielibrary.bloc.sql.data;

import android.graphics.Bitmap;

import com.joel.movielibrary.Tools.ImageConverter;

public class insertData {

    private String title;
    private String desc;
    private Bitmap image;

    public insertData(String title, String desc, Bitmap image) {
        this.title = title;
        this.desc = desc;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public byte[] getImage() {

        ImageConverter.setBitmapToByte(this.image);

        return ImageConverter.getBitmapToByte();
    }
}

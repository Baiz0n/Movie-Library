package com.joel.movielibrary.bloc.sql.data;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Collections;

public class RowData {

    private ArrayList<String> titles;
    private ArrayList<String> descs;
    private ArrayList<Bitmap> images;
    private ArrayList<String> imgUrl;

    public RowData(ArrayList<String> titles,
                   ArrayList<String> descs,
                   ArrayList<Bitmap> images,
                   ArrayList<String> imgUrl) {
        this.titles = titles;
        this.descs = descs;
        this.images = images;
        this.imgUrl = imgUrl;
    }

    public ArrayList<String> getTitles() {

        Collections.reverse(titles);
        return this.titles;
    }


    public ArrayList<String> getDescs() {

        Collections.reverse(descs);
        return this.descs;
    }

    public ArrayList<Bitmap> getImages() {

        Collections.reverse(images);
        return this.images;
    }

    public ArrayList<String> getImgUrl() {

        Collections.reverse(imgUrl);
        return this.imgUrl;
    }

}

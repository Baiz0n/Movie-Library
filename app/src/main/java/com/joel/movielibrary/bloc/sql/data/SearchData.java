package com.joel.movielibrary.bloc.sql.data;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class SearchData {

    private ArrayList<String> titles;
    private ArrayList<String> descs;
    private ArrayList<Bitmap> images;

    public SearchData(ArrayList<String> titles, ArrayList<String> descs, ArrayList<Bitmap> images) {
        this.titles = titles;
        this.descs = descs;
        this.images = images;
    }

    public ArrayList<String> getTitles() {
        return titles;
    }

    public ArrayList<String> getDescs() {
        return descs;
    }

    public ArrayList<Bitmap> getImages() {
        return images;
    }
}

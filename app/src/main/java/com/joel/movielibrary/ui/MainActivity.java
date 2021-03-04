package com.joel.movielibrary.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joel.movielibrary.R;
import com.joel.movielibrary.Tools.ImageConverter;
import com.joel.movielibrary.Tools.RecycleAdapter;
import com.joel.movielibrary.Tools.Toaster;
import com.joel.movielibrary.bloc.sql.data.RowData;
import com.joel.movielibrary.bloc.sql.sql.MovieDatabase;

import java.sql.Blob;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ArrayList<String> mTitles;
    private ArrayList<String> mDescs;
    private ArrayList<Bitmap> mImages;
    private ArrayList<String> mImgUrl;

    private Intent intent;
    private RecyclerView recyclerView;

    private MovieDatabase db;

    private boolean loaded = false;

    private SwipeRefreshLayout swipe;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycle_view);
        swipe = findViewById(R.id.container);

        db = new MovieDatabase(this);

        populateData();

        swipe.setOnRefreshListener(swipeListener);

    } // end onCreate

    public void initRecyclerView(ArrayList<String> mTitles, ArrayList<String> mDescs, ArrayList<Bitmap> mImages, ArrayList<String> mImgUrl) {
        Log.d(TAG, "Initted!");
        RecycleAdapter adapter = new RecycleAdapter(mTitles, mDescs, mImages, this, true, mImgUrl);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.navigation,menu);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case R.id.navigation_search_movie:

                intent = new Intent(this, SearchMovies.class);
                startActivity(intent);
                break;

            case R.id.navigation_add_movie:

                intent = new Intent(this, AddOrEditMovie.class);
                startActivity(intent);
                break;

            case R.id.navigation_clear_library:
                db.delData("all");
                populateData();
                break;

            case R.id.navigation_exit:
                finish();
                System.exit(0);
                break;
        }


        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void populateData() {

        mTitles = new ArrayList<>();
        mDescs = new ArrayList<>();
        mImages = new ArrayList<>();
        mImgUrl = new ArrayList<>();

        Cursor titles = db.getData("titles");
        while ( titles.moveToNext() ) { mTitles.add(titles.getString(0)); }

        Cursor descs = db.getData("descs");
        while ( descs.moveToNext() ) { mDescs.add(descs.getString(0)); }

        Cursor imgUrl = db.getData("urls");
        while ( imgUrl.moveToNext() ) { mImgUrl.add(imgUrl.getString(0)); }

        Cursor images = db.getData("images");
        while ( images.moveToNext() ) {
            ImageConverter.setByteToBitmap(images.getBlob(0));
            mImages.add(ImageConverter.getByteToBitmap());
        }


        db.close();
        titles.close();
        descs.close();
        images.close();
        imgUrl.close();

        if (mTitles.size() != 0 && mDescs.size() != 0 && mImages.size() != 0) {

            Log.i(TAG, "the number of dudes here is : " + mTitles.size());

            RowData data = new RowData(mTitles, mDescs, mImages, mImgUrl);
            initRecyclerView(data.getTitles(), data.getDescs(), data.getImages(), data.getImgUrl());

        } else {

            recyclerView.setVisibility(View.GONE);
            Toaster.longMidToast(this,getString(R.string.entry_text));
        }
    }

    private SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onRefresh() {

            populateData();

            swipe.setRefreshing(false);
        }
    };

} // end Activity
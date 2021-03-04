package com.joel.movielibrary.ui;

import android.app.AliasActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joel.movielibrary.R;
import com.joel.movielibrary.Tools.ImageConverter;
import com.joel.movielibrary.Tools.Toaster;
import com.joel.movielibrary.bloc.sql.sql.MovieDatabase;

import java.util.ArrayList;
import java.util.Objects;

import static android.widget.ListPopupWindow.MATCH_PARENT;
import static android.widget.ListPopupWindow.POSITION_PROMPT_ABOVE;

public class MovieDetails extends AppCompatActivity {

    private static final String TAG = "MovieDetails";
    private TextView desc;
    private ImageView image;
    private boolean library;
    private MovieDatabase db;
    private Button addOrDel;
    private boolean urlShow = false;
    private Button imgBtn;
    private TextView imgUrl;
    private Button editMovie;
    private View layout;
    private Button returnTo;

    private String mTitle;
    private String mDesc;
    private Bitmap mImage;
    private String mImgUrl;

    private Intent intent;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ) {

            setContentView(R.layout.activity_movie_detailes);

        } else {

            setContentView(R.layout.activity_movie_detail_landscape);
        }


        addOrDel = findViewById(R.id.add_or_del);
        imgUrl = findViewById(R.id.img_url);
        imgBtn = findViewById(R.id.img_btn);
        desc = findViewById(R.id.desc);
        image = findViewById(R.id.image);
        editMovie = findViewById(R.id.edit_movie);
        layout = findViewById(android.R.id.content);
        returnTo = findViewById(R.id.return_to);

        db = new MovieDatabase(this);

        getIncomingIntent();

        intent = new Intent(this, AddOrEditMovie.class);

        editMovie.setOnClickListener(onEditMovie);
        imgBtn.setOnClickListener(viewUrl);
        returnTo.setOnClickListener(returnToLibraryListner);

        if ( library ) {

            addOrDel.setText(getString(R.string.remove_from_library));
            addOrDel.setOnClickListener(dialogClickListener);


        } else {

            addOrDel.setOnClickListener(addThisToLibrary);
            editMovie.setVisibility(View.GONE);
        }


    } // onCreate

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getIncomingIntent() {

        if (getIntent().hasExtra("title")) {

            mTitle = getIntent().getStringExtra("title");
            mDesc = getIntent().getStringExtra("desc");
            library = Objects.requireNonNull(getIntent().getExtras()).getBoolean("library");
            mImgUrl = getIntent().getStringExtra("img_url");

            if ( getIntent().hasExtra("toast") ) {

                Toaster.shortBotToast(this,"Saved!");
            }

            setContent();
            setImageSize();
        }
    }

    private void setContent() {

        desc.setText(mDesc);

        imgUrl.setText(mImgUrl);
        imgUrl.setVisibility(View.GONE);
        setTitle(mTitle);

        if ( !(String.valueOf(imgUrl.getText()).equals("")) && isInternetConnection() ) {

            try {

                Thread thread = new Thread(new Runnable(){
                    @Override
                    public void run() {

                        mImage = ImageConverter.getUrlImage(String.valueOf(imgUrl.getText()));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                image.setImageBitmap(mImage);
                            }
                        });
                    }
                });
                thread.start();


            } catch ( Exception e ){

                Log.e(TAG, "ERROR CAUGHT ===========> " + e);

            }

        } else {

            imgBtn.setVisibility(View.GONE);

            Cursor cursorTitle = db.getData("titles");
            Cursor img = db.getData("images");
            ArrayList<String> titles = new ArrayList<>();
            ArrayList<byte[]> images = new ArrayList<>();
            int count = 0;

            while(cursorTitle.moveToNext()) {

                titles.add(cursorTitle.getString(0));


                if (cursorTitle.getString(0).equals(mTitle)) {

                   count = titles.indexOf(mTitle);
                   break;
                }
            }

            while (img.moveToNext()) {

                images.add(img.getBlob(0));
            }
                ImageConverter.setByteToBitmap( images.get(count) );
                image.setImageBitmap(ImageConverter.getByteToBitmap());
        }
    }

    private View.OnClickListener addThisToLibrary = new View.OnClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {

            ImageConverter.setBitmapToByte(mImage);
            byte[] byteImage = ImageConverter.getBitmapToByte();

            db.addData(mTitle,mDesc,byteImage,mImgUrl);

            Toaster.shortBotToast(getContext(), mTitle + " was added");
            backToLibrary();
        }
    };


    View.OnClickListener dialogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("please don't remove " + mTitle + " it's such an awesome movie! .. Are you sure?")
            .setCancelable(false)
            .setPositiveButton("Yep", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    db.delData(mTitle);
                    Toaster.longBotToast(getContext(),mTitle + " is deleted from library! :(");
                    backToLibrary();
                }
            })
            .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toaster.shortBotToast(getContext(),"No action has been made");
                }
            });

            AlertDialog alert = builder.create();
            alert.setTitle("MOVIES KILLER");
            alert.show();
        }
    };

    private View.OnClickListener viewUrl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if ( urlShow ) {

                imgUrl.setVisibility(View.GONE);
                imgBtn.setText("Show Image URL");
                urlShow = false;

            } else {

                imgUrl.setVisibility(View.VISIBLE);
                imgBtn.setText("Hide Image URL");
                urlShow = true;
            }
        }
    };

    private View.OnClickListener onEditMovie = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            intent.putExtra("title", mTitle);
            intent.putExtra("desc", mDesc);
            intent.putExtra("image", mImage);
            startActivity(intent);
        }
    };

    private Context getContext() {

        return this;
    }

    private void setImageSize() {

        image.post(new Runnable() {
            @Override
            public void run() {

                image.requestLayout();
                layout.requestLayout();
                desc.requestLayout();

                double multiplier;
                double imgNewHeight;

                if ( getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT ) {

                    multiplier = layout.getHeight()/image.getHeight();

                    double imgNewWidth = image.getWidth() * (multiplier/2);
                    imgNewHeight = layout.getHeight()/2;

                    image.getLayoutParams().width = (int) imgNewWidth;
                    image.getLayoutParams().height = (int) imgNewHeight;
                }


            }
        });
    }

    private View.OnClickListener returnToLibraryListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            backToLibrary();
        }
    };

    private void backToLibrary() {

        intent = new Intent( getApplicationContext(), MainActivity.class );
        getApplicationContext().startActivity(intent);
    }

    public  boolean isInternetConnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            return false;
    }

}

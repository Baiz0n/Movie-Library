package com.joel.movielibrary.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.joel.movielibrary.R;
import com.joel.movielibrary.Tools.ImageConverter;
import com.joel.movielibrary.Tools.Toaster;
import com.joel.movielibrary.bloc.sql.sql.MovieDatabase;

import java.io.IOException;
import java.util.Objects;

public class AddOrEditMovie extends AppCompatActivity {

    private EditText title;
    private EditText desc;
    private Button selectImg;
    private ImageView img;
    private Button save;

    private String mTitle;
    private String mDesc;
    private Bitmap mImage;

    private boolean exist = false;
    private boolean gallery = false;

    Intent intent;

    private MovieDatabase db = new MovieDatabase(this);

    private final int REQUEST_CODE_EXTERNAL_IMAGE = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_movie);

        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);
        selectImg = findViewById(R.id.img_btn);
        img = findViewById(R.id.img);
        save = findViewById(R.id.save);

        selectImg.setOnClickListener(selectImage);
        save.setOnClickListener(clickSave);

        getIncomingIntent();

    }

    private void getIncomingIntent() {

        if (getIntent().hasExtra("title")) {

            mTitle = getIntent().getStringExtra("title");
            mDesc = getIntent().getStringExtra("desc");
            mImage = getIntent().getParcelableExtra("image");

            setContent();
            exist = true;
        }
    }

    private void setContent() {

        desc.setText(mDesc);
        img.setImageBitmap(mImage);
        title.setText(mTitle);
    }

    View.OnClickListener selectImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(intent,
                    REQUEST_CODE_EXTERNAL_IMAGE);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == REQUEST_CODE_EXTERNAL_IMAGE && RESULT_OK == resultCode && data != null) {

            Uri selectedImg = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImg);
                int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                img.setImageBitmap(scaled);
                gallery = true;

            } catch (IOException e) {

                e.printStackTrace();
                Toaster.longBotToast(this, "There's been a problem. Please try another image");
            }

        }
    }

    View.OnClickListener clickSave = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {

            Bitmap bitmapImg = ((BitmapDrawable)img.getDrawable()).getBitmap();
            ImageConverter.setBitmapToByte(bitmapImg);

            String newTitle = String.valueOf(title.getText());
            String newDesc = String.valueOf(desc.getText());
            byte[] newImg = ImageConverter.getBitmapToByte();

            if ( !newTitle.equals("") &&  !newDesc.equals("") && exist) {

                db.upData(mTitle, newTitle, newDesc, newImg, true);
                sendIntent();

            } else if (!newTitle.equals("") &&  !newDesc.equals("")) {

                db.addData(newTitle, newDesc, newImg, "");
                sendIntent();

            } else {
                Toaster.shortBotToast(getApplicationContext(), "Must add title, description and image");
            }
        }
    };

    private void sendIntent() {

        intent = new Intent( this, MovieDetails.class);

        intent.putExtra("title", String.valueOf(title.getText()));
        intent.putExtra("desc", String.valueOf(desc.getText()));
        intent.putExtra("library", true);
        intent.putExtra("img_url", "");
        intent.putExtra("toast", true );

        this.startActivity(intent);
    }
}

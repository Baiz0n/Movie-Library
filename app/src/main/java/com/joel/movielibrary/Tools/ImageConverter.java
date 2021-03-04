package com.joel.movielibrary.Tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageConverter {

    static private byte[] bitmapToByte;
    static private Bitmap byteToBitmap;

    private String urlImage;

    static public byte[] getBitmapToByte() {
        return bitmapToByte;
    }

    static public void setBitmapToByte(Bitmap image)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        bitmapToByte = byteArray;
    }

    static public Bitmap getByteToBitmap() {
        return byteToBitmap;
    }

    static public void setByteToBitmap(byte[] image)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(image , 0, image.length);

        byteToBitmap = bitmap;
    }



    public static Bitmap getUrlImage(String urlImage)
    {
            try {
                URL url = new URL(urlImage);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                // Log exception
                return null;
            }
    }

}

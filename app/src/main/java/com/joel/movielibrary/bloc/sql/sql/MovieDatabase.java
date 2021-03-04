package com.joel.movielibrary.bloc.sql.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static java.lang.Integer.parseInt;

public class MovieDatabase extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "movies";
    public static final String DATABASE_NAME = "movies.db";
    private Cursor cursor;
    private String query;


    public MovieDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable =
                "CREATE TABLE "
                + TABLE_NAME
                + " (a INTEGER PRIMARY KEY,"
                + " titles TEXT,"
                + " descs TEXT,"
                + " urls TEXT,"
                + " images BLOB)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
    }

    public void addData(String title, String desc, byte[] img, String url) {

        SQLiteDatabase db = getReadableDatabase();
        String sql = "INSERT INTO movies VALUES (NULL, ? ,?, ?, ?)";

        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, title);
        statement.bindString(2, desc);
        statement.bindString(3, url);
        statement.bindBlob(4, img);

        statement.executeInsert();
    }

    public Cursor getData(String column) {

        SQLiteDatabase db = getReadableDatabase();
        query = "SELECT "+column+" FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query,null);

        return data;
    }

    public Cursor getConditionalData( String column, String title ) {

        SQLiteDatabase db = getReadableDatabase();
        query = "SELECT "+column+"" +
                " FROM " + TABLE_NAME +
                " WHERE titles = '" +title + "'";

        Cursor data = db.rawQuery(query,null);

        data.moveToFirst();

        return data;
    }

    public boolean delData(String title) {

        SQLiteDatabase db = getReadableDatabase();

        if ( !title.equals("all") ) {

            query = "DELETE FROM " + TABLE_NAME + " WHERE titles = '" + title + "'";

        } else {

            query = "DELETE FROM " + TABLE_NAME;
        }

        db.execSQL(query);

        return true;
    }

    public boolean upData(String title,String newTitle, String desc, byte[] img, boolean gallery) {

        SQLiteDatabase db = getReadableDatabase();

        ContentValues content = new ContentValues();

        content.put("titles", newTitle);
        content.put("descs", desc);

        if ( gallery ) {

          content.put("urls", "");
        }

        content.put("images", img);

        String where = "titles LIKE ?";
        String[] whereArg = {title};

        db.update(TABLE_NAME, content,where,whereArg);

        return true;
    }
}

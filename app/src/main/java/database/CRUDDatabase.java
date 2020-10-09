package database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

import helpers.MyApplication;
import models.Gif;
import storage.Constants;

public class CRUDDatabase {
    private static SQLiteDatabase database = MyApplication.getInstance().getDatabase();

    public static void write(Gif gif) {
        ContentValues cv = new ContentValues();
        cv.put("id", gif.getId());
        cv.put("url", gif.getUrl());
        database.insert(Constants.TABLE_SAVED_GIFS, null, cv);
    }

    public static HashMap<String, String> read() {
        HashMap<String, String> gifFromDatabase = new HashMap<>();
        Cursor cursor = database.query(Constants.TABLE_SAVED_GIFS, null,
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int urlIndex = cursor.getColumnIndex("url");
            do {
                gifFromDatabase.put(cursor.getString(idIndex), cursor.getString(urlIndex));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return gifFromDatabase;
    }

    public static void delete(Gif gif) {
        database.delete(Constants.TABLE_SAVED_GIFS, "id = '" + gif.getId() + "'", null);
    }

    public static boolean isSaved(Gif gif) {
        Cursor cursor = database.query(Constants.TABLE_SAVED_GIFS, null, "id = ?", new String[] {gif.getId()},
                null, null, null);
        boolean isSave = cursor.moveToFirst();
        cursor.close();
        return isSave;
    }

    public static void close() {
        database.close();
    }
}

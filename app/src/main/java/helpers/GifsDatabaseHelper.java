package helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import storage.Constants;

public class GifsDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String CREATE_DATABASE =
            "CREATE TABLE gifs (" +
                    "id VARCHAR," +
                    "url VARCHAR" + ");";

    public GifsDatabaseHelper(@Nullable Context context) {
        super(context, Constants.GIFS_TABLE_DB, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        if (oldVersion == 1 && newVersion == 2) {
            try {
                db.execSQL(CREATE_DATABASE);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }
}

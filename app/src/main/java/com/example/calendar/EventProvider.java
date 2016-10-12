package com.example.calendar;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * Created by ryo on 2016/10/01.
 */
public class EventProvider extends ContentProvider {
    private EventDatabaseHelper mEventDatabaseHelper = null;
    public static final int CURRENT_DATABASE_VERSION = 1;
    @Override
    public boolean onCreate() {
        mEventDatabaseHelper = new EventDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mEventDatabaseHelper.getReadableDatabase();
        Cursor c = db.query(EventInfo.DB_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mEventDatabaseHelper.getWritableDatabase();
        long newId = db.insert(EventInfo.DB_NAME, null, values);
        Uri newUri = Uri.parse(uri + "/" + newId);
        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mEventDatabaseHelper.getWritableDatabase();
        int numDeleted = db.delete(EventInfo.DB_NAME, selection, selectionArgs);
        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mEventDatabaseHelper.getWritableDatabase();
        int numUpdated = db.update(EventInfo.DB_NAME, values, selection, selectionArgs);
        return numUpdated;
    }

    public class EventDatabaseHelper extends SQLiteOpenHelper {
        public EventDatabaseHelper(Context context) {
            super(context, EventInfo.DB_NAME + ".db", null, CURRENT_DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE " + EventInfo.DB_NAME + "("
                    + EventInfo.ID + " INTEGER PRIMARY KEY,"
                    + EventInfo.TITLE + " TEXT,"
                    + EventInfo.CONTENT + " TEXT,"
                    + EventInfo.WHERE + " TEXT,"
                    + EventInfo.END_TIME + " TEXT,"
                    + EventInfo.START_TIME + " TEXT"
                    + ");";
            db.execSQL(sql);
        }

        /**
         * アプリのバージョンが上がってDBのレコードに変更があった場合など、
         * DBのバージョンに差異があると呼び出される。
         * Googleカレンダーと同期をとるので、DB構造が変わっても新たに同期しなおせばよい。
         * よって、ここではテーブルを消去、再作成を行う。
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + EventInfo.DB_NAME);
            onCreate(db);
        }
    }
}
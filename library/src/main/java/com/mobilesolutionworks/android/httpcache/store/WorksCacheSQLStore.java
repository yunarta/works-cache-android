package com.mobilesolutionworks.android.httpcache.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mobilesolutionworks.android.httpcache.WorksCache;
import com.mobilesolutionworks.android.httpcache.config.WorksCacheConfig;

import java.io.IOException;

/**
 * Created by yunarta on 17/5/15.
 */
public class WorksCacheSQLStore implements WorksCacheStore {

    private static final String TABLE_NAME = "works_cache";

    private final Object LOCK = new Object();

    private SQLiteDatabase mDatabase;

    private String mDatabaseName;

    public WorksCacheSQLStore(String databaseName) {
        mDatabaseName = databaseName;
    }

    @Override
    public WorksCache load(String path) {
        if (mDatabase == null) {
            mDatabase = new SQLiteOpenHelperImpl(WorksCacheConfig.getInstance().getContext(), mDatabaseName).getWritableDatabase();
        }

        Cursor cursor;
        synchronized (LOCK) {
            cursor = mDatabase.query(TABLE_NAME, new String[]{"uri", "data", "time"}, "uri = ?", new String[]{path}, null, null, null);
            try {
                if (cursor.moveToNext()) {
                    byte[] blob;
                    long time;

                    cursor.getString(0);
                    blob = cursor.getBlob(1);
                    time = cursor.getLong(2);

                    return new WorksCache(blob, time);
                }

                return null;
            } finally {
                cursor.close();
            }
        }
    }

    @Override
    public void deleteCache(String path) {
        if (mDatabase == null) {
            mDatabase = new SQLiteOpenHelperImpl(WorksCacheConfig.getInstance().getContext(), mDatabaseName).getWritableDatabase();
        }

        synchronized (LOCK) {
            mDatabase.delete(TABLE_NAME, "uri = ?", new String[]{path});
        }
    }

    @Override
    public void store(String path, WorksCache cache) throws IOException {
        if (mDatabase == null) {
            mDatabase = new SQLiteOpenHelperImpl(WorksCacheConfig.getInstance().getContext(), mDatabaseName).getWritableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("uri", path);
        values.put("data", cache.data());
        values.put("time", cache.time());

        synchronized (LOCK) {
            mDatabase.insert(TABLE_NAME, null, values);
        }
    }

    private class SQLiteOpenHelperImpl extends SQLiteOpenHelper {

        public SQLiteOpenHelperImpl(Context context, String name) {
            super(context, name, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    /* local uri    */ "uri TEXT," +
                    /* data content */ "data BLOB," +
                    /* expiry time  */ "time INTEGER," +
                    "PRIMARY KEY (uri) ON CONFLICT REPLACE" +
                    ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}

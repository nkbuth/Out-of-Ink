package com.nathanbuth.outofink;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
    private final Context dbCtx;

    //Columns for databse
    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DETAIL = "detail";

    //private static final String TAG = "DBAdapter";
    private DatabaseHelper DBHelper;
    private SQLiteDatabase DB;

    //Statement for database creation
    private static final String DB_CREATE =
            "create table notes (_id integer primary key autoincrement, "
            + "title text not null, detail text not null);";

    private static final String DB_NAME = "data";
    private static final String DB_TABLE = "notes";
    private static final int DB_VERSION = 1;

    private static final class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
            DB.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(DB);
        }
    }

    public DBAdapter(Context ctx) {
       this.dbCtx = ctx;
    }

    public DBAdapter open() throws SQLException {
        DBHelper = new DatabaseHelper(dbCtx);
        DB = DBHelper.getWritableDatabase();
        return this;
    }

    //Insert new note into the database with values passed to it
    public long createNote(String title, String detail) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_DETAIL, detail);

        return DB.insert(DB_TABLE, null, initialValues);
    }

    //Remove note from database based upon id passed to it
    public boolean deleteNote(long rowId) {
        return DB.delete(DB_TABLE, KEY_ID + "=" + rowId, null) > 0;
    }

    //Retrieve all notes from the database
    public Cursor getAllNotes() {
        return DB.query(DB_TABLE, new String[] {KEY_ID, KEY_TITLE,
                KEY_DETAIL}, null, null, null, null, null);
    }

    //Retrieve specific note from database
    public Cursor getNote(long rowId) throws SQLException {

        Cursor cursor =
                DB.query(true, DB_TABLE, new String[] {KEY_ID,
                                KEY_TITLE, KEY_DETAIL}, KEY_ID + "=" + rowId, null,
                        null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }

    //Update note based on values passed to it
    public boolean updateNote(long rowId, String title, String body) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_DETAIL, body);

        return DB.update(DB_TABLE, args, KEY_ID + "=" + rowId, null) > 0;
    }
}

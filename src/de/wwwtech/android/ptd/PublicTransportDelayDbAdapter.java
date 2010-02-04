package de.wwwtech.android.ptd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PublicTransportDelayDbAdapter {
    public static final String SERVICE_OPERATOR_KEY_NAME = "name";
    public static final String SERVICE_OPERATOR_KEY_CONTACT_EMAIL = "email";
    public static final String SERVICE_OPERATOR_KEY_ROWID = "_id";
    
    public static final String LINE_KEY_NAME = "name";
    public static final String LINE_KEY_START = "start";
    public static final String LINE_KEY_END = "ende";
    public static final String LINE_KEY_ROWID = "_id";
    
    private static final String TAG = "PublicTransportDelayDbAdapter";

    private SQLiteDatabase mDb;
	private Context mCtx;
	private DatabaseHelper mDbHelper;
    
    /**
     * Database creation sql statement
     */
    private static final String DATABASE_NAME = "ptd";
    private static final String SERVICE_OPERATOR_DATABASE_TABLE = "serviceOperator";
    private static final String LINE_DATABASE_TABLE = "line";
    private static final int DATABASE_VERSION = 1;
    
    final static String SERVICE_OPERATOR_CREATE =
        "create table " + SERVICE_OPERATOR_DATABASE_TABLE + " (" + SERVICE_OPERATOR_KEY_ROWID + " integer primary key autoincrement, "
                + SERVICE_OPERATOR_KEY_NAME + " text not null, " + SERVICE_OPERATOR_KEY_CONTACT_EMAIL + " text not null);";
    
    final static String LINE_CREATE =
        "create table " + LINE_DATABASE_TABLE + " (" + LINE_KEY_ROWID + " integer primary key autoincrement, "
                + LINE_KEY_NAME + " text not null, " + LINE_KEY_START + " text not null, " + LINE_KEY_END + " text not null);";
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(SERVICE_OPERATOR_CREATE);
            db.execSQL(LINE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SERVICE_OPERATOR_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + LINE_DATABASE_TABLE);
            onCreate(db);
        }
    }
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public PublicTransportDelayDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the . If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public PublicTransportDelayDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }


    public long serviceOperatorCreate(String name, String eMail) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(SERVICE_OPERATOR_KEY_NAME, name);
        initialValues.put(SERVICE_OPERATOR_KEY_CONTACT_EMAIL, eMail);

        return mDb.insert(SERVICE_OPERATOR_DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean serviceOperatorDelete(long rowId) {

        return mDb.delete(SERVICE_OPERATOR_DATABASE_TABLE, SERVICE_OPERATOR_KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor serviceOperatorFetchAll() {

        return mDb.query(SERVICE_OPERATOR_DATABASE_TABLE, new String[] {SERVICE_OPERATOR_KEY_ROWID, SERVICE_OPERATOR_KEY_NAME,
        		SERVICE_OPERATOR_KEY_CONTACT_EMAIL}, null, null, null, null, null);
    }

    public Cursor serviceOperatorFetch(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, SERVICE_OPERATOR_DATABASE_TABLE, new String[] {SERVICE_OPERATOR_KEY_ROWID,
                		SERVICE_OPERATOR_KEY_NAME, SERVICE_OPERATOR_KEY_CONTACT_EMAIL}, SERVICE_OPERATOR_KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public boolean serviceOperatorUpdate(long rowId, String name, String eMail) {
        ContentValues args = new ContentValues();
        args.put(SERVICE_OPERATOR_KEY_NAME, name);
        args.put(SERVICE_OPERATOR_KEY_CONTACT_EMAIL, eMail);

        return mDb.update(SERVICE_OPERATOR_DATABASE_TABLE, args, SERVICE_OPERATOR_KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    
    public long lineCreate(String name, String start, String end) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(LINE_KEY_NAME, name);
        initialValues.put(LINE_KEY_START, start);
        initialValues.put(LINE_KEY_END, end);

        return mDb.insert(LINE_DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean lineDelete(long rowId) {

        return mDb.delete(LINE_DATABASE_TABLE, LINE_KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor lineFetchAll() {

        return mDb.query(LINE_DATABASE_TABLE, new String[] {LINE_KEY_ROWID, LINE_KEY_NAME, LINE_KEY_START, LINE_KEY_END}, null, null, null, null, null);
    }

    public Cursor lineFetch(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, SERVICE_OPERATOR_DATABASE_TABLE, new String[] {LINE_KEY_ROWID, LINE_KEY_NAME, LINE_KEY_START, LINE_KEY_END}, LINE_KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public boolean lineUpdate(long rowId, String name, String start, String end) {
        ContentValues args = new ContentValues();
        args.put(LINE_KEY_NAME, name);
        args.put(LINE_KEY_START, start);
        args.put(LINE_KEY_END, end);

        return mDb.update(LINE_DATABASE_TABLE, args, LINE_KEY_ROWID + "=" + rowId, null) > 0;
    }  
}



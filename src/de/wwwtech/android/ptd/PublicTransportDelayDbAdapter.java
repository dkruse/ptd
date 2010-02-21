package de.wwwtech.android.ptd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PublicTransportDelayDbAdapter {
	
	private static final String SERVICE_OPERATOR_DATABASE_TABLE = "serviceOperator";
    public static final String SERVICE_OPERATOR_KEY_NAME = "name";
    public static final String SERVICE_OPERATOR_KEY_CONTACT_EMAIL = "email";
    public static final String SERVICE_OPERATOR_KEY_ROWID = "_id";
    
    private static final String LINE_DATABASE_TABLE = "line";
    public static final String LINE_KEY_NAME = "name";
    public static final String LINE_KEY_START = "start";
    public static final String LINE_KEY_END = "ende";
    public static final String LINE_KEY_ROWID = "_id";
    
    private static final String CONNECTION_DATABASE_TABLE = "connection";
    public static final String CONNECTION_KEY_ROWID = "_id";
    public static final String CONNECTION_DIRECTION_KEY = "direction"; // outward / return
    public static final String CONNECTION_TIME_KEY = "time";
    public static final String CONNECTION_WEEKDAY_TYPE_KEY = "weekdayType"; // daily, sundays and holyday...
    public static final String CONNECTION_FK_LINE_KEY_ROWID = "lineKeyRef";
    
    private static final String STATION_DATABASE_TABLE = "station";
    public static final String STATION_KEY_NAME = "name";
    public static final String STATION_KEY_ROWID = "_id";
    
    private static final String DELAY_DATABASE_TABLE = "delay";
    public static final String DELAY_KEY_ROWID = "_id";
    public static final String DELAY_KEY_CONNECTION_START = "start";
    public static final String DELAY_KEY_CONNECTION_END = "end";
    public static final String DELAY_KEY_START_TIME = "startTime";
    public static final String DELAY_KEY_END_TIME = "endTime";
    public static final String DELAY_KEY_DATE = "date";
    public static final String DELAY_FK_CONNECTION_KEY_ROWID = "connectionKeyRef";
    
    private static final String EVENT_DATABASE_TABLE = "event";
    public static final String EVENT_KEY_ROWID = "_id";
    public static final String EVENT_KEY_PLACE_TYPE = "placeType"; // track, station
    public static final String EVENT_KEY_REASON = "reason";
    public static final String EVENT_FK_DELAY_KEY_ROWID = "delayKeyRef";
    
    private static final String ANNOUNCEMENT_DATABASE_TABLE = "announcement";
    private static final String ANNOUNCEMENT_KEY_ROWID = "_id";
    private static final String ANNOUNCEMENT_KEY_TYPE = "type"; // vehicle, station
    private static final String ANNOUNCEMENT_KEY_TEXT = "text";
    private static final String ANNOUNCEMENT_FK_DELAY_KEY_ROWID = "delayKeyRef";
    
    private static final String TAG = "PublicTransportDelayDbAdapter";

    private SQLiteDatabase mDb;
	private Context mCtx;
	private DatabaseHelper mDbHelper;
    
    /**
     * Database creation sql statement
     */
    private static final String DATABASE_NAME = "ptd";
    private static final int DATABASE_VERSION = 1;
    
    
    final static String SERVICE_OPERATOR_CREATE =
        "create table " + SERVICE_OPERATOR_DATABASE_TABLE + " (" 
                + SERVICE_OPERATOR_KEY_ROWID + " integer primary key autoincrement, "
                + SERVICE_OPERATOR_KEY_NAME + " text not null, " 
                + SERVICE_OPERATOR_KEY_CONTACT_EMAIL + " text not null);";
    
    final static String LINE_CREATE =
        "create table " + LINE_DATABASE_TABLE + " (" 
                + LINE_KEY_ROWID + " integer primary key autoincrement, "
                + LINE_KEY_NAME + " text not null, " 
                + LINE_KEY_START + " integer constraint fk_line_start references " + STATION_DATABASE_TABLE + "(" + STATION_KEY_ROWID + ") on delete restrict, "
                + LINE_KEY_END + " integer constraint fk_line_end references " + STATION_DATABASE_TABLE + "(" + STATION_KEY_ROWID + ") on delete restrict);";
    
    final static String CONNECTION_CREATE =
    	"create table " + CONNECTION_DATABASE_TABLE + " (" 
    	        + CONNECTION_KEY_ROWID + " integer primary key autoincrement, "
    	        + CONNECTION_FK_LINE_KEY_ROWID + " integer constraint fk_line references " + LINE_DATABASE_TABLE + "(" + LINE_KEY_ROWID + ") on delete restrict, "
    	        + CONNECTION_DIRECTION_KEY + " integer not null, "
    	        + CONNECTION_TIME_KEY + " integer not null, "
    	        + CONNECTION_WEEKDAY_TYPE_KEY + " integer not null);";
    
    final static String STATION_CREATE = 
    	"create table " + STATION_DATABASE_TABLE + " ("
    	        + STATION_KEY_ROWID + " integer primary key autoincrement, "
    	        + STATION_KEY_NAME + " text not null)";
    
    final static String DELAY_CREATE =
    	"create table " + DELAY_DATABASE_TABLE + " ("
    	        + DELAY_KEY_ROWID + " integer primary key autoincrement, "
    	        + DELAY_FK_CONNECTION_KEY_ROWID + " integer constraint fk_connection references " + CONNECTION_DATABASE_TABLE + "(" + CONNECTION_KEY_ROWID + ") on delete restrict, "
                + DELAY_KEY_CONNECTION_START + " integer constraint fk_entry references " + STATION_DATABASE_TABLE + "(" + STATION_KEY_ROWID + ") on delete restrict, "
                + DELAY_KEY_CONNECTION_END + " integer constraint fk_exit references " + STATION_DATABASE_TABLE + "(" + STATION_KEY_ROWID + ") on delete restrict, "
                + DELAY_KEY_START_TIME + " integer not null, "
                + DELAY_KEY_END_TIME + " integer not null, "
                + DELAY_KEY_DATE + " date not null);";
    
    final static String EVENT_CREATE = 
    	"create table " + EVENT_DATABASE_TABLE + " ("
    	        + EVENT_KEY_ROWID + " integer primary key autoincrement, "
    	        + EVENT_FK_DELAY_KEY_ROWID + " integer constraint fk_event_delay references " + DELAY_DATABASE_TABLE + "(" + DELAY_KEY_ROWID + ") on delete cascade, "
                + EVENT_KEY_PLACE_TYPE + " integer, "
                + EVENT_KEY_REASON + " text);";
    
    final static String ANNOUNCEMENT_CREATE =
    	"create table " + ANNOUNCEMENT_DATABASE_TABLE + " ("
    	        + ANNOUNCEMENT_KEY_ROWID + " integer primary key autoincrement, "
    	        + ANNOUNCEMENT_FK_DELAY_KEY_ROWID + " integer constraint fk_announcement_delay references " + DELAY_DATABASE_TABLE + "(" + DELAY_KEY_ROWID + ") on delete cascade, "
                + ANNOUNCEMENT_KEY_TYPE + " integer not null, "
                + ANNOUNCEMENT_KEY_TEXT + " text not null);";
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	try {
        		db.execSQL(SERVICE_OPERATOR_CREATE);
        		db.execSQL(STATION_CREATE);
        		db.execSQL(LINE_CREATE);
        		db.execSQL(CONNECTION_CREATE);
        		db.execSQL(DELAY_CREATE);
        		db.execSQL(ANNOUNCEMENT_CREATE);
        		db.execSQL(EVENT_CREATE);
        		Log.i("DatabaseCreate", "Database successfully created");
        	} catch(Exception e)
        	{
        		Log.e("DatabaseCreate", e.getStackTrace().toString());
        	}
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SERVICE_OPERATOR_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + LINE_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + STATION_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + CONNECTION_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DELAY_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ANNOUNCEMENT_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + EVENT_DATABASE_TABLE);
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



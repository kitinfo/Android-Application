/**
 * 
 */
package de.kitinfo.app.data;

import java.util.LinkedList;
import java.util.List;

import de.kitinfo.app.data.StorageProvider.UriMatch;
import de.kitinfo.app.timers.TimerEvent;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * @author mpease
 *
 */
public class Database extends SQLiteOpenHelper {

	private static final String TIMER_TITLE = "title";
	private static final String TIMER_MESSAGE = "message";
	private static final String TIMER_ID = "id";
	private static final int DBVERSION = 1;
	private static final String DBNAME = "kitinfo.db";
	private final String TIMER_TABLE = "timers";
	private static final String TIMER_DATE = "date";
	
	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public Database(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	
	public void insert(TimerEvent event) {
		
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();
		db.insert(TIMER_TABLE, null, getContentValues(event));
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	/**
	 * Return all timer events in database.
	 * @return list of timer events
	 */
	public List<TimerEvent> getEvents() {
		
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.query(TIMER_TABLE, null, null, null, null, null, null);
		
		List<TimerEvent> events = convertTimerEvents(c);
		c.close();
		db.close();
		
		return events;
	}

	/**
	 * creates the database object.
	 * @param context context of the app. Needed for saving the database in his data section.
	 */
	public Database(Context context) {
		super(context, DBNAME, null, DBVERSION);
		
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + TIMER_TABLE + "(tableID integer primary key autoincrement, id integer, title text, message text,  date real)");

	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TIMER_TABLE);
		
		onCreate(db);

	}

	/**
	 * Sends a raw query to database and returns the cursor comes from database.
	 * @param table the table we wants to query. Valid tables @see {@link UriMatch}
	 * @param projection columns we want to query.
	 * @param selection the WHERE String
	 * @param selectionArgs args for selection (the ?s).
	 * @param sortOrder order of columns.
	 * @return the cursor with the database answer.
	 */
	public Cursor rawQuery(String table, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
		db.close();
		
		return c;
	}


	public long rawInsert(String table, ContentValues values) {
		
		SQLiteDatabase db = getReadableDatabase();
		
		return db.insert(table, null, values);
	}
	
	
	public static ContentValues getContentValues(TimerEvent event) {
		
		ContentValues values = new ContentValues();
		
		values.put(TIMER_TITLE, event.getTitle());
		values.put(TIMER_MESSAGE, event.getMessage());
		values.put(TIMER_ID, event.getID());
		values.put(TIMER_DATE, event.getDateInLong());
		
		return values;
	}
	
	public static List<TimerEvent> convertTimerEvents(Cursor c) {
		
		List<TimerEvent> timers = new LinkedList<TimerEvent>();
		
		while (c.moveToNext()) {
			TimerEvent te = new TimerEvent(c.getString(c.getColumnIndex(TIMER_TITLE)), c.getString(c.getColumnIndex(TIMER_MESSAGE)), c.getInt(c.getColumnIndex(TIMER_ID)), c.getLong(c.getColumnIndex(TIMER_DATE)));
			timers.add(te);
		}
		
		
		return timers;
	}


	public int rawDelete(Uri uri, String selection, String[] selectionArgs) {
		
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();
		
		int rows = db.delete(TIMER_TABLE, selection, selectionArgs);
		
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		
		return rows;
	}

}

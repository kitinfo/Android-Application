/**
 * 
 */
package de.kitinfo.app.data;

import java.util.LinkedList;
import java.util.List;

import de.kitinfo.app.timers.TimerEvent;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.OpenableColumns;

/**
 * @author mpease
 *
 */
public class Database extends SQLiteOpenHelper {

	private static final String TIMER_TITLE = "title";
	private static final String TIMER_MESSAGE = "message";
	private static final String TIMER_ID = "id";
	private final String TIMER_TABLE = "timers";
	private final String TIMER_DATE = "date";
	
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
		ContentValues values = new ContentValues();
		
		values.put(TIMER_TITLE, event.getTitle());
		values.put(TIMER_MESSAGE, event.getMessage());
		values.put(TIMER_ID, event.getID());
		values.put(TIMER_DATE, event.getDateInLong());
		
		db.beginTransaction();
		db.insert(TIMER_TABLE, null, values);
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	/**
	 * Return all timer events in database.
	 * @return list of timer events
	 */
	public List<TimerEvent> getEvents() {
		
		List<TimerEvent> events = new LinkedList<TimerEvent>();
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.query(TIMER_TABLE, null, null, null, null, null, null);
		
		while (c.moveToNext()) {
			TimerEvent te = new TimerEvent(c.getString(c.getColumnIndex(TIMER_TITLE)), c.getString(c.getColumnIndex(TIMER_MESSAGE)), c.getInt(c.getColumnIndex(TIMER_ID)), c.getLong(c.getColumnIndex(TIMER_DATE)));
			events.add(te);
		}
		c.close();
		db.close();
		
		return events;
	}

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 * @param errorHandler
	 */
	public Database(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		
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

}

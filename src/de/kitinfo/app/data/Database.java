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

/**
 * @author mpease
 *
 */
public class Database extends SQLiteOpenHelper {

	public enum Tables {
		
		TIMER_TABLE("timers", Columns.TIMER),
		MENSA("mensa", Columns.MENSA),
		MENSA_LINE("mensa_line", Columns.MENSA_LINE),
		MENSA_MEAL("mensa_meal", Columns.MENSA_MEAL);
		
		private String table;
		private Columns columns;
		
		private Tables(String table, Columns colums) {
			this.table = table;
			this.columns = colums;
		}
		
		public String getTable() {
			return table;
		}
		
		public Columns getColumns() {
			return columns;
		}
		
	}
	
	public enum Columns {
		
		TIMER(5, new ColumnValues[]{ColumnValues.TIMER_ID, ColumnValues.TIMER_TITLE, ColumnValues.TIMER_MESSAGE, ColumnValues.TIMER_DATE, ColumnValues.TIMER_IGNORE }),
		MENSA(4, new ColumnValues[]{ColumnValues.MENSA_DATABASEID, ColumnValues.MENSA_ID, ColumnValues.MENSA_NAME, ColumnValues.MENSA_DATE}),
		MENSA_LINE(3, new ColumnValues[]{ColumnValues.LINE_ID, ColumnValues.LINE_NAME, ColumnValues.LINE_MENSA}),
		MENSA_MEAL(3, new ColumnValues[]{ColumnValues.MEAL_ID, ColumnValues.MEAL_LINE, ColumnValues.MEAL_VALUE});
		
		private ColumnValues[] columns;
		private int count;
		
		private Columns(int count, ColumnValues[] columns) {
			this.count = count;
			this.columns = columns;
		}
		
		public ColumnValues[] getColumnValues() {
			return columns;
		}
		
		public int getCount() {
			return count;
		}
	}
	
	public enum ColumnValues {
		
		TIMER_ID("id", "integer unique", 0, 0),
		TIMER_TITLE("title", "text", 1, 0),
		TIMER_MESSAGE("message", "text", 2, 0),
		TIMER_DATE("date", "real", 3, 0),
		TIMER_IGNORE("ignore", "integer DEFAULT(0)", 4, 0),
		MENSA_DATABASEID("dbid", "integer unique", 0, 1),
		MENSA_ID("id", "integer", 1, 1),
		MENSA_NAME("name", "text", 2, 1),
		MENSA_DATE("date", "real", 3, 1),
		LINE_ID("id", "integer unique", 2, 2),
		LINE_NAME("line_name", "text", 3, 2),
		LINE_MENSA("mensaid", "integer", 4, 2),
		MEAL_ID("id", "integer unique", 0, 3),
		MEAL_LINE("line", "integer", 1, 3),
		MEAL_VALUE("value", "text", 2, 3);
		
		private String name;
		private String type;
		private int position;
		private int tableID;
		
		private ColumnValues(String name, String type, int position, int tableID) {
			this.name = name;
			this.type = type;
			this.position = position;
			this.tableID = tableID;
		}
		
		public int getPosition() {
			return position;
		}
		
		public String getName() {
			return name;
		}
		
		public String getType() {
			return type;
		}
		
		public ColumnValues fromPosition(int position, int tableID) {
			
			for (ColumnValues cv : values()) {
				if (cv.getPosition() == position && tableID == cv.tableID) {
					return cv;
				}
			}
			return null;
		}
	}
	
	
	private static final int DBVERSION = 3;
	private static final String DBNAME = "kitinfo.db";
	
	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public Database(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	/**
	 * updates with raw data
	 * @param table
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int rawUpdate(String table, ContentValues values, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = getReadableDatabase();
		
		int i = db.update(table, values, whereClause, whereArgs);
		
		db.close();
		return i;
	}
	
	
	public void insert(TimerEvent event) {
		
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();
		ContentValues cv = getContentValues(event);
		
		if (db.insert(Tables.TIMER_TABLE.getTable(), null, cv) == -1) {
			db.update(Tables.TIMER_TABLE.getTable(), cv , "id = ?", new String[]{"" + event.getID()});
		}
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
		
		Cursor c = db.query(Tables.TIMER_TABLE.getTable(), null, null, null, null, null, null);
		
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
		
		for (Tables t : Tables.values()) {
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("create table ");
			sb.append(t.getTable());
			sb.append("(tableID integer primary key autoincrement, ");
			
			for (int i = 0; i < t.getColumns().getColumnValues().length; i++) {
				for (ColumnValues cv : t.getColumns().getColumnValues()) {
					if (cv.getPosition() == i) {
						sb.append(cv.getName());
						sb.append(" ");
						sb.append(cv.getType());
						sb.append(", ");
						break;
					}
				}
			}
			sb.delete(sb.lastIndexOf(", "), sb.length());
			sb.append(")");
			
			db.execSQL(sb.toString());
		}
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		for (Tables t : Tables.values()) {
			db.execSQL("DROP TABLE IF EXISTS " + t.getTable());
		}
		
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
		return c;
	}
	
	public void close() {
		SQLiteDatabase db = getWritableDatabase();
		db.close();
	}


	public long rawInsert(String table, ContentValues values) {
		SQLiteDatabase db = getReadableDatabase();
		
		db.beginTransaction();
		
		if (db.updateWithOnConflict(Tables.TIMER_TABLE.getTable(), values , "id = ?", new String[]{"" + values.getAsString(ColumnValues.TIMER_ID.getName())}, SQLiteDatabase.CONFLICT_IGNORE) < 1) {
			db.insert(Tables.TIMER_TABLE.getTable(), null, values);
		}
		
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		
		return 0;
	}
	
	
	public static ContentValues getContentValues(TimerEvent event) {
		
		ContentValues values = new ContentValues();
		
		values.put(ColumnValues.TIMER_TITLE.getName(), event.getTitle());
		values.put(ColumnValues.TIMER_MESSAGE.getName(), event.getMessage());
		values.put(ColumnValues.TIMER_ID.getName(), event.getID());
		values.put(ColumnValues.TIMER_DATE.getName(), event.getDateInLong());
		
		return values;
	}
	
	public static List<TimerEvent> convertTimerEvents(Cursor c) {
		
		List<TimerEvent> timers = new LinkedList<TimerEvent>();
		
		while (c.moveToNext()) {
			TimerEvent te = new TimerEvent(
					c.getString(c.getColumnIndex(ColumnValues.TIMER_TITLE.getName())), 
					c.getString(c.getColumnIndex(ColumnValues.TIMER_MESSAGE.getName())), 
					c.getInt(c.getColumnIndex(ColumnValues.TIMER_ID.getName())),
					c.getLong(c.getColumnIndex(ColumnValues.TIMER_DATE.getName())));
			timers.add(te);
		}
		
		
		return timers;
	}


	public int rawDelete(String table, String selection, String[] selectionArgs) {
		
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();
		
		int rows = db.delete(table, selection, selectionArgs);
		
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		
		return rows;
	}


	public void reset() {
		
		onUpgrade(getWritableDatabase(), DBVERSION, DBVERSION);
	}

}

/**
 * 
 */
package de.kitinfo.app.data;

import java.util.LinkedList;
import java.util.List;

import de.kitinfo.app.data.StorageProvider.UriMatch;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.SparseArray;

/**
 * @author mpease
 *
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class Database extends SQLiteOpenHelper {
	
	private static List<Database> dblist = new LinkedList<Database>();
	/**
	 * enum for getting easy access to database structure of tables
	 * @author mpease
	 *
	 */
	public enum Tables {
		
		TIMER_TABLE("timers", new ColumnValues[]{ColumnValues.TIMER_ID, ColumnValues.TIMER_TITLE, ColumnValues.TIMER_MESSAGE, ColumnValues.TIMER_DATE, ColumnValues.TIMER_IGNORE }),
		MENSA_LINE("mensa_line", new ColumnValues[]{ColumnValues.LINE_ID, ColumnValues.LINE_NAME, ColumnValues.LINE_MENSA}),
		MENSA_MEAL("mensa_meal", new ColumnValues[]{ColumnValues.MEAL_ID, ColumnValues.MEAL_LINE, ColumnValues.MEAL_NAME, ColumnValues.MEAL_INFO, ColumnValues.MEAL_HINT, ColumnValues.MEAL_PRICE, ColumnValues.MEAL_ADDS, ColumnValues.MEAL_DATE});
		
		private String table;
		private ColumnValues[] columns;
		
		// constructor
		private Tables(String table, ColumnValues[] colums) {
			this.table = table;
			this.columns = colums;
		}
		
		/**
		 * Returns the table name
		 * @return name of the table
		 */
		public String getTable() {
			return table;
		}
		
		public static Tables fromString(String s) {
			for (Tables t : values()) {
				if (t.getTable().equals(s)) {
					return t;
				}
			}
			return null;
		}
		
		/**
		 * returns an array with objects presets the columns of the table
		 * @return array of columns
		 */
		public ColumnValues[] getColumns() {
			return columns;
		}
		
		public int getSizeOfColumns() {
			return columns.length;
		}
		
	}
	
	/**
	 * enum for easy access to the database structure of columns
	 * @author mpease
	 *
	 */
	public enum ColumnValues {
		
		TIMER_ID("id", "integer unique", 0, 0),
		TIMER_TITLE("title", "text", 1, 0),
		TIMER_MESSAGE("message", "text", 2, 0),
		TIMER_DATE("date", "real", 3, 0),
		TIMER_IGNORE("ignore", "integer DEFAULT(0)", 4, 0),
		LINE_ID("id", "integer unique", 0, 1),
		LINE_NAME("line_name", "text", 1, 1),
		LINE_MENSA("mensaid", "integer", 2, 1),
		MEAL_ID("id", "integer unique", 0, 2),
		MEAL_LINE("line", "integer", 1, 2),
		MEAL_HINT("hint", "text", 2, 2),
		MEAL_INFO("info", "text", 3, 2),
		MEAL_NAME("name", "name", 4, 2),
		MEAL_PRICE("price", "real", 5, 2),
		MEAL_ADDS("adds", "string", 6, 2),
		MEAL_TAGS("tags", "string", 7, 2),
		MEAL_DATE("date", "real", 8, 2);
		
		private String name;
		private String type;
		private int position;
		private int tableID;
		
		// constructor
		private ColumnValues(String name, String type, int position, int tableID) {
			this.name = name;
			this.type = type;
			this.position = position;
			this.tableID = tableID;
		}
		
		/**
		 * Return the position in column
		 * @return
		 */
		public int getPosition() {
			return position;
		}
		
		/**
		 * get the name of the column
		 * @return
		 */
		public String getName() {
			return name;
		}
		/**
		 * returns the type of the database object
		 * @return type of the column
		 */
		public String getType() {
			return type;
		}
		/**
		 * get the object from position in database table
		 * @param position position in table
		 * @param tableID id of the table
		 * @return object for this position
		 */
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
	 * creates the databse
	 * @param context the context of the app (for getting the database filepath)
	 * @param name name of the database
	 * @param factory if we want some special cursors
	 * @param version version of the database
	 */
	public Database(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	/**
	 * updates with raw data
	 * @param table the table
	 * @param values content values
	 * @param whereClause where we want to update
	 * @param whereArgs what args
	 * @return number of rows affected
	 */
	public int rawUpdate(String table, ContentValues values, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = getReadableDatabase();
		db.beginTransaction();
		int i = db.update(table, values, whereClause, whereArgs);
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return i;
	}

	/**
	 * creates the database object.
	 * @param context context of the app. Needed for saving the database in his data section.
	 */
	public Database(Context context) {
		super(context, DBNAME, null, DBVERSION);
		clean();
	}
	
	private int clean() {
		synchronized (dblist) {
			
			if (dblist.isEmpty()) {
				return 1;
			}
			dblist.get(0).close();
			dblist.remove(0);
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		for (Tables t : Tables.values()) {
			
			create(t, db);
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
		clean();
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
		synchronized (dblist) {
			dblist.add(this);
		}
		return c;
	}

	/**
	 * inserts a line to given table
	 * @param table the tables we want to insert data
	 * @param values values in ContentValues style
	 * @return the row id
	 */
	public long rawInsert(String table, ContentValues values) {
		SQLiteDatabase db = getReadableDatabase();
		
		db.beginTransaction();
		
		long row = db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		
		return row;
	}

	/**
	 * Deletes data from given sql infos
	 * @param table the table we want to delete from
	 * @param selection a selection string (use ? in selection)
	 * @param selectionArgs arguments for the selection (?-s)
	 * @return return affected rows
	 */
	public int rawDelete(String table, String selection, String[] selectionArgs) {
		
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();
		
		int rows = db.delete(table, selection, selectionArgs);
		
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		
		return rows;
	}

	/**
	 * resets the database
	 */
	public void reset() {
		while (clean() != 0);
		SQLiteDatabase db = getWritableDatabase();
		onUpgrade(db, DBVERSION, DBVERSION);
		db.close();
		
	}

	public void drop(Tables table) {
		while (clean() != 0);
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + table);
		db.close();
	}

	public void create(Tables t, SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("create table ");
		sb.append(t.getTable());
		sb.append("(tableID integer primary key autoincrement, ");
		
		for (ColumnValues cv : t.getColumns()) {
			sb.append(cv.getName());
			sb.append(" ");
			sb.append(cv.getType());
			sb.append(", ");
		}
		sb.delete(sb.lastIndexOf(", "), sb.length());
		sb.append(")");

		Log.d("Database", sb.toString());
		db.execSQL(sb.toString());
	}
	
	public void create(Tables t) {
		SQLiteDatabase db = getWritableDatabase();
		create(t, db);
		db.close();
	}

}

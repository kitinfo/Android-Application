/**
 * 
 */
package de.kitinfo.app.data;

import de.kitinfo.app.data.Database.ColumnValues;
import de.kitinfo.app.data.Database.Tables;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.Log;

/**
 * @author mpease
 * 
 */
public class StorageProvider extends ContentProvider {

	private static final UriMatcher matcher = new UriMatcher(
			TRIM_MEMORY_MODERATE);
	public static final String AUTHORITY = "de.kitinfo.provider";
	private Database db;

	public enum UriMatch {

		/**
		 * Use this for getting timers from database
		 */
		TIMERS(1, Tables.TIMER_TABLE.getTable(), ColumnValues.TIMER_ID.getName() + " = ?"),
		IGNORE(2, "ignore_timer", null),
		RESET(3, "reset", null),
		MENSA_LINE(4, Tables.MENSA_LINE.getTable(), ColumnValues.LINE_ID.getName() + " = ?"),
		MENSA_MEAL(5, Tables.MENSA_MEAL.getTable(), ColumnValues.MEAL_DATE.getName() + " = ? AND " + ColumnValues.MEAL_LINE.getName() + " = ?"),
		DISTINCT(6, "distinct", null);

		private int code;
		private String table;
		private String where;

		// for translating uri matches
		private UriMatch(int code, String table, String where) {
			this.code = code;
			this.table = table;
			this.where = where;
		}

		/**
		 * Returns the code for the UriMatcher
		 * 
		 * @return
		 */
		public int getCode() {
			return code;
		}

		/**
		 * returns the table of the uri match object.
		 * 
		 * @return
		 */
		public String getTable() {
			return table;
		}

		/**
		 * finds the right object
		 * 
		 * @param code
		 *            the uri code given by matches.match(Uri uri)
		 * @return the UriMatch object that belongs to the given code
		 */
		public static UriMatch findMatch(int code) {
			for (UriMatch um : UriMatch.values()) {
				if (um.getCode() == code) {
					return um;
				}
			}
			return null;
		}

		public String getWhere() {
			
			return where;
		}

	}

	/**
	 * new StorageProvider object
	 */
	public StorageProvider() {
		
		for (UriMatch um : UriMatch.values()) {
			matcher.addURI(AUTHORITY, um.getTable(), um.getCode());
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {		
		
		UriMatch um = UriMatch.findMatch(matcher.match(uri));
		if (um == null) {
			return 0;
		}
		
		

		if (um == UriMatch.RESET) {
			db.reset();
			return 0;
		}
		if (uri.getEncodedFragment() != null) {

		if (uri.getEncodedFragment().equals(UriMatch.RESET.getTable())) {
			Tables t = Tables.fromString(um.getTable());
			db.drop(t);
			db.create(t);
			return 0;
		}
		}
		
		return db.rawDelete(um.getTable(), selection, selectionArgs);
		
	}

	@Override
	public String getType(Uri uri) {

		UriMatch um = UriMatch.findMatch(matcher.match(uri));

		if (um == null) {
			return null;
		}

		return "vnd.android.cursor.dir/vnd.de.kitinfo.provider."
				+ um.getTable();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		// find right table
		UriMatch um = UriMatch.findMatch(matcher.match(uri));

		if (um == null) {
			return null;
		}
		if (um == UriMatch.RESET) {
			return null;
		}
		
		
		// insert values
		long row = db.rawInsert(um.getTable(), values);

		// return uri with inserted row
		return Uri.parse(uri.toString() + "#" + row);
	}

	@Override
	public boolean onCreate() {
		db = new Database(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// find the right table
		UriMatch um = UriMatch.findMatch(matcher.match(uri));
		if (um == null) {
			return null;
		}
		if (um == UriMatch.RESET) {
			return null;
		}

		if (selection != null) {
			// check for sql injection
			//selection = DatabaseUtils.sqlEscapeString(selection);
		}
		
		boolean distinct = false;
		if (uri.getEncodedFragment() != null) {
			if (uri.getEncodedFragment().equals(UriMatch.DISTINCT.getTable())) {
				distinct = true;
			}
		}
		
		// search in database
		Cursor c = db.rawQuery(um.getTable(), projection, selection,
				selectionArgs, sortOrder, distinct);

		// check for values
		if (c.isLast()) {
			// get data from server
			// getTimerFromServer(uri);
			c.close();
			db.close();

			// new query
			Cursor cr = db.rawQuery(um.getTable(), projection, selection,
					selectionArgs, sortOrder, distinct);
			return cr;
		}
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		UriMatch um = UriMatch.findMatch(matcher.match(uri));
		if (um == null) {
			Log.d("StorageProvider|update", "matcher null");
			return 0;
		}
		return db.rawUpdate(um.getTable(), values, selection, selectionArgs);
	}

}

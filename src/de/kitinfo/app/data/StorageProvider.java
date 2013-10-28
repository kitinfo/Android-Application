/**
 * 
 */
package de.kitinfo.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author mpease
 *
 */
public class StorageProvider extends ContentProvider {

	private static final UriMatcher matcher = new UriMatcher(TRIM_MEMORY_MODERATE);
	public static final String AUTHORITY = "de.kitinfo.provider";
	
	
	public enum UriMatch {
		
		/**
		 * Use this for getting timers from database
		 */
		TIMERS(1, "timers"); 
		
		private int code;
		private String table;
		
		// for translating uri matches
		private UriMatch(int code, String table) {
			this.code = code;
			this.table = table;
		}
		
		/**
		 * Returns the code for the UriMatcher
		 * @return
		 */
		public int getCode() {
			return code;
		}
		/**
		 * returns the table of the uri match object.
		 * @return 
		 */
		public String getTable() {
			return table;
		}
		
		/**
		 * finds the right object
		 * @param code the uri code given by matches.match(Uri uri)
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
		
	}
	
	
	
	/**
	 * new StorageProvider object
	 */
	public StorageProvider() {
		matcher.addURI(AUTHORITY, "timers", 1);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		UriMatch um = UriMatch.findMatch(matcher.match(uri));
		
		if (um == null) {
			return 0;
		}
		
		Database db = new Database(getContext());
		
		return db.rawDelete(uri, selection, selectionArgs);
	}

	@Override
	public String getType(Uri uri) {
		
		UriMatch um = UriMatch.findMatch(matcher.match(uri));
		
		if (um == null) {
			return null;
		}
		
		return "vnd.android.cursor.dir/vnd.de.kitinfo.provider." + um.getTable();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		UriMatch um = UriMatch.findMatch(matcher.match(uri));
		
		if (um == null) {
			return null;
		}
		
		Database db = new Database(getContext());
		long row = db.rawInsert(um.table, values);
				
		
		return Uri.parse(uri.toString() + "/" + row);
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		UriMatch match = UriMatch.findMatch(matcher.match(uri));
		if (match == null) {
			return null;
		}
		
		if (selection.contains(";")) {
			return null;
		}
		
		Database db = new Database(getContext());
		
		Cursor c = db.rawQuery(match.getTable(), projection, selection, selectionArgs, sortOrder);
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// not implemented
		return 0;
	}

}

/**
 * 
 */
package de.kitinfo.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author mpease
 *
 */
public class StorageProvider extends ContentProvider {

	private static final UriMatcher matcher = new UriMatcher(TRIM_MEMORY_MODERATE);
	private static final String AUTHORITY = "de.kitinfo.provider";
	private static final String DBNAME = "kitinfo.db";
	private static final int DBVERSION = 1;
	
	
	public enum UriMatch {
		
		TIMERS(1, "timers");
		
		private int code;
		private String table;
		
		private UriMatch(int code, String table) {
			this.code = code;
			this.table = table;
		}
		
		public int getCode() {
			return code;
		}
		
		public String getTable() {
			return table;
		}
		
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
	 * 
	 */
	public StorageProvider() {
		matcher.addURI(AUTHORITY, "timers", 1);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return null;
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
		
		Database db = new Database(getContext(), DBNAME, null, DBVERSION);
		
		Cursor c = db.rawQuery(match.getTable(), projection, selection, selectionArgs, sortOrder);
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}

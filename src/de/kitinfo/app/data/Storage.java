package de.kitinfo.app.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import de.kitinfo.app.data.Database.ColumnValues;
import de.kitinfo.app.data.Database.Columns;
import de.kitinfo.app.timers.TimerEvent;

public class Storage {
	
	Context ctx;
	
	
	/**
	 * implements a storage system
	 * @param ctx the app context
	 */
	public Storage(Context ctx) {
		this.ctx = ctx;
	}
	
	public void saveTimers(List<TimerEvent> timers) {
		for (TimerEvent te : timers) {
			
			addTimerEvent(te);
		}
		
	}
	

	public void addTimerEvent(TimerEvent te) {
		ContentValues values = Database.getContentValues(te);
		
		Uri uri = Uri.parse(StorageContract.TIMER_URI);
		
		ContentResolver resolver = ctx.getContentResolver();
		resolver.insert(uri, values);
	}
	
	
	public void deleteTimerEvent(TimerEvent te) {
		
		Uri uri = Uri.parse(StorageContract.TIMER_URI);
		
		String where = Database.ColumnValues.TIMER_ID + "= ?";
		String[] selectionArgs = {"" + te.getID()};

		ContentResolver resolver = ctx.getContentResolver();
		resolver.delete(uri, where, selectionArgs);
	}
	
	/**
	 * returns all timers that don't be ignored.
	 * @return list of timers
	 */
	public List<TimerEvent> getTimers() {
		return getTimers(0);
	}
	
	
	private List<TimerEvent> getTimers(int ignore) {
		Uri uri = Uri.parse(StorageContract.TIMER_URI);
		
		// get the content provider
		ContentResolver resolver = ctx.getContentResolver();
		
		
		String[] protection = {ColumnValues.TIMER_DATE.getName(),
				ColumnValues.TIMER_ID.getName(),
				ColumnValues.TIMER_MESSAGE.getName(),
				ColumnValues.TIMER_TITLE.getName()};
		String where = ColumnValues.TIMER_IGNORE.getName() + " = ?";
		// get all timers
		Cursor c = resolver.query(uri, protection, where, new String[]{"" + ignore}, null);
		
		// convert to list
		List<TimerEvent> timers = convertTimerEvents(c);
		
		c.close();
		
		Collections.sort(timers);
		
		return timers;
	}
	
	/**
	 * sets an Timer on ignore
	 * @param id id of timer
	 */
	public void ignoreTimer(int id) {
		updateIgnoreFlag(id, 1);
	}
	
	/**
	 * makes an ignored timer visible again.
	 * @param id id of the timer
	 */
	public void rememberTimer(int id) {
		updateIgnoreFlag(id, 0);
	}
	
	private void updateIgnoreFlag(int id, int ignore) {
		String where = Database.ColumnValues.TIMER_IGNORE + "= ?";
		String[] selectionArgs = {"" + id};
		
		ContentValues cv = new ContentValues();
		cv.put(ColumnValues.TIMER_IGNORE.getName(), ignore);
		
		Uri uri = Uri.parse(StorageContract.IGNORE_TIMER_URI);
		
		ContentResolver resolver = ctx.getContentResolver();
		resolver.update(uri, cv, where, selectionArgs);
	}
	
	/**
	 * returns a list of ignored timers
	 * @return ignored timers
	 */
	public List<TimerEvent> getIgnoredTimers() {
		return getTimers(1);
	}
	
public List<TimerEvent> convertTimerEvents(Cursor c) {
		
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

public void reset() {
	
	ContentResolver resolver = ctx.getContentResolver();
	Uri uri = Uri.parse(StorageContract.RESET_URI);
	
	resolver.delete(uri, null, null);
	
}

	public void addCustomTimer(TimerEvent te) {
		ContentResolver resolver = ctx.getContentResolver();
		
		Uri uri = Uri.parse(StorageContract.TIMER_URI);
		
		String[] projection = {ColumnValues.TIMER_ID.getName()};
		String orderBy = "id ASC Limit 1";
		
		Cursor c = resolver.query(uri, projection, null, null, orderBy);
		
		// get 
		int id;
		if(c.moveToNext()) {
			id = c.getInt(c.getColumnIndex(ColumnValues.TIMER_ID.getName()));
		} else {
			id = -1;
		}
		c.close();
		TimerEvent newTe = new TimerEvent(te.getTitle(), te.getMessage(), id + 1, te.getDateInLong());
		addTimerEvent(newTe);
	}

}

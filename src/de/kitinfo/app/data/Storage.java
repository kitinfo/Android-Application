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
	
	/**
	 * Saves a list of timers to database. If id is in database, we update them.
	 * @param timers list of timer event objects
	 */
	public void saveTimers(List<TimerEvent> timers) {
		
		List<TimerEvent> databaseList = getTimers();
		databaseList.addAll(getIgnoredTimers());
		
		List<TimerEvent> withoutCustomTimers = new LinkedList<TimerEvent>();
		
		// remove custom timer
		for (TimerEvent t : databaseList) {
			if (t.getID() >= 0) {
				withoutCustomTimers.add(t);
			}
		}
		
		// add timers to database
		for (TimerEvent te : timers) {
			
			addTimerEvent(te);
			// remove timer because we have update them, don't care if not in list
			withoutCustomTimers.remove(te);
		}
		
		// delete timers not in list from server
		for (TimerEvent t : withoutCustomTimers) {
			deleteTimerEvent(t);
		}
		
	}
	
	/**
	 * adds a timer event to database.
	 * @param te timer event object
	 */
	public void addTimerEvent(TimerEvent te) {
		ContentValues values = Database.getContentValues(te);
		
		Uri uri = Uri.parse(StorageContract.TIMER_URI);
		
		ContentResolver resolver = ctx.getContentResolver();
		resolver.insert(uri, values);
	}
	
	/**
	 * delete the given timerevent
	 * @param te timer event object
	 */
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
	
	
	/* 
	 * returns timer with ignore status
	 * @param ignore 1 for get the ignored timers, 0 for getting the others
	 */
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
		for (TimerEvent t : timers) {
			Log.d("Timers", "timestamp: " + t.getDateInLong());
		}
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
	
	/*
	 *  updates the ignore flag of a timer
	 *  @param id id of the timer event
	 *  @param ignore 1 for ignoring the timer, 0 for not ignoring the timer
	 */
	private void updateIgnoreFlag(int id, int ignore) {
		String where = Database.ColumnValues.TIMER_ID.getName() + "= ?";
		String[] selectionArgs = {"" + id};
		
		ContentValues cv = new ContentValues();
		cv.put(ColumnValues.TIMER_IGNORE.getName(), ignore);
		
		Uri uri = Uri.parse(StorageContract.TIMER_URI);
		
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
	
	/**
	 * converts timer events from cursor to his object structure.
	 * @param c cursor with timer events
	 * @return list of timer event objects
	 */
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

/**
 * resets the database.
 */
public void reset() {
	
	ContentResolver resolver = ctx.getContentResolver();
	Uri uri = Uri.parse(StorageContract.RESET_URI);
	
	resolver.delete(uri, null, null);
	
}
	/**
	 * Adds a custom timer to database.
	 * @param te timer event
	 */
	public void addCustomTimer(TimerEvent te) {
		ContentResolver resolver = ctx.getContentResolver();
		
		Uri uri = Uri.parse(StorageContract.TIMER_URI);
		
		String[] projection = {ColumnValues.TIMER_ID.getName()};
		String orderBy = "id ASC Limit 1";
		
		Cursor c = resolver.query(uri, projection, null, null, orderBy);
		
		// get 
		int id = -1;
		if(c.moveToNext()) {
			id = c.getInt(c.getColumnIndex(ColumnValues.TIMER_ID.getName()));
		}
		
		if (id >= 0) {
			id = -1;
		}
		
		c.close();
		TimerEvent newTe = new TimerEvent(te.getTitle(), te.getMessage(), id -1, te.getDateInLong());
		addTimerEvent(newTe);
	}

}

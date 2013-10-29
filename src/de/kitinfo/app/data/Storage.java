package de.kitinfo.app.data;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.UserDictionary;
import android.util.Log;
import de.kitinfo.app.data.Database.ColumnValues;
import de.kitinfo.app.timers.JsonParser_TimeEvent;
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
				
		Uri uri = Uri.parse(StorageContract.TIMER_URI);
		
		// get the content provider
		ContentResolver resolver = ctx.getContentResolver();
		
		// get all timers
		Cursor c = resolver.query(uri, null, null, null, null);
		
		// convert to list
		List<TimerEvent> timers = convertTimerEvents(c);
		
		c.close();
		
		uri = Uri.parse(StorageContract.IGNORE_TIMER_URI);
		
		// get all ids from ignored timers
		Cursor cv = resolver.query(uri, null, null, null, null);
		
		List<Integer> ids = new LinkedList<Integer>();
		
		// add ids to list
		while (cv.moveToNext()) {
			ids.add(cv.getInt(cv.getColumnIndex(Database.ColumnValues.TIMER_IGNORE_ID.getName())));
		}
		cv.close();
		
		// new events
		List<TimerEvent> newEvents = new LinkedList<TimerEvent>();
		
		// for every timer event
		for (TimerEvent t : timers) {
			boolean test = true;
			
			// check if id is in ignore list
			for (int id : ids) {
				if (id == t.getID()) {
					test = false;
				}
			}
			
			// if don't, add to new event list
			if (test) {
				newEvents.add(t);
			}
		}
		
		return newEvents;
	}
	
	/**
	 * sets an Timer on ignore
	 * @param id id of timer
	 */
	public void ignoreTimer(int id) {
		Log.d("Storage", "Ignore timer: " + id);
		ContentValues values = new ContentValues();
		
		values.put(Database.ColumnValues.TIMER_IGNORE_ID.getName(), "" + id);
		
		Uri uri = Uri.parse(StorageContract.IGNORE_TIMER_URI);
		
		ContentResolver resolver = ctx.getContentResolver();
		resolver.insert(uri, values);
	}
	
	/**
	 * makes an ignored timer visible again.
	 * @param id id of the timer
	 */
	public void rememberTimer(int id) {
		
		String where = Database.ColumnValues.TIMER_IGNORE_ID + "= ?";
		String[] selectionArgs = {"" + id};
		
		Uri uri = Uri.parse(StorageContract.IGNORE_TIMER_URI);
		
		ContentResolver resolver = ctx.getContentResolver();
		resolver.delete(uri, where, selectionArgs);
	}
	
	/**
	 * returns a list of ignored timers
	 * @return ignored timers
	 */
	public List<TimerEvent> getIgnoredTimers() {
		
		List<TimerEvent> fullTimerList = getTimers();
		
		ContentResolver resolver = ctx.getContentResolver();
		
		// uri for ignored timers
		Uri uri = Uri.parse(StorageContract.IGNORE_TIMER_URI);
		
		// get list of ignored timers
		Cursor c = resolver.query(uri, null, null, null, null);
		
		List<TimerEvent> timerList = new LinkedList<TimerEvent>();
		
		// get all timers stand on ignore list
		while (c.moveToNext()) {
			for (TimerEvent t : fullTimerList) {
				if (t.getID() == c.getInt(c.getColumnIndex(Database.ColumnValues.TIMER_IGNORE_ID.getName()))) {
					timerList.add(t);
				}
			}
		}	
		c.close();
				
				
		return timerList;
		
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
}

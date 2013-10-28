package de.kitinfo.app.data;

import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.UserDictionary;
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
		
		Uri uri = Uri.parse("content://" + StorageProvider.AUTHORITY + "/timers");
		
		ContentResolver resolver = ctx.getContentResolver();
		resolver.insert(uri, values);
	}
	
	public List<TimerEvent> getTimers() {
				
		Uri uri = Uri.parse("content://" + StorageProvider.AUTHORITY + "/timers");
		
		ContentResolver resolver = ctx.getContentResolver();
		
		Cursor c = resolver.query(uri, null, null, null, null);
		
		List<TimerEvent> timers = Database.convertTimerEvents(c);
		
		c.close();
		
		return timers;
	}
	
	

	
}

package de.kitinfo.app.timers;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentResolver;
import de.kitinfo.app.TimeFunctions;

/**
 * This class provides a data structure for a timer elements which represents an
 * event in the future or the past.
 * 
 * @author Indidev
 * 
 */
public class TimerEvent implements Serializable, Comparable<TimerEvent> {

	private static final long serialVersionUID = 3387055649275802046L;
	private String title;
	private String message;
	private long date;
	private int id;

	/**
	 * builds a new event
	 * 
	 * @param title
	 *            title
	 * @param message
	 *            message, will be displayed, if time left < 0
	 * @param id
	 *            id of this event
	 * @param year
	 *            year
	 * @param month
	 *            month
	 * @param day
	 *            day
	 * @param hour
	 *            hour
	 * @param minute
	 *            minute
	 * @param second
	 *            scond
	 */
	public TimerEvent(String title, String message, int id, int year,
			int month, int day, int hour, int minute, int second) {
		this.title = title;
		this.message = message;
		this.id = id;
		date = TimeFunctions.getMillis(year, month, day, hour, minute, second);

	}

	public TimerEvent(String title, String message, int id, long date) {
		this.title = title;
		this.message = message;
		this.id = id;
		this.date = date;
	}

	/**
	 * returns the date of the time event
	 * 
	 * @return the date of the event represented in a string
	 */
	public String getDate() {
		// german time string
		// return day + "." + month + "." + year + " " + hour + ":" + minute;
		return TimeFunctions.toLocalTime(date, SimpleDateFormat.MEDIUM,
				SimpleDateFormat.SHORT);
	}

	public long getDateInLong() {
		return date;
	}

	/**
	 * the remaining time in seconds
	 * 
	 * @return the remaining time in seconds
	 */
	public long getRemainingTime() {

		return ((new Date(date)).getTime() - System.currentTimeMillis()) / 1000;
	}

	/**
	 * returns the title of this event
	 * 
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * returns the message of this event, which should be displayed, if remainig
	 * time < 0
	 * 
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * returns the id of this event
	 * 
	 * @return id
	 */
	public int getID() {
		return id;
	}

	@Override
	public int compareTo(TimerEvent another) {
		return (date - another.getDateInLong()) > 0 ? 1 : -1;
	}

	@Override
	public boolean equals(Object o) {

		// null check
		if (o == null) {
			return false;
		}

		// save reference
		if (o == this) {
			return true;
		}
		// not the right class
		if (o.getClass() != getClass()) {
			return false;
		}
		if (((TimerEvent) o).getID() == getID()) {
			return true;
		}

		return false;

	}
}

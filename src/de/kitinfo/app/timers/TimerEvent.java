package de.kitinfo.app.timers;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class provides a data structure for a timer elements which represents an
 * event in the future or the past.
 * 
 * @author indidev
 * 
 */
public class TimerEvent implements Serializable {

	private static final long serialVersionUID = 3387055649275802046L;
	private String title;
	private String message;
	private int id;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;

	/**
	 * constructor
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
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	/**
	 * returns the date of the time event
	 * 
	 * @return the date of the event represented in a string
	 */
	public String getDate() {
		// german time string
		// return day + "." + month + "." + year + " " + hour + ":" + minute;
		return SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM,
				SimpleDateFormat.SHORT).format(
				new Date(year - 1900, month - 1, day, hour, minute, second));
	}

	/**
	 * the remaining time in seconds
	 * 
	 * @return the remaining time in seconds
	 */
	@SuppressWarnings("deprecation")
	public long getRemainingTime() {

		return ((new Date(year - 1900, month - 1, day, hour, minute, second))
				.getTime() - System.currentTimeMillis()) / 1000;
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
}

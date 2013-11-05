package de.kitinfo.app;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeConverter {

	private TimeConverter() {

	}

	/**
	 * parse time in readable form
	 * 
	 * @param timeInMillis
	 *            time in milliseconds
	 * @param dateFormat
	 *            i.e. SimpleDateFormat.MEDIUM
	 * @param timeFormat
	 *            i.e. SimpleDateFormat.SHORT
	 * @return time in human readable form
	 */
	public static String toLocalTime(long timeInMillis, int dateFormat,
			int timeFormat) {
		String timeString = "";

		return SimpleDateFormat.getDateTimeInstance(dateFormat, timeFormat)
				.format(new Date(timeInMillis));
	}
}

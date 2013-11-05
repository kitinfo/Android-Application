package de.kitinfo.app;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeConverter {

	private TimeConverter() {

	}

	public static String toLocalTime(long timeInMillis, int dateFormat,
			int timeFormat) {
		String timeString = "";

		return SimpleDateFormat.getDateTimeInstance(dateFormat, timeFormat)
				.format(new Date(timeInMillis));
	}
}

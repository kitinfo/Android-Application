package de.kitinfo.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeFunctions {

	public static enum Format {
		DAY_HOUR_MINUTE_SECOND, HOUR_MINUTE_SECOND, MINUTE_SECOND, SECOND
	}

	private TimeFunctions() {

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
		return SimpleDateFormat.getDateTimeInstance(dateFormat, timeFormat)
				.format(new Date(timeInMillis));
	}

	public static long getMillis(int year, int month, int day, int hour,
			int minute) {
		return getMillis(year, month, day, hour, minute, 0);
	}

	public static long getMillis(int year, int month, int day, int hour,
			int minute, int second) {
		return new GregorianCalendar(year, month, day, hour, minute, second)
				.getTimeInMillis();
	}

	public static String formatTime(long millis, Format format) {
		String readable = "";

		long day = millis / 86400;
		long hour = (millis % 86400) / 3600;
		long minute = (millis % 3600) / 60;
		long second = millis % 60;

		switch (format) {
		case DAY_HOUR_MINUTE_SECOND:
			readable = String.format("%02d:%02d:%02d:%02d", day, hour, minute,
					second);
			break;
		case HOUR_MINUTE_SECOND:
			hour += day * 24;
			readable = String.format("%02d:%02d:%02d", hour, minute, second);
			break;
		case MINUTE_SECOND:
			minute += day * 1440 + hour * 60;
			readable = String.format("%02d:%02d", minute, second);
			break;
		case SECOND:
			second += day * 86400 + hour * 3600 + minute * 60;
			readable = String.format("%02d", second);
			break;
		default:
		}

		return readable;
	}

	public static long getDayInMillis(long datetime) {

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(datetime);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTimeInMillis();
	}

	public static boolean isSameDay(long datetime1, long datetime2) {
		return getDayInMillis(datetime1) == getDayInMillis(datetime2);
	}
}

package de.kitinfo.app.status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * 
 * JSON Parser to parse time events out of the timer api
 * 
 * @author mpease
 * 
 */
public class JsonParser_Status {
	public enum Tags {

		CHANNEL("channel"), USER("user"), TIME("time");

		private String tag;

		private Tags(String tag) {
			this.tag = tag;
		}

		public String toString() {
			return tag;
		}

	}

	/**
	 * parse names out of json
	 * 
	 * @param data
	 *            json string
	 * @return sorted list of nicknames
	 */
	public String[] parseNames(String data) {
		data = data.replace("[\"", "");
		data = data.replace("\"]", "");

		String[] nicks = data.split("\",\"");
		Arrays.sort(nicks, new StringComperator());
		return nicks;
	}

	/**
	 * parse last message data
	 * 
	 * @param data
	 *            data to parse
	 * @return parsed input (channel : user @ time)
	 */
	public String parseLastMessage(String data) {
		String message = "";

		JSONObject dataObject;
		try {
			dataObject = new JSONObject(data);

			String channel = android.text.Html.fromHtml(
					dataObject.getString(Tags.CHANNEL.toString())).toString();

			String user = android.text.Html.fromHtml(
					dataObject.getString(Tags.USER.toString())).toString();

			String time = android.text.Html.fromHtml(
					dataObject.getString(Tags.TIME.toString())).toString();

			Date date = parseRFC3339Date(time);

			time = SimpleDateFormat.getDateTimeInstance(
					SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM).format(
					date);

			message = channel + ":\n" + user + " @ " + time;

		} catch (JSONException e) {
			Log.e("JsonParser_Status|parseLastMessage", e.toString());
		}

		return message;
	}

	private class StringComperator implements Comparator<String> {

		@Override
		public int compare(String lhs, String rhs) {
			return lhs.toLowerCase().compareTo(rhs.toLowerCase());
		}

	}

	public static java.util.Date parseRFC3339Date(String datestring) {
		Date d = new Date();

		// if there is no time zone, we don't need to do any special parsing.
		if (datestring.endsWith("Z")) {
			try {
				SimpleDateFormat s = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss'Z'");// spec for RFC3339
				d = s.parse(datestring);
			} catch (java.text.ParseException pe) {// try again with optional
													// decimals
				SimpleDateFormat s = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");// spec for RFC3339
															// (with fractional
															// seconds)
				s.setLenient(true);
				try {
					d = s.parse(datestring);
				} catch (ParseException e) {
					Log.e("JsonParser_Status|parseRFC...", e.toString());
				}
			}
			return d;
		}

		// step one, split off the timezone.
		String firstpart = datestring.substring(0, datestring.lastIndexOf('-'));
		String secondpart = datestring.substring(datestring.lastIndexOf('-'));

		// step two, remove the colon from the timezone offset
		secondpart = secondpart.substring(0, secondpart.indexOf(':'))
				+ secondpart.substring(secondpart.indexOf(':') + 1);
		datestring = firstpart + secondpart;
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");// spec
																			// for
																			// RFC3339
		try {
			d = s.parse(datestring);
		} catch (java.text.ParseException pe) {// try again with optional
												// decimals
			s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");// spec
																		// for
																		// RFC3339
																		// (with
																		// fractional
																		// seconds)
			s.setLenient(true);
			try {
				d = s.parse(datestring);
			} catch (ParseException e) {
				Log.e("JsonParser_Status|parseRFC...", e.toString());
			}
		}
		return d;
	}
}

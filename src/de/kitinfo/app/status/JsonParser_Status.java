package de.kitinfo.app.status;

import java.util.Arrays;
import java.util.Comparator;

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

			message = channel + " : " + user + "@" + time;

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
}

package de.kitinfo.app.timers;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
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
public class JsonParser_TimeEvent {
	public enum Tags {

		ID("id"), TIMERS("timers"), EVENT("event"), MESSAGE("message"), DAY(
				"day"), MONTH("month"), YEAR("year"), HOUR("hour"), MINUTE(
				"minute"), SECOND("second");

		private String tag;

		private Tags(String tag) {
			this.tag = tag;
		}

		public String toString() {
			return tag;
		}

	}

	/**
	 * method to parse events in the json format to timer event objects and
	 * store it into a list of events
	 * 
	 * @param data
	 *            events in the json format (for more info, take a look at <a
	 *            href="http://timers.kitinfo.de/timerapi.php">http://timers.
	 *            kitinfo.de/timerapi.php</a> )
	 * @return list of timer events
	 */
	public List<TimerEvent> parse(String data) {

		List<TimerEvent> timerList = new LinkedList<TimerEvent>();

		JSONObject dataObject;
		try {
			dataObject = new JSONObject(data);

			JSONArray timersArr = dataObject.getJSONArray("timers");

			for (int i = 0; i < timersArr.length(); i++) {

				JSONObject timerObj = timersArr.getJSONObject(i);

				int id = timerObj.getInt(Tags.ID.toString());
				String event = android.text.Html.fromHtml(
						timerObj.getString(Tags.EVENT.toString())).toString();
				String message = android.text.Html.fromHtml(
						timerObj.getString(Tags.MESSAGE.toString())).toString();
				int day = timerObj.getInt(Tags.DAY.toString());
				int month = timerObj.getInt(Tags.MONTH.toString()) -1;
				int year = timerObj.getInt(Tags.YEAR.toString());
				int hour = timerObj.getInt(Tags.HOUR.toString());
				int minute = timerObj.getInt(Tags.MINUTE.toString());
				int second = timerObj.getInt(Tags.SECOND.toString());

				TimerEvent timer = new TimerEvent(event, message, id, year,
						month, day, hour, minute, second);

				timerList.add(timer);
			}
		} catch (JSONException e) {
			Log.e("JsonParser_TimeEvent|parse", e.toString());
		}
		return timerList;
	}
}

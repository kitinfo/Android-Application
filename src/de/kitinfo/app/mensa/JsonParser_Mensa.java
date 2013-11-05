package de.kitinfo.app.mensa;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonParser_Mensa {
	public enum Tags {

		MENSA("adenauerring"), NODATA("nodata"), BIO("bio"), FISH("fish"), PORK(
				"pork"), BEEF("cow"), N_T_BEEF("cow_aw"), VEGAN("vegan"), VEGGIE(
				"veg"), INFO("info"), PRICE("price_4"), NAME("meal"), HINT(
				"add");

		private String tag;

		private Tags(String tag) {
			this.tag = tag;
		}

		public String toString() {
			return tag;
		}

	}

	public List<MensaDay> parseMensaData(String json) {
		List<MensaDay> mensaWeek = new LinkedList<MensaDay>();

		JSONObject dataObject;

		try {
			dataObject = new JSONObject(json);

			JSONArray timersArr = dataObject
					.getJSONArray(Tags.MENSA.toString());

			for (int i = 0; i < timersArr.length(); i++) {

				JSONObject day = timersArr.getJSONObject(i);
				Log.d("JsonParser_Mensa|parseMensaData", day.toString(0)); // TODO
																			// Test!!

				// JSONObject timerObj = timersArr.getJSONObject(i);
				//
				// int id = timerObj.getInt(Tags.ID.toString());
				// String event = android.text.Html.fromHtml(
				// timerObj.getString(Tags.EVENT.toString())).toString();
				// String message = android.text.Html.fromHtml(
				// timerObj.getString(Tags.MESSAGE.toString())).toString();
				// int day = timerObj.getInt(Tags.DAY.toString());
				// int month = timerObj.getInt(Tags.MONTH.toString()) -1;
				// int year = timerObj.getInt(Tags.YEAR.toString());
				// int hour = timerObj.getInt(Tags.HOUR.toString());
				// int minute = timerObj.getInt(Tags.MINUTE.toString());
				// int second = timerObj.getInt(Tags.SECOND.toString());
				//
				// TimerEvent timer = new TimerEvent(event, message, id, year,
				// month, day, hour, minute, second);
				//
				// timerList.add(timer);
			}
		} catch (JSONException e) {
			Log.e("JsonParser_Mensa|parse", e.toString());
		}

		return mensaWeek;
	}
}

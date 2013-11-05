package de.kitinfo.app.mensa;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import de.kitinfo.app.data.JSONParser;

public class JsonParser_Mensa implements JSONParser<List<MensaDay>> {

	private HashMap<String, String> lineConverter = new LineConverter();

	private class LineConverter extends HashMap<String, String> {
		private LineConverter() {
			this.put("l1", "Linie 1");
			this.put("l2", "Linie 2");
			this.put("l3", "Linie 3");
			this.put("l45", "Linie 4/5");
			this.put("update", "L6 Update");
			this.put("abend", "Abend");
			this.put("aktion", "Curry Queen");
			this.put("nmtisch", "Cafeteria ab 14:30");
			this.put("heisstheke", "Cafeteria Heiße Theke");
			this.put("schnitzelbar", "Schnitzelbar");

		}
	}

	public enum Tags {

		MENSA("adenauerring"), NODATA("nodata"), BIO("bio"), FISH("fish"), PORK(
				"pork"), BEEF("cow"), N_T_BEEF("cow_aw"), VEGAN("vegan"), VEGGIE(
				"veg"), INFO("info"), PRICE("price_4"), NAME("meal"), HINT(
				"dish"), ADD("add"), CLOSING_END("closing_end"), CLOSING_START(
				"closing_start"), SINGLE_PRICE("price_1"), PRICE_FLAG(
				"price_flag");

		private String tag;

		private Tags(String tag) {
			this.tag = tag;
		}

		public String toString() {
			return tag;
		}

	}

	public List<MensaDay> parse(String json) {
		List<MensaDay> mensaWeek = new LinkedList<MensaDay>();

		JSONObject dataObject;

		try {
			dataObject = new JSONObject(json);
			// Log.d("JsonParser_Mensa|parseMensaData", dataObject.toString());

			JSONObject mensaObject = dataObject.getJSONObject(Tags.MENSA
					.toString());

			Iterator<String> mensaItterator = mensaObject.keys();

			// iterate over days
			while (mensaItterator.hasNext()) {

				String mensaKey = mensaItterator.next();

				MensaDay day = new MensaDay(new Long(mensaKey.trim()) * 1000);

				JSONObject mensaDayObject = mensaObject.getJSONObject(mensaKey);

				// Log.d("JsonParser_Mensa|parse", mensaDayObject.toString());

				Iterator<String> dayItterator = mensaDayObject.keys();

				// iterate over lines
				while (dayItterator.hasNext()) {

					String dayKey = dayItterator.next();

					MensaLine line = new MensaLine(
							lineConverter.containsKey(dayKey) ? lineConverter
									.get(dayKey) : dayKey);

					// Log.d("JsonParser_Mensa|parse", dayKey + ":");
					JSONArray mensaLineArray = mensaDayObject
							.getJSONArray(dayKey);

					// iterate over meals
					for (int i = 0; i < mensaLineArray.length(); i++) {

						JSONObject mealObject = mensaLineArray.getJSONObject(i);

						if (!(mealObject.has(Tags.NODATA.toString())
								|| mealObject
										.has(Tags.CLOSING_START.toString()) || mealObject
									.has(Tags.CLOSING_END.toString()))) {

							boolean veggie = mealObject.getBoolean(Tags.VEGGIE
									.toString());
							boolean vegan = mealObject.getBoolean(Tags.VEGAN
									.toString());
							boolean bio = mealObject.getBoolean(Tags.BIO
									.toString());
							boolean pork = mealObject.getBoolean(Tags.PORK
									.toString());
							boolean fish = mealObject.getBoolean(Tags.FISH
									.toString());
							boolean beef = mealObject.getBoolean(Tags.BEEF
									.toString());
							boolean nTBeef = mealObject
									.getBoolean(Tags.N_T_BEEF.toString());

							float price;

							String priceFlag = mealObject
									.getString(Tags.PRICE_FLAG.toString());

							if ("0".equals(priceFlag))
								price = (float) mealObject.getDouble(Tags.PRICE
										.toString());
							else
								price = (float) mealObject
										.getDouble(Tags.SINGLE_PRICE.toString());

							String name = android.text.Html.fromHtml(
									mealObject.getString(Tags.NAME.toString()))
									.toString();
							String hint = android.text.Html.fromHtml(
									mealObject.getString(Tags.HINT.toString()))
									.toString();
							String info = android.text.Html.fromHtml(
									mealObject.getString(Tags.INFO.toString()))
									.toString();

							List<String> adds = new LinkedList<String>();

							JSONArray addsArray = mealObject
									.getJSONArray(Tags.ADD.toString());

							for (int j = 0; j < addsArray.length(); j++) {
								adds.add(addsArray.getString(j));
							}

							MensaMeal meal = new MensaMeal(veggie, vegan, bio,
									pork, fish, beef, nTBeef, name, hint, info,
									price, adds);

							line.addMeal(meal);
						}
					}

					day.addLine(line);
				}

				mensaWeek.add(day);

			}

			// dataObject = new JSONObject(json);

			// JSONArray timersArr = dataObject
			// .getJSONArray(Tags.MENSA.toString());
			//
			// for (int i = 0; i < timersArr.length(); i++) {
			//
			// JSONObject day = timersArr.getJSONObject(i);
			// Log.d("JsonParser_Mensa|parseMensaData", day.toString(0)); //
			// TODO
			// // Test!!
			//
			// // JSONObject timerObj = timersArr.getJSONObject(i);
			// //
			// // int id = timerObj.getInt(Tags.ID.toString());
			// // String event = android.text.Html.fromHtml(
			// // timerObj.getString(Tags.EVENT.toString())).toString();
			// // String message = android.text.Html.fromHtml(
			// // timerObj.getString(Tags.MESSAGE.toString())).toString();
			// // int day = timerObj.getInt(Tags.DAY.toString());
			// // int month = timerObj.getInt(Tags.MONTH.toString()) -1;
			// // int year = timerObj.getInt(Tags.YEAR.toString());
			// // int hour = timerObj.getInt(Tags.HOUR.toString());
			// // int minute = timerObj.getInt(Tags.MINUTE.toString());
			// // int second = timerObj.getInt(Tags.SECOND.toString());
			// //
			// // TimerEvent timer = new TimerEvent(event, message, id, year,
			// // month, day, hour, minute, second);
			// //
			// // timerList.add(timer);
			// }
		} catch (JSONException e) {
			Log.e("JsonParser_Mensa|parse", e.toString());
		}

		return mensaWeek;
	}
}

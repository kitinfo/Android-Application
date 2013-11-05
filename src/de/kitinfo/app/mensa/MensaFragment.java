package de.kitinfo.app.mensa;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import de.kitinfo.app.IOManager;
import de.kitinfo.app.Slide;
import de.kitinfo.app.TimeConverter;

public class MensaFragment extends Fragment implements Slide {

	private static final String API_URL = "http://www.studentenwerk-karlsruhe.de/de/json_interface/canteen/?mensa[]=adenauerring";
	private static final String TITLE = "Mensa am Adenauerring";

	private int id;

	public MensaFragment() {
		id = -3;
	}

	@Override
	public void update() {
		// TODO Automatisch generierter Methodenstub

	}

	@Override
	public String getTitle() {
		return TITLE;
	}

	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public boolean isExpandable() {
		return false;
	}

	@Override
	public void addElement(Context context) {
	}

	@Override
	public void updateContent(Context context) {
		// TODO Automatisch generierter Methodenstub

	}

	@Override
	public void querryData(Context context) {
		List<MensaDay> mensaDays = new JsonParser_Mensa().parse(new IOManager()
				.queryJSON(API_URL));

		for (MensaDay day : mensaDays) {
			if (day.getDateTime() == TimeConverter.getDayInMillis(System
					.currentTimeMillis())) {
				Log.d("MensaFragment|querryData",
						"Today's ("
								+ TimeConverter.toLocalTime(day.getDateTime(),
										SimpleDateFormat.MEDIUM,
										SimpleDateFormat.SHORT) + ") meals:");

				for (MensaLine line : day.getLines()) {
					Log.d("MensaFragment|querryData",
							"Line : " + line.getName());

					for (MensaMeal meal : line.getMeals()) {
						Log.d("MensaFragment|querryData", "Meal : " + meal);
					}
				}
			}
		}
	}
}

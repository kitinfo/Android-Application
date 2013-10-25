package de.kitinfo.app;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;
import de.kitinfo.app.TimeManager.TimeListener;
import de.kitinfo.app.timers.TimerViewFragment;

public class ReferenceManager {

	@SuppressWarnings("unused")
	private static final ReferenceManager RM = new ReferenceManager();
	public static MainActivity MA;
	public static List<Slide> SLIDES;
	public static List<TimeListener> SLIDES_TO_UPDATE;
	public static TimeManager TM;

	public static TimerViewFragment TVF;

	private ReferenceManager() {
		SLIDES_TO_UPDATE = new LinkedList<TimeManager.TimeListener>();
		SLIDES = new LinkedList<Slide>();
	}

	/**
	 * register a TimeListener to be update on an event
	 * 
	 * @param listener
	 *            listener to update
	 */
	public static void register(TimeListener listener) {
		if (SLIDES_TO_UPDATE != null) {
			SLIDES_TO_UPDATE.add(listener);
			Log.d("MainActivity | registration", "sucessfull");
		}
	}

	/**
	 * unregister a TimeListener
	 * 
	 * @param listener
	 *            TimeListener to unregister
	 */
	public static void unregister(TimeListener listener) {
		SLIDES_TO_UPDATE.remove(listener);
	}

	public static void addSlide(Slide s) {
		boolean found = false;
		for (int i = 0; i < SLIDES.size(); i++) {
			if (SLIDES.get(i).getID() == s.getID()) {
				found = true;
				SLIDES.add(i + 1, s);
				SLIDES.remove(i);
			}
		}
		if (!found) {
			SLIDES.add(s);
		}
	}

}

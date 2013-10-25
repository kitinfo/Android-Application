package de.kitinfo.app;

import java.util.LinkedList;
import java.util.List;

import de.kitinfo.app.TimeManager.Updatable;
import de.kitinfo.app.timers.TimerViewFragment;

/**
 * provides the current references to the most objects (still beta, don't hate
 * me for that...)
 * 
 * 
 * @author Indidev
 * 
 */
public class ReferenceManager {

	@SuppressWarnings("unused")
	private static final ReferenceManager RM = new ReferenceManager();
	public static MainActivity MA;
	public static List<Slide> SLIDES;
	public static List<Updatable> SLIDES_TO_UPDATE;
	public static TimeManager TM;

	public static TimerViewFragment TVF;

	private ReferenceManager() {
		SLIDES_TO_UPDATE = new LinkedList<TimeManager.Updatable>();
		SLIDES = new LinkedList<Slide>();
	}

	/**
	 * register a Updatable to be update on an event
	 * 
	 * @param listener
	 *            listener to update
	 */
	public static void register(Updatable listener) {
		if (SLIDES_TO_UPDATE != null)
			SLIDES_TO_UPDATE.add(listener);
	}

	/**
	 * unregister a Updatable
	 * 
	 * @param listener
	 *            Updatable to unregister
	 */
	public static void unregister(Updatable listener) {
		SLIDES_TO_UPDATE.remove(listener);
	}

	/**
	 * add a slide to the list (replaces a slide with the same id, so make sure
	 * you set the id if you want so)
	 * 
	 * @param s
	 *            slide to add
	 */
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

package de.kitinfo.app;

import java.util.LinkedList;
import java.util.List;

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
	public static TimeManager TM;

	private ReferenceManager() {
		SLIDES = new LinkedList<Slide>();
	}

	/**
	 * Add a slide to the list
	 * 
	 * @param s
	 *            Slide to add
	 */
	public static void addSlide(Slide s) {
		SLIDES.add(s);
	}

	/**
	 * update a slide in the list (replaces a slide with the same id, so make
	 * sure you set the id if you want so)
	 * 
	 * @param s
	 *            slide to update
	 */
	public static void updateSlide(Slide s) {
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

package de.kitinfo.app;

import android.content.Context;
import android.support.v4.app.Fragment;
import de.kitinfo.app.TimeManager.Updatable;

/**
 * Each slide should implement this, it is very important,...
 * 
 * @author Indidev
 * 
 */
public interface Slide extends Updatable {

	/**
	 * get the title of a slide, this will be displayed as tab name
	 * 
	 * @return title of a slide
	 */
	public String getTitle();

	/**
	 * fragment of the slide, used to display the content of the slide
	 * 
	 * @return fragment of the slide
	 */
	public Fragment getFragment();

	/**
	 * the id is used to prevent the app to display multiple instances of one
	 * slide
	 * 
	 * @return the slides id
	 */
	public int getID();

	/**
	 * whether the slide is expandable or not, if it is expandable, the
	 * expandable item will be shown in the action bar
	 * 
	 * @return whether the slide is expandable
	 */
	public boolean isExpandable();

	/**
	 * add an Element (for example an event to timers)
	 * 
	 * @param context
	 *            context, is maybe needed
	 */
	public void addElement(Context context);
}

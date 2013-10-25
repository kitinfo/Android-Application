package de.kitinfo.app;

import android.support.v4.app.Fragment;

/**
 * Each slide should implement this, it is very important,...
 * 
 * @author Indidev
 * 
 */
public interface Slide {

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
}

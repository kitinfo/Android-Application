/**
 * 
 */
package de.kitinfo.app.data;

import de.kitinfo.app.data.StorageProvider.UriMatch;

/**
 * @author mpease
 *
 */
public class StorageContract {

	public static final String AUTHORITY = "de.kitinfo.provider";
	public static final String PROVIDER_URI = "content://" + AUTHORITY;
	
	public static final String TIMER_URI = PROVIDER_URI + "/" + UriMatch.TIMERS.getTable();
	
	public static final String IGNORE_TIMER_URI = PROVIDER_URI + "/" + UriMatch.IGNORE.getTable();
	
	public static final String RESET_URI = PROVIDER_URI + "/" + UriMatch.RESET.getTable();
	
	public static final String MENSA_LINE_URI = PROVIDER_URI + "/" + UriMatch.MENSA_LINE.getTable();
	
	public static final String MEAL_URI = PROVIDER_URI + "/" + UriMatch.MENSA_MEAL.getTable();
	
	public static final String TIMER_RESET_URI = TIMER_URI + "#" + UriMatch.RESET.getTable();
	
	public static final String MENSA_LINE_RESET_URI = MENSA_LINE_URI + "#" + UriMatch.RESET.getTable();
	
	public static final String MENSA_MEAL_RESET_URI = MEAL_URI + "#" + UriMatch.RESET.getTable();
	
	public static final String MENSA_MEAL_DISTINCT_URI = MEAL_URI + "#" + UriMatch.DISTINCT.getTable();

}

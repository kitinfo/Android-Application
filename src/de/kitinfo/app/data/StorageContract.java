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
	
	
	/**
	 * 
	 */
	public StorageContract() {
		// TODO Auto-generated constructor stub
	}

}

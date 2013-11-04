/**
 * 
 */
package de.kitinfo.app.data;

import java.util.List;

/**
 * @author mpease
 * @param <T>
 *
 */
public interface JSONParser<T> {

	
	public List<T> parse(String data);
	
}

/**
 * 
 */
package de.kitinfo.app.data;


/**
 * @author mpease
 * @param <T>
 * 
 */
public interface JSONParser<T> {

	public T parse(String data);

}

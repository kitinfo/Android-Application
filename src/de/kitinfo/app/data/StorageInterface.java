/**
 * 
 */
package de.kitinfo.app.data;

import java.util.List;

/**
 * @author mpease
 *
 */
public interface StorageInterface<T> {
	
	public void add(List<T> t);
	public void add(T t);
	public List<T> getAll();
	public T get(T t);
	public int delete(T t);
	public int delete(List<T> t);
	public void reset();
}

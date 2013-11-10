/**
 * 
 */
package de.kitinfo.app.mensa;

/**
 * @author mpease
 *
 */
public enum Mensa {

	ADENAUER("Mensa am Adenauerring");
	
	private String name;
	
	private Mensa(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}

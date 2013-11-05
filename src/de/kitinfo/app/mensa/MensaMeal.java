package de.kitinfo.app.mensa;

import java.io.Serializable;

public class MensaMeal implements Serializable {

	private static final long serialVersionUID = 6318652233903651125L;

	private boolean veggie;
	private boolean vegan;
	private boolean bio;
	private boolean pork;
	private boolean fish;
	private boolean beef;
	private boolean nonTorturedBeef;

	private String name;
	private String hint;
	private String info;

	private float price;

	public MensaMeal(boolean veggie, boolean vegan, boolean bio, boolean pork,
			boolean fish, boolean beef, boolean nTBeef, String name,
			String hint, String info, float price) {

		this.vegan = vegan;
		this.veggie = veggie;
		this.bio = bio;
		this.pork = pork;
		this.fish = fish;
		this.beef = beef;
		this.nonTorturedBeef = nTBeef;
		this.name = name;
		this.hint = hint;
		this.info = info;
		this.price = price;

	}

	/**
	 * @return veggie
	 */
	public boolean isVeggie() {
		return veggie;
	}

	/**
	 * @return vegan
	 */
	public boolean isVegan() {
		return vegan;
	}

	/**
	 * @return bio
	 */
	public boolean isBio() {
		return bio;
	}

	/**
	 * @return pork
	 */
	public boolean isPork() {
		return pork;
	}

	/**
	 * @return fish
	 */
	public boolean isFish() {
		return fish;
	}

	/**
	 * @return beef
	 */
	public boolean isBeef() {
		return beef;
	}

	/**
	 * @return nonTorturedBeef
	 */
	public boolean isNonTorturedBeef() {
		return nonTorturedBeef;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return hint
	 */
	public String getHint() {
		return hint;
	}

	/**
	 * @return info
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * @return price
	 */
	public float getPrice() {
		return price;
	}
}

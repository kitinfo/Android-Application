package de.kitinfo.app.mensa;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.os.DropBoxManager.Entry;

public class MensaMeal implements Serializable {

	private static final long serialVersionUID = 6318652233903651125L;

	HashMap<String, Boolean> tags;
	
	public enum Tags {
		VEGGIE,
		VEGAN,
		BIO,
		PORK,
		FISH,
		BEEF,
		NONTORTUREDBEEF;
	}

	private String name;
	private String hint;
	private String info;

	private float price;

	private List<String> adds;

	public MensaMeal(boolean veggie, boolean vegan, boolean bio, boolean pork,
			boolean fish, boolean beef, boolean nTBeef, String name,
			String hint, String info, float price, List<String> adds) {

		tags.put(Tags.VEGGIE.name(), veggie);
		tags.put(Tags.VEGAN.name(), vegan);
		tags.put(Tags.BIO.name(), bio);
		tags.put(Tags.PORK.name(), pork);
		tags.put(Tags.FISH.name(), fish);
		tags.put(Tags.BEEF.name(), beef);
		tags.put(Tags.NONTORTUREDBEEF.name(), nTBeef);
		this.name = name;
		this.hint = hint;
		this.info = info;
		this.price = price;
		this.adds = adds;
	}
	
	public MensaMeal(HashMap<String, Boolean> tags, String name, String hint, String info, float price, List<String> adds) {
		this.tags = tags;
		this.name = name;
		this.hint = hint;
		this.info = info;
		this.price = price;
		this.adds = adds;
	}
	
	/**
	 * return the tag map as string with split symbol between key and tag
	 * @param split split symbol
	 * @return a string
	 */
	public String getTags(String split) {
		
		StringBuilder builder = new StringBuilder();
		
		for (String s : tags.keySet()) {
			builder.append(s);
			builder.append(split);
			builder.append(tags.get(s));
			builder.append("\n");
		}
		builder.delete(builder.lastIndexOf(split, 0), builder.length() -1);
		
		return builder.toString();
	}
	
	public HashMap<String, Boolean> getTags() {
		return tags;
	}

	/**
	 * @return veggie
	 */
	public boolean isVeggie() {
		
		if (tags.containsKey(Tags.VEGGIE.name())) {
			return tags.get(Tags.VEGGIE.name());
		}
		
		return false;
	}

	/**
	 * @return vegan
	 */
	public boolean isVegan() {
		if (tags.containsKey(Tags.VEGAN.name())) {
			return tags.get(Tags.VEGAN.name());
		}
		
		return false;
	}

	/**
	 * @return bio
	 */
	public boolean isBio() {
		if (tags.containsKey(Tags.BIO.name())) {
			return tags.get(Tags.BIO.name());
		}
		
		return false;
	}

	/**
	 * @return pork
	 */
	public boolean isPork() {
		if (tags.containsKey(Tags.PORK.name())) {
			return tags.get(Tags.PORK.name());
		}
		
		return false;
	}

	/**
	 * @return fish
	 */
	public boolean isFish() {
		if (tags.containsKey(Tags.FISH.name())) {
			return tags.get(Tags.FISH.name());
		}
		
		return false;
	}

	/**
	 * @return beef
	 */
	public boolean isBeef() {
		if (tags.containsKey(Tags.BEEF.name())) {
			return tags.get(Tags.BEEF.name());
		}
		
		return false;
	}

	/**
	 * @return nonTorturedBeef
	 */
	public boolean isNonTorturedBeef() {
		if (tags.containsKey(Tags.NONTORTUREDBEEF.name())) {
			return tags.get(Tags.NONTORTUREDBEEF.name());
		}
		
		return false;
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

	/**
	 * @return adds of this meal
	 * @return
	 */
	public List<String> getAdds() {
		return adds;
	}

	@Override
	public String toString() {
		String addsString = "";
		for (String add : adds) {
			addsString += add + ",";
		}
		addsString = "(" + addsString.substring(0, addsString.length() - 1)
				+ ")";
		return addsString + "  " + name + " - " + hint + " for: " + price + "€";
	}
}

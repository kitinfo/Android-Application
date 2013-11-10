package de.kitinfo.app.mensa;

import java.util.LinkedList;
import java.util.List;

public class MensaLine implements Comparable<MensaLine> {

	private String name;
	private List<MensaMeal> meals;
	private int id;
	private int mensaID;

	public MensaLine(String name, int mensaID) {
		meals = new LinkedList<MensaMeal>();
		this.name = name;
		this.mensaID = mensaID;
	}
	
	public MensaLine(String name, int id, int mensaID) {
		this.id = id;
		this.name = name;
		this.mensaID = mensaID;
	}

	public void addMeal(MensaMeal meal) {
		this.meals.add(meal);
	}

	public List<MensaMeal> getMeals() {
		return meals;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(MensaLine other) {
		return this.name.toLowerCase().compareTo(other.getName().toLowerCase());
	}

	public int getID() {
		return id;
	}

	/**
	 * Returns the id of the mensa
	 * @return id of the mensa
	 */
	public int getMensaID() {
		return mensaID;
	}

}

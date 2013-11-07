package de.kitinfo.app.mensa;

import java.util.LinkedList;
import java.util.List;

public class MensaLine implements Comparable<MensaLine> {

	private String name;
	private List<MensaMeal> meals;

	public MensaLine(String name) {
		meals = new LinkedList<MensaMeal>();
		this.name = name;
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

}

package de.kitinfo.app.mensa;

import java.util.LinkedList;
import java.util.List;

public class MensaLine {

	private String name;
	private List<MensaMeal> meals;

	public MensaLine(String name) {
		meals = new LinkedList<MensaMeal>();
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

}
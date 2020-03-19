package de.seibushin.nutrigo.proposer.database;/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-01-27)
 * ***************************************************/

import java.util.ArrayList;
import java.util.List;

public class Meal implements NutritionUnit {
	public int id;
	public String name;
	public int dailyMax;
	public double propose;
	public double portion = 1;
	public double weight;
	public List<Food> foods;
	public boolean portionize = true;


	public Meal(int id, String name) {
		this.id = id;
		this.name = name;
		this.foods = new ArrayList<>();
	}

	public void addFood(Food food) {
		foods.add(food);
	}

	public double getCarbs() {
		return portionize(foods.stream().reduce(0d, (sum, food) -> sum + food.getCarbs(), (d1, d2) -> d1 + d2));
	}

	public double getFat() {
		return portionize(foods.stream().reduce(0d, (sum, food) -> sum + food.getFat(), (d1, d2) -> d1 + d2));
	}

	public double getProtein() {
		return portionize(foods.stream().reduce(0d, (sum, food) -> sum + food.getProtein(), (d1, d2) -> d1 + d2));
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getType() {
		return "MEAL";
	}

	@Override
	public double getServing() {
		if (portionize) {
			return portionize(getWeight());
		}
		return 100;
	}

	public double getKcal() {
		return portionize(foods.stream().reduce(0d, (sum, food) -> sum + food.getKcal(), (d1, d2) -> d1 + d2));
	}

	public double getWeight() {
		return foods.stream().reduce(0d, (sum, food) -> sum + food.portion, (d1, d2) -> d1 + d2);
	}

	@Override
	public double getPropose() {
		return propose;
	}

	@Override
	public int dailyMax() {
		return dailyMax;
	}

	@Override
	public String name() {
		return name;
	}

	public double portionize(double val) {
		if (portionize) {
			return val;
		}
		return val * 100 / getWeight();
	}

	@Override
	public String toString() {
		return "Meal{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", kcal=" + getKcal() +
				", fat=" + getFat() +
				", protein=" + getProtein() +
				", carbs=" + getCarbs() +
				", dailyMax=" + dailyMax() +
				", portion=" + portion +
				", weight=" + getWeight() +
				'}';
	}
}

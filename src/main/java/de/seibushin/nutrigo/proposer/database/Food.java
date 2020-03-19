package de.seibushin.nutrigo.proposer.database;/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-01-27)
 * ***************************************************/

public class Food implements NutritionUnit {
	public int id;
	public double fat;
	public double protein;
	public double carbs;
	public double kcal;
	public String name;
	public int dailyMax;
	public double portion;
	public double weight;
	public double propose;
	public boolean portionize = true;

	public Food(int id, String name, double kcal, double fat, double carbs, double protein, double portion, double weight, int dailyMax, double propose) {
		this.id = id;
		this.name = name;
		this.fat = fat;
		this.kcal = kcal;
		this.protein = protein;
		this.carbs = carbs;
		this.portion = portion;
		this.weight = weight;
		this.dailyMax = dailyMax;
		this.propose = propose;
	}

	public double getCarbs() {
		return portionize(carbs);
	}

	public double getFat() {
		return portionize(fat);
	}

	public double getProtein() {
		return portionize(protein);
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getType() {
		return "FOOD";
	}

	@Override
	public double getServing() {
		if (portionize) {
			return portion;
		}
		return weight;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public double getPropose() {
		return propose;
	}

	public double getKcal() {
		return portionize(kcal);
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
			return val * portion / weight;
		}
		return val;
	}


	@Override
	public String toString() {
		return "Food{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", kcal=" + getKcal() +
				", fat=" + getFat() +
				", protein=" + getProtein() +
				", carbs=" + getCarbs() +
				", dailyMax=" + dailyMax +
				", portion=" + portion +
				", weight=" + weight +
				'}';
	}
}

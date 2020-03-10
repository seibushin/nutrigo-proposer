package de.seibushin.nutrigo.proposer;/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-01-27)
 * ***************************************************/

public class Food {
	public double fat;
	public double protein;
	public double carbs;
	public double kcal;
	public String name;
	public int dailyMax;
	public double portion;
	public double weight;

	public Food(String name, double kcal, double fat, double carbs, double protein, double portion, double weight, int dailyMax) {
		this.name = name;
		this.fat = fat;
		this.kcal = kcal;
		this.protein = protein;
		this.carbs = carbs;
		this.portion = portion;
		this.weight = weight;
		this.dailyMax = dailyMax;
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

	public double getKcal() {
		return portionize(kcal);
	}

	public double portionize(double val) {
		return val * portion / weight;
	}

	@Override
	public String toString() {
		return "Food{" +
				"name='" + name + '\'' +
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

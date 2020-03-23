/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-23)
 * ***************************************************/
package de.seibushin.nutrigo.proposer.database;

public class Profile {
	public int kcal;
	public int fat;
	public int carbs;
	public int sugar;
	public int protein;
	public double weight;
	public int height;
	public int age;
	public int male;

	@Override
	public String toString() {
		return "Profile{" +
				"kcal=" + kcal +
				", fat=" + fat +
				", carbs=" + carbs +
				", sugar=" + sugar +
				", protein=" + protein +
				", weight=" + weight +
				", height=" + height +
				", age=" + age +
				", male=" + male +
				'}';
	}
}

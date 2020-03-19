/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-12)
 * ***************************************************/
package de.seibushin.nutrigo.proposer.database;

public interface NutritionUnit {
	int dailyMax();

	String name();

	double getKcal();

	double getFat();

	double getCarbs();

	double getProtein();

	int getId();

	String getType();

	double getServing();

	double getWeight();

	double getPropose();
}

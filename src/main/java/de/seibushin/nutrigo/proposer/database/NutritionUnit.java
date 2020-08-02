/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-12)
 * ***************************************************/
package de.seibushin.nutrigo.proposer.database;

public interface NutritionUnit {
	double dailyMax();

	String name();

	double getKcal();

	double getFat();

	double getCarbs();

	double getSugar();

	double getProtein();

	int getId();

	String getType();

	double getServing();

	double getPortion();

	double getWeight();

	double getPropose();

	void setPropose(double propose);

}

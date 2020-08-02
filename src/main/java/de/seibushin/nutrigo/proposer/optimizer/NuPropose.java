/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-05-21)
 * ***************************************************/
package de.seibushin.nutrigo.proposer.optimizer;

import de.seibushin.nutrigo.proposer.database.Database;
import de.seibushin.nutrigo.proposer.database.NutritionUnit;

import java.util.ArrayList;
import java.util.List;

public class NuPropose {
	public List<NutritionUnit> nus = new ArrayList<>();
	public List<Propose> foodIDs = new ArrayList<>();
	public List<Propose> mealIDs = new ArrayList<>();

	public void getData() {
		if (foodIDs.size() > 0) {
			nus.addAll(Database.getInstance().searchFood(foodIDs));
		}

		if (mealIDs.size() > 0) {
			nus.addAll(Database.getInstance().searchMeal(mealIDs));
		}
	}
}

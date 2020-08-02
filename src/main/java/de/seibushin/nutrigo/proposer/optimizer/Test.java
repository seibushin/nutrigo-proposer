/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-15)
 * ***************************************************/
package de.seibushin.nutrigo.proposer.optimizer;

import de.seibushin.nutrigo.proposer.database.Database;

import java.util.List;

public class Test {
	public static void main(String[] args) {
		Optimizer opt = new Optimizer();
		opt.nus.addAll(Database.getInstance().getFoods(false));
		opt.nus.addAll(Database.getInstance().getMeals(false));
		List<NuPropose> nus = opt.optimize();
		System.out.println(nus.size());
	}
}

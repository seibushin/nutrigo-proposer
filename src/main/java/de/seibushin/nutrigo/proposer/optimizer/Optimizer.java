package de.seibushin.nutrigo.proposer.optimizer;/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-09)
 * ***************************************************/

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import de.seibushin.nutrigo.proposer.database.Database;
import de.seibushin.nutrigo.proposer.database.NutritionUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Optimizer {
//	static {
//		System.loadLibrary("jniortools");
//	}

	public int fatmin = 60;
	public int fatmax = 100;
	public int carbsmin = 200;
	public int carbsmax = 350;
	public int proteinmin = 80;
	public int proteinmax = 150;
	public int kcalmin = 2200;
	public int kcalmax = 2400;
	public int mealCount = 0;

	public List<NutritionUnit> nus = new ArrayList<>();

	public void setContraints(int kcal, int fat, int carbs, int protein) {
		this.kcalmin = (int) (kcal * 0.9);
		this.kcalmax = (int) (kcal * 1.1);
		this.fatmin = (int) (fat * 0.9);
		this.fatmax = (int) (fat * 1.1);
		this.carbsmin = (int) (carbs * 0.9);
		this.carbsmax = (int) (carbs * 1.1);
		this.proteinmin = (int) (protein * 0.9);
		this.proteinmax = (int) (protein * 1.1);
	}

	public List<NutritionUnit> optimize() {
		System.out.println("KCAL: " + kcalmin + " - " + kcalmax);
		System.out.println("FAT: " + fatmin + " - " + fatmax);
		System.out.println("CARBS: " + carbsmin + " - " + carbsmax);
		System.out.println("PROTEIN: " + proteinmin + " - " + proteinmax);

		CpModel model = new CpModel();
		model.newBoolVar("test");
		IntVar fat = model.newIntVar(0, 10000, "fat");
		IntVar carbs = model.newIntVar(0, 10000, "carbs");
		IntVar protein = model.newIntVar(0, 10000, "protein");
		IntVar kcal = model.newIntVar(0, 100000, "kcal");
		IntVar meals = model.newIntVar(0, 100, "meals");

		IntVar[] used = new IntVar[nus.size()];
		for (int i = 0; i < nus.size(); i++) {
			NutritionUnit nu = nus.get(i);
			used[i] = model.newIntVar(0, nu.dailyMax(), nu.getType() + nu.getId());
		}

		model.addEquality(fat, LinearExpr.scalProd(used, nus.stream().mapToInt(f -> (int) (f.getFat() * 10)).toArray()));
		model.addEquality(carbs, LinearExpr.scalProd(used, nus.stream().mapToInt(f -> (int) (f.getCarbs() * 10)).toArray()));
		model.addEquality(protein, LinearExpr.scalProd(used, nus.stream().mapToInt(f -> (int) (f.getProtein() * 10)).toArray()));
		model.addEquality(kcal, LinearExpr.scalProd(used, nus.stream().mapToInt(f -> (int) (f.getKcal() * 10)).toArray()));
		model.addEquality(meals, LinearExpr.scalProd(used, nus.stream().mapToInt(f -> f.getType().equals("MEAL") ? 1 : 0).toArray()));

		model.addGreaterOrEqual(meals, mealCount);
		model.addGreaterOrEqual(fat, fatmin * 10);
		model.addLessOrEqual(fat, fatmax * 10);
		model.addGreaterOrEqual(carbs, carbsmin * 10);
		model.addLessOrEqual(carbs, carbsmax * 10);
		model.addGreaterOrEqual(protein, proteinmin * 10);
		model.addLessOrEqual(protein, proteinmax * 10);
		model.addGreaterOrEqual(kcal, kcalmin * 10);
		model.addLessOrEqual(kcal, kcalmax * 10);

		CpSolver solver = new CpSolver();
		solver.getParameters().setMaxTimeInSeconds(30);
		Callback cb = new Callback(used, fat, carbs, protein, kcal);
		System.out.println("HDFSDF");
		solver.searchAllSolutions(model, cb);

		List<NutritionUnit> nus = new ArrayList<>();

		String ids = "(" + cb.getFoodID().stream().map(integer -> "" + integer).collect(Collectors.joining(", ")) + ")";
		nus.addAll(Database.getInstance().searchFood(ids));

		return nus;
	}

	static class Callback extends CpSolverSolutionCallback {
		private int solutionCount;
		private final List<IntVar> variableArray = new ArrayList<>();
		private final List<Integer> foodIDs = new ArrayList<>();
		private final List<Integer> mealIDs = new ArrayList<>();

		public Callback(IntVar[] foods, IntVar... variables) {
			variableArray.addAll(Arrays.asList(foods));
			variableArray.addAll(Arrays.asList(variables));
		}

		public List<Integer> getFoodID() {
			return foodIDs;
		}

		public List<Integer> getMealIDs() {
			return mealIDs;
		}

		@Override
		public void onSolutionCallback() {
			System.out.printf("Solution #%d: time = %.02f s%n", solutionCount, wallTime());
			if (solutionCount > 10) {
				stopSearch();
			}
			for (IntVar v : variableArray) {

				switch (v.getName()) {
					case "fat":
					case "carbs":
					case "protein":
					case "kcal":
						System.out.printf("  %s = %.01f%n", v.getName(), value(v) / 10f);
						break;
					default:
						if (value(v) > 0) {
							System.out.printf("  %s = %d%n", v.getName(), value(v));
						}
						break;
				}

				if (v.getName().startsWith("FOOD") && value(v) > 0) {
					int id = Integer.valueOf(v.getName().replace("FOOD", ""));
					foodIDs.add(id);
				}


			}
			solutionCount++;
		}

		public int getSolutionCount() {
			return solutionCount;
		}
	}
}

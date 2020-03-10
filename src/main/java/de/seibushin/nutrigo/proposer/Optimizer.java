package de.seibushin.nutrigo.proposer;/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-09)
 * ***************************************************/

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Optimizer {
	static {
		System.loadLibrary("jniortools");
	}

	public int fatmin = 60;
	public int fatmax = 100;
	public int carbsmin = 200;
	public int carbsmax = 350;
	public int proteinmin = 80;
	public int proteinmax = 150;
	public int kcalmin = 2200;
	public int kcalmax = 2400;

	public List<Food> foods = new ArrayList<>();

	public void optimize() {
		System.out.println(kcalmin + " - " + kcalmax);

		CpModel model = new CpModel();
		IntVar fat = model.newIntVar(0, 10000, "fat");
		IntVar carbs = model.newIntVar(0, 10000, "carbs");
		IntVar protein = model.newIntVar(0, 10000, "protein");
		IntVar kcal = model.newIntVar(0, 100000, "kcal");

		IntVar[] used = new IntVar[foods.size()];
		for (int i = 0; i < foods.size(); i++) {
			Food food = foods.get(i);
			used[i] = model.newIntVar(0, food.dailyMax, food.name);
		}

		model.addEquality(fat, LinearExpr.scalProd(used, foods.stream().mapToInt(f -> (int) (f.getFat() * 10)).toArray()));
		model.addEquality(carbs, LinearExpr.scalProd(used, foods.stream().mapToInt(f -> (int) (f.getCarbs() * 10)).toArray()));
		model.addEquality(protein, LinearExpr.scalProd(used, foods.stream().mapToInt(f -> (int) (f.getProtein() * 10)).toArray()));
		model.addEquality(kcal, LinearExpr.scalProd(used, foods.stream().mapToInt(f -> (int) (f.getKcal() * 10)).toArray()));

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
		solver.searchAllSolutions(model, cb);
	}

	static class Callback extends CpSolverSolutionCallback {
		private int solutionCount;
		private final List<IntVar> variableArray = new ArrayList<>();

		public Callback(IntVar[] foods, IntVar... variables) {
			variableArray.addAll(Arrays.asList(foods));
			variableArray.addAll(Arrays.asList(variables));
		}

		@Override
		public void onSolutionCallback() {
			System.out.printf("Solution #%d: time = %.02f s%n", solutionCount, wallTime());
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
			}
			solutionCount++;
		}

		public int getSolutionCount() {
			return solutionCount;
		}
	}
}

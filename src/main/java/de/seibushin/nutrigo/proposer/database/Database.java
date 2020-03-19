package de.seibushin.nutrigo.proposer.database;/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-09)
 * ***************************************************/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {
	private static Database INSTANCE;

	public Database() {
		connect();
	}

	public static Database getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Database();
		}

		return INSTANCE;
	}

	private Connection connect() {
		// SQLite connection string
		String url = "jdbc:sqlite:ba_nutrigo";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
			System.out.println(conn);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println(e);
		}
		return conn;
	}


	/**
	 * select all rows in the warehouses table
	 */
	public List<NutritionUnit> getMeals(Boolean portionize) {
		List<NutritionUnit> meals = new ArrayList<>();
		String sql_meals = "SELECT * FROM MEALINFO";

		try (Connection con = this.connect()) {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql_meals);

			while (rs.next()) {
				int meal_id = rs.getInt("id");
				String meal_name = rs.getString("name");
				Meal meal = new Meal(meal_id, meal_name);

				String sql_foods = "SELECT * FROM MEALXFOOD as mf" +
						" INNER JOIN FOOD as f ON mf.mealId = " + meal_id +
						" and f.id = mf.foodId";
				Statement foods = con.createStatement();
				ResultSet rs2 = foods.executeQuery(sql_foods);
				while (rs2.next()) {
					int food_id = rs2.getInt("foodId");
					String food_name = rs2.getString("name");
					int food_serving = rs2.getInt("serving");
					double food_kcal = rs2.getDouble("kcal");
					double food_fat = rs2.getDouble("fat");
					double food_carbs = rs2.getDouble("carbs");
					double food_protein = rs2.getDouble("protein");
					double food_weight = rs2.getDouble("weight");
					double food_propose = rs2.getDouble("propose");

					Food food = new Food(food_id, food_name, food_kcal, food_fat, food_carbs, food_protein, food_serving, food_weight, 1, food_propose);
					meal.addFood(food);
				}

				Statement daily = con.createStatement();
				ResultSet rs3 = daily.executeQuery("SELECT *, sum(serving) as 'total' FROM DayMeal where mid = " + meal_id + " GROUP BY date ORDER BY total DESC LIMIT 1");
				try {
					rs3.next();
					double total = rs3.getDouble("total");
					meal.dailyMax = (int) Math.ceil(total / 1.0d);
				} catch (SQLException e1) {
//					System.out.println("No data available - dailyMax=1");
				}
				meal.portionize = portionize;

				meals.add(meal);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return meals;
	}

	/**
	 * select all rows in the warehouses table
	 */
	public List<NutritionUnit> getFoods(Boolean portionize) {
		List<NutritionUnit> foods = new ArrayList<>();
		String sql = "SELECT * FROM FOOD";

		try (Connection conn = this.connect()) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// loop through the result set
			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				double kcal = rs.getDouble("kcal");
				double fat = rs.getDouble("fat");
				double carbs = rs.getDouble("carbs");
				double protein = rs.getDouble("protein");
				double weight = rs.getDouble("weight");
				double portion = rs.getDouble("portion");
				double propose = rs.getDouble("propose");
				int dailyMax = 1;
				Food food = new Food(id, name, kcal, fat, carbs, protein, portion, weight, dailyMax, propose);
				food.portionize = portionize;

				Statement stmt2 = conn.createStatement();
				ResultSet rs2 = stmt2.executeQuery("SELECT *, sum(serving) as 'total' FROM DayFood where fid = " + id + " GROUP BY date ORDER BY total DESC LIMIT 1");
				try {
					rs2.next();
					double total = rs2.getDouble("total");
					food.dailyMax = (int) Math.ceil(total / portion);
				} catch (SQLException e1) {
//					System.out.println("No data available - dailyMax=1");
				}

				foods.add(food);
//				System.out.println(food);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return foods;
	}

	public List<NutritionUnit> searchFood(String fid) {
		List<NutritionUnit> foods = new ArrayList<>();
		String sql = "SELECT * FROM FOOD WHERE id IN " + fid;

		try (Connection conn = this.connect()) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// loop through the result set
			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				double kcal = rs.getDouble("kcal");
				double fat = rs.getDouble("fat");
				double carbs = rs.getDouble("carbs");
				double protein = rs.getDouble("protein");
				double weight = rs.getDouble("weight");
				double portion = rs.getDouble("portion");
				double propose = rs.getDouble("propose");
				int dailyMax = 1;
				Food food = new Food(id, name, kcal, fat, carbs, protein, portion, weight, dailyMax, propose);

				Statement stmt2 = conn.createStatement();
				ResultSet rs2 = stmt2.executeQuery("SELECT *, sum(serving) as 'total' FROM DayFood where fid = " + id + " GROUP BY date ORDER BY total DESC LIMIT 1");
				try {
					rs2.next();
					double total = rs2.getDouble("total");
					food.dailyMax = (int) Math.ceil(total / portion);
				} catch (SQLException e1) {
				}
				foods.add(food);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return foods;
	}
}

package de.seibushin.nutrigo.proposer.database;/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-09)
 * ***************************************************/

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class Database {
	private static Database INSTANCE;

	public Database() {
		connect();
	}

	/**
	 * Singleton getter
	 *
	 * @return Database instance
	 */
	public static Database getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Database();
		}

		return INSTANCE;
	}

	/**
	 * Connect to the database
	 *
	 * @return connection
	 */
	private Connection connect() {
		// SQLite connection string
		String url = "jdbc:sqlite:database/nutrigo";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * If a new database gets uploaded we want to process it
	 */
	public void process() {
		try (Connection con = this.connect();
			 Statement statement = con.createStatement()) {
			// add propose column
			String alter = "ALTER TABLE Food ADD propose REAL DEFAULT 1;";
			statement.executeUpdate(alter);
			// set inital values equal to the dailymax
			String update = "UPDATE Food SET propose = IFNULL((SELECT ROUND(SUM(serving) / portion, 1) as dailymax" +
					" FROM DayFood WHERE fid = id GROUP BY date ORDER BY dailymax DESC LIMIT 1), 1);";
			statement.executeUpdate(update);

			// also add the same column for the meal table
			// add propose column
			alter = "ALTER TABLE MealInfo ADD propose REAL DEFAULT 1;";
			statement.executeUpdate(alter);
			// set inital values equal to dailymax
			update = "UPDATE MealInfo SET propose = IFNULL((SELECT ROUND(SUM(serving), 1) as dailymax FROM DayMeal WHERE mid = id GROUP BY date ORDER BY dailymax DESC LIMIT 1), 1);";
			statement.executeUpdate(update);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * select all rows in the warehouses table
	 */
	public List<NutritionUnit> getFoods(Boolean portionize) {
		List<NutritionUnit> foods = new ArrayList<>();
		String sql = "SELECT * FROM FOOD";

		foods.addAll(createFood(sql, portionize));

		return foods;
	}

	/**
	 * Get all foods with food id in the given String List
	 *
	 * @param fid
	 * @return food list
	 */
	public List<NutritionUnit> searchFood(String fid) {
		List<NutritionUnit> foods = new ArrayList<>();
		String sql = "SELECT * FROM FOOD WHERE id IN " + fid;

		foods.addAll(createFood(sql, true));

		return foods;
	}

	/**
	 * Give a query which returns a food result and retrieve the list of foods
	 *
	 * @param sql query selecting foods
	 * @return List of foods
	 */
	private List<Food> createFood(String sql, boolean portionize) {
		List<Food> foods = new ArrayList<>();

		// try with resource on connection statement and resultSet
		// this ensures all resources are closed afterwards
		try (Connection conn = this.connect();
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery(sql)) {
			// iterate over resultset
			while (rs.next()) {
				// extract data needed fro food
				int id = rs.getInt("id");
				String name = rs.getString("name");
				double kcal = rs.getDouble("kcal");
				double fat = rs.getDouble("fat");
				double carbs = rs.getDouble("carbs");
				double sugar = rs.getDouble("sugar");
				double protein = rs.getDouble("protein");
				double weight = rs.getDouble("weight");
				double portion = rs.getDouble("portion");
				double propose = rs.getDouble("propose");
				int dailyMax = 1;
				Food food = new Food(id, name, kcal, fat, carbs, sugar, protein, portion, weight, dailyMax, propose);
				food.portionize = portionize;

				// get the dailymax
				String sql2 = "SELECT IFNULL((SELECT ROUND(sum(serving),2) as total FROM DayFood where fid = " + id + " GROUP BY date ORDER BY total DESC LIMIT 1), 0) as total";
				try (Statement stmt2 = conn.createStatement();
					 ResultSet rs2 = stmt2.executeQuery(sql2)) {
					if (rs2.next()) {
						double total = rs2.getDouble("total");
						food.dailyMax = total;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				foods.add(food);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return foods;
	}

	/**
	 * Get the meal with the given ID
	 *
	 * @param id
	 * @return
	 */
	public Meal getMeal(int id) {
		Meal meal = null;
		String sql_meals = "SELECT * FROM MEALINFO WHERE id = " + id;

		try (Connection con = this.connect();
			 Statement stmt = con.createStatement();
			 ResultSet rs = stmt.executeQuery(sql_meals)) {

			if (rs.next()) {
				int meal_id = rs.getInt("id");
				String meal_name = rs.getString("name");
				meal = new Meal(meal_id, meal_name);
				meal.propose = rs.getDouble("propose");
				meal.portionize = true;
				dailyMaxMeal(meal);

				// add the foods to the meal
				String foods_query = "SELECT * FROM MEALXFOOD as mf" +
						" INNER JOIN FOOD as f ON mf.mealId = " + meal_id +
						" and f.id = mf.foodId";
				meal.addAllFood(createFood(foods_query, true));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return meal;
	}

	/**
	 * Get all meals with meal id in the given String List
	 *
	 * @param mid
	 * @return food list
	 */
	public List<NutritionUnit> searchMeal(List<Integer> mid) {
		List<NutritionUnit> nus = new ArrayList<>();

		for (Integer id : mid) {
			nus.add(getMeal(id));
		}

		return nus;
	}

	/**
	 * select all rows in the warehouses table
	 */
	public List<NutritionUnit> getMeals(Boolean portionize) {
		List<NutritionUnit> meals = new ArrayList<>();
		String sql_meals = "SELECT * FROM MEALINFO";

		try (Connection con = this.connect();
			 Statement stmt = con.createStatement();
			 ResultSet rs = stmt.executeQuery(sql_meals)) {
			while (rs.next()) {
				// create the meal from the resultset
				int meal_id = rs.getInt("id");
				String meal_name = rs.getString("name");
				Meal meal = new Meal(meal_id, meal_name);
				meal.propose = rs.getDouble("propose");
				meal.portionize = portionize;
				dailyMaxMeal(meal);

				// add all foods to the meal
				String sql_foods = "SELECT * FROM MEALXFOOD as mf" +
						" INNER JOIN FOOD as f ON mf.mealId = " + meal_id +
						" and f.id = mf.foodId";
				meal.addAllFood(createFood(sql_foods, true));

				meals.add(meal);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return meals;
	}

	/**
	 * Get the dailyMax for the given meal
	 *
	 * @param meal Meal
	 */
	private void dailyMaxMeal(Meal meal) {
		String sql = "SELECT *, ROUND(sum(serving),2) as 'total' FROM DayMeal where mid = " + meal.id + " GROUP BY date ORDER BY total DESC LIMIT 1";

		try (Connection conn = this.connect();
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery(sql)) {
			if (rs.next()) {
				double total = rs.getDouble("total");
				meal.dailyMax = total;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the profile from the database
	 *
	 * @return
	 */
	public Profile getProfile() {
		Profile profile = new Profile();
		String sql = "SELECT * FROM Profile LIMIT 1";
		try (Connection conn = this.connect();
			 Statement stm = conn.createStatement();
			 ResultSet rs = stm.executeQuery(sql)) {
			if (rs.next()) {
				profile.kcal = rs.getInt("kcal");
				profile.fat = rs.getInt("fat");
				profile.carbs = rs.getInt("carbs");
				profile.sugar = rs.getInt("sugar");
				profile.protein = rs.getInt("protein");
				profile.weight = rs.getDouble("weight");
				profile.height = rs.getInt("height");
				profile.age = rs.getInt("age");
				profile.male = rs.getInt("male");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return profile;
	}
}

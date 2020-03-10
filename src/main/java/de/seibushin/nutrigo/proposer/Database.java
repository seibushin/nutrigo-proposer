package de.seibushin.nutrigo.proposer;/* ***************************************************
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
		String url = "jdbc:sqlite::resource:ba_nutrigo";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	/**
	 * select all rows in the warehouses table
	 */
	public List<Food> getFoods() {
		List<Food> foods = new ArrayList<>();
		String sql = "SELECT * FROM FOOD";

		try (Connection conn = this.connect();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {

			// loop through the result set
			while (rs.next()) {
				String name = rs.getString("name");
				double kcal = rs.getDouble("kcal");
				double fat = rs.getDouble("fat");
				double carbs = rs.getDouble("carbs");
				double protein = rs.getDouble("protein");
				double weight = rs.getDouble("weight");
				double portion = rs.getDouble("portion");
				int id = rs.getInt("id");
				int dailyMax = 1;
				Food food = new Food(name, kcal, fat, carbs, protein, portion, weight, dailyMax);

				Statement stmt2 = conn.createStatement();
				ResultSet rs2 = stmt2.executeQuery("SELECT *, sum(serving) as 'total' FROM DayFood where fid = " + id + " GROUP BY date ORDER BY total DESC LIMIT 1");
				try {
					rs2.next();
					double total = rs2.getInt("total");
					food.dailyMax = (int) Math.ceil(total / portion);
				} catch (SQLException e1) {
					System.out.println("No data available - dailyMax=1");
				}

				foods.add(food);
				System.out.println(food);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return foods;
	}
}

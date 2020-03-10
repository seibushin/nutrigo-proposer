package de.seibushin.nutrigo.proposer;/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-09)
 * ***************************************************/

public class App {
	public App() {

	}

	public static void main(String args[]) {
		App app = new App();

		app.run();
	}

	private void run() {
		Optimizer opt = new Optimizer();
		opt.foods.addAll(Database.getInstance().getFoods());

		opt.optimize();
	}
}

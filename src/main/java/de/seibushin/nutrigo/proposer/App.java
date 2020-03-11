package de.seibushin.nutrigo.proposer;/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-09)
 * ***************************************************/

import java.util.InputMismatchException;
import java.util.Scanner;

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

		Scanner scanner = new Scanner(System.in);
		String again = "";
		while (!again.equals("n")) {
			try {
				System.out.print("Kcal:");
				int kcal = scanner.nextInt();
				System.out.print("Fat:");
				int fat = scanner.nextInt();
				System.out.print("Carbs:");
				int carbs = scanner.nextInt();
				System.out.print("Protein:");
				int protein = scanner.nextInt();

				opt.setContraints(kcal, fat, carbs, protein);
				opt.optimize();
			} catch (InputMismatchException e) {
				System.out.println("Input Error try again.");
				scanner.next();
				scanner.reset();
			}

			System.out.print("Do you wanna do a new search?(y/n)");
			again = scanner.next();
		}
	}
}

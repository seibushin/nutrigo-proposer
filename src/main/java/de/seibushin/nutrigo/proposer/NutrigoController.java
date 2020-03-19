/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-14)
 * ***************************************************/
package de.seibushin.nutrigo.proposer;

import de.seibushin.nutrigo.proposer.database.Database;
import de.seibushin.nutrigo.proposer.database.NutritionUnit;
import de.seibushin.nutrigo.proposer.optimizer.Optimizer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class NutrigoController {
	@GetMapping("/")
	public String root() {
		return "redirect:/plan";
	}

	@GetMapping("/plan")
	public String plan(Model model) throws Exception {
		return "plan";
	}

	@GetMapping("/foods")
	public String foods(@RequestParam(name = "portionize", required = false, defaultValue = "true") String portionize, Model model) throws Exception {
		Boolean portion = Boolean.parseBoolean(portionize);
		model.addAttribute("foods", Database.getInstance().getFoods(portion));
		return "foods";
	}

	@GetMapping("/meals")
	public String meals(@RequestParam(name = "portionize", required = false, defaultValue = "true") String portionize, Model model) throws Exception {
		Boolean portion = Boolean.parseBoolean(portionize);
		model.addAttribute("meals", Database.getInstance().getMeals(portion));
		return "meals";
	}

	// mapping for GET and POST
	@RequestMapping(value = "/propose", method = {RequestMethod.GET, RequestMethod.POST})
	public String propose(@RequestParam(name = "kcal", required = false) Integer kcal,
						  @RequestParam(name = "fat", required = false) Integer fat,
						  @RequestParam(name = "carbs", required = false) Integer carbs,
						  @RequestParam(name = "protein", required = false) Integer protein,
						  @RequestParam(name = "meals", required = false) Integer meals,
						  Model model) {
		Optimizer opt = new Optimizer();
		if (meals != null) {
			opt.mealCount = meals;
		}
		if (kcal != null && fat != null && carbs != null && protein != null) {
			opt.setContraints(kcal, fat, carbs, protein);
			opt.nus.addAll(Database.getInstance().getFoods(false));
			opt.nus.addAll(Database.getInstance().getMeals(false));
			List<NutritionUnit> nus = opt.optimize();
			model.addAttribute("nus", nus);
		}

		// add attributes to show searched values
		model.addAttribute("kcal", kcal);
		model.addAttribute("fat", fat);
		model.addAttribute("carbs", carbs);
		model.addAttribute("protein", protein);
		model.addAttribute("meals", meals);

		return "propose";
	}
}
/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-23)
 * ***************************************************/
package de.seibushin.nutrigo.proposer;

import de.seibushin.nutrigo.proposer.database.Database;
import de.seibushin.nutrigo.proposer.database.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NutrigoRestController {
	@GetMapping("/profile")
	public Profile profile() {
		return Database.getInstance().getProfile();
	}
}

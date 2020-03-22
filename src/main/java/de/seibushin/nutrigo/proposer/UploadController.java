/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-22)
 * ***************************************************/
package de.seibushin.nutrigo.proposer;

import de.seibushin.nutrigo.proposer.upload.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UploadController {
	private final StorageService storageService;

	@Autowired
	public UploadController(StorageService storageService) {
		this.storageService = storageService;
	}

	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file")MultipartFile file, RedirectAttributes redirectAttributes) {
		storageService.store(file);
		redirectAttributes.addFlashAttribute("message", "Uploaded new database " + file.getOriginalFilename());
		return "redirect:/plan";
	}
}

/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-22)
 * ***************************************************/
package de.seibushin.nutrigo.proposer.upload;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	void store(MultipartFile file);
}

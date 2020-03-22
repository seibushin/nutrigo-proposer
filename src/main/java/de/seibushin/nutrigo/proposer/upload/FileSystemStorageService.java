package de.seibushin.nutrigo.proposer.upload;

import de.seibushin.nutrigo.proposer.database.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {
	private final Path rootLocation;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	public void store(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				// This is a security check
				throw new StorageException("Cannot store file with relative path outside current directory " + filename);
			}
			try (InputStream inputStream = file.getInputStream()) {
				// create dir if not already existing
				File dir = this.rootLocation.toFile();
				if (!dir.exists()) {
					dir.mkdirs();
				}

				Files.copy(inputStream, this.rootLocation.resolve("nutrigo"), StandardCopyOption.REPLACE_EXISTING);

				// process database
				Database.getInstance().process();
			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}
	}
}
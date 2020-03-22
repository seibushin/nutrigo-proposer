package de.seibushin.nutrigo.proposer;

import de.seibushin.nutrigo.proposer.upload.StorageProperties;
import de.seibushin.nutrigo.proposer.upload.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class WebProposerApplication {
	public static void main(String[] args) {
		SpringApplicationBuilder app = new SpringApplicationBuilder(WebProposerApplication.class).web(WebApplicationType.SERVLET);
		app.build();
		app.run();
	}
}

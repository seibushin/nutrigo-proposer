package de.seibushin.nutrigo.proposer;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class WebProposerApplication {
	public static void main(String[] args) {
		SpringApplicationBuilder app = new SpringApplicationBuilder(WebProposerApplication.class).web(WebApplicationType.SERVLET);
		app.build();
		app.run();
	}
}

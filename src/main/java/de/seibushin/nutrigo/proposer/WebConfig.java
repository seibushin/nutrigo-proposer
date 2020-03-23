/* ***************************************************
 * Created by Sebastian Meyer (s.meyer@seibushin.de)
 * (2020-03-23)
 * ***************************************************/
package de.seibushin.nutrigo.proposer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class WebConfig {
	@Bean
	public ObjectMapper configureJson() {
		return new Jackson2ObjectMapperBuilder()
				.indentOutput(true)
				.propertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE)
				.build();
	}
}

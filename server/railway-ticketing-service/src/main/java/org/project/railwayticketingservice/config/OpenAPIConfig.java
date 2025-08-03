package org.project.railwayticketingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Railway Ticketing System")
                        .description("This documentation explains how to use this API from start to end. " +
                                "It also contains guidelines as regards consumption from the front-end")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Isla")
                                .email("islarcherjr@gmail.com")
                        )
                );
    }
}

package com.ptsecurity.appsec.ai.ee.utils.ci.integration.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    /**
     * @return Make Swagger available at https://ptai.domain.org:8443/swagger-ui.html
     */
    @Bean
    public Docket api() {
        /*
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/sast/**"))
                .paths(PathSelectors.ant("/api/admin/**"))
                // .paths(PathSelectors.ant("/oauth/**"))
                .build();

         */
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ptsecurity.appsec.ai.ee"))
                .build();
    }
}
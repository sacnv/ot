
package com.cs.orderbook.config;


import static com.cs.orderbook.app.ApplicationLiterals.API_DESCRIPTION;
import static com.cs.orderbook.app.ApplicationLiterals.API_LICENSE;
import static com.cs.orderbook.app.ApplicationLiterals.API_TITLE;
import static com.cs.orderbook.app.ApplicationLiterals.API_VERSION;
import static com.cs.orderbook.app.ApplicationLiterals.CONTACT_MAILID;
import static com.cs.orderbook.app.ApplicationLiterals.CONTACT_NAME;
import static com.cs.orderbook.app.ApplicationLiterals.CONTACT_URL;
import static com.cs.orderbook.app.ApplicationLiterals.LICENSE_URL;
import static com.cs.orderbook.app.ApplicationLiterals.TOS;
import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.any())
                .paths(paths())
                .build()
                .apiInfo(apiInfo());
    }

    private Predicate<String> paths() {
        return or(
            regex("/actuator/.*"),
            regex("/v1.*"));
      }

    private ApiInfo apiInfo() {
        return new ApiInfo(
          API_TITLE, API_DESCRIPTION, API_VERSION, TOS,
          new Contact(CONTACT_NAME, CONTACT_URL, CONTACT_MAILID),
          API_LICENSE, LICENSE_URL, Collections.emptyList());
    }
}

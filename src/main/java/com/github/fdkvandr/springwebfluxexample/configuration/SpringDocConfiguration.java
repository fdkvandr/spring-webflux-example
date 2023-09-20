package com.github.fdkvandr.springwebfluxexample.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(name = "Basic Authentication", type = SecuritySchemeType.HTTP, scheme = "basic")
public class SpringDocConfiguration {

}

package com.currencyexchange.configuration;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Currency Exchange API", description = "APIs for currency exchange operations", version = "v1.0"))
public class SwaggerConfig {
}
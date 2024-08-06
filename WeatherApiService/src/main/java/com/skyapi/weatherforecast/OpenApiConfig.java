package com.skyapi.weatherforecast;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        contact = @Contact(
            name = "Weather Forecast API",
            email = "gnaht.asdzxc@gmail.com"
        ),
        title = "Weather Forecast API",
        version = "1.0.0",
        description = "This API provides weather forecast information for a given location."
    )
)
public class OpenApiConfig {

}

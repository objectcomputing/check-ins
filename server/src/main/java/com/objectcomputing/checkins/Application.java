package com.objectcomputing.checkins;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Check-ins Project",
                version = "0.1",
                description = "This web application is for uploading files and tracking skill set of team members.",
                contact = @Contact(url = "https://objectcomputing.com", name = "OCI", email = "info@objectcomputing.com")
        )

)
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}

package com.objectcomputing.checkins;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Check-ins Onboarding Project",
                version = "0.1",
                description = "This web application is for onboarding new team members.",
                contact = @Contact(url = "https://objectcomputing.com", name = "OCI", email = "checkins@objectcomputing.com")
        )

)
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}

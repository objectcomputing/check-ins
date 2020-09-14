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

    public static final String PASSWORD="P@ssw0rd";

    public void iAmAMethod() {
        System.out.println("doing stuff");
    }

    public int[] swapValues(int a, int b) {
        a = a^b; // a = a^b
        b = a^b; // b = a^b^b = a
        a = a^b; // a = a^b^a = b
        return new int[] {a, b};
    }

    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}

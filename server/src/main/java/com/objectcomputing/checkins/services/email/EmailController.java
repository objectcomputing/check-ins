package com.objectcomputing.checkins.services.email;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.List;

@Controller("/services/email")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
public class EmailController {

    private final EmailServices emailServices;

    public EmailController(EmailServices emailServices) {
        this.emailServices = emailServices;
    }

    @Post
    @Status(HttpStatus.CREATED)
    @RequiredPermission(Permission.CAN_SEND_EMAIL)
    public List<Email> sendEmail(String subject, String content, boolean html, String... recipients) {
        return emailServices.sendAndSaveEmail(subject, content, html, recipients);
    }
}

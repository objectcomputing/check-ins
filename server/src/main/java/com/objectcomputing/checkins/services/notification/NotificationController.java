package com.objectcomputing.checkins.services.notification;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
@Controller("/services/notifications")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Send a notification to a user
     *
     * @param notificationDTO {@link NotificationDTO} containing the userId and message
     * @return {@link HttpResponse} with status indicating success or error
     */
    @Post("/send")
    @RequiredPermission(Permission.CAN_SEND_NOTIFICATIONS)
    public HttpResponse<Void> sendNotification(@Body @Valid @NotNull NotificationDTO notificationDTO) {
        notificationService.sendNotification(notificationDTO.getUserId(), notificationDTO.getMessage());
        return HttpResponse.ok();
    }
}
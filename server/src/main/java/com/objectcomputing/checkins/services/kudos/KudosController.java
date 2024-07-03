package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Controller("/services/kudos")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
@Tag(name = "kudos")
public class KudosController {

    private final KudosServices kudosServices;

    public KudosController(KudosServices kudosServices) {
        this.kudosServices = kudosServices;
    }

    @Post
    @Status(HttpStatus.CREATED)
    public Kudos create(@Body @Valid KudosCreateDTO kudos) {
        return kudosServices.save(kudos);
    }

    @Put
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Kudos approve(@Body @Valid Kudos kudos) {
        return kudosServices.approve(kudos);
    }

    @Get("/{id}")
    public KudosResponseDTO getById(@NotNull UUID id) {
        return kudosServices.getById(id);
    }

    @Get("/{?recipientId,?senderId,?isPending,?isPublic}")
    public List<KudosResponseDTO> get(@Nullable UUID recipientId, @Nullable UUID senderId, @Nullable Boolean isPending, @Nullable Boolean isPublic) {
        return kudosServices.findByValues(recipientId, senderId, isPending, isPublic);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public void delete(@NotNull UUID id) {
        kudosServices.delete(id);
    }
}

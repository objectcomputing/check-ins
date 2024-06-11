package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/kudos")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
@Tag(name = "kudos")
public class KudosController {

    private final KudosServices kudosServices;

    private static final Logger LOG = LoggerFactory.getLogger(KudosController.class);

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

    @Get("/{?recipientId,?senderId,?isPending,?Public}")
    public List<KudosResponseDTO> get(@Nullable UUID recipientId, @Nullable UUID senderId, @Nullable Boolean isPending, @Nullable Boolean Public) {
        return kudosServices.findByValues(recipientId, senderId, isPending, Public);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public void delete(@NotNull UUID id) {
        kudosServices.delete(id);
    }

}

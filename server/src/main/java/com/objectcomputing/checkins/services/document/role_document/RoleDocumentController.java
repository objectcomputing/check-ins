package com.objectcomputing.checkins.services.document.role_document;

import com.objectcomputing.checkins.services.document.DocumentResponseDTO;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/documents/role-documents")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "role-documents")
public class RoleDocumentController {

    private final RoleDocumentServices roleDocumentServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public RoleDocumentController(RoleDocumentServices roleDocumentServices,
                                  EventLoopGroup eventLoopGroup,
                                  @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.roleDocumentServices = roleDocumentServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Post
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Mono<HttpResponse<RoleDocument>> create(UUID roleId, UUID documentId) {
        return Mono.fromCallable(() -> roleDocumentServices.saveByIds(roleId, documentId))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(roleDoc -> (HttpResponse<RoleDocument>) HttpResponse.created(roleDoc))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Get("/{roleId}")
    public Mono<HttpResponse<List<DocumentResponseDTO>>> getDocumentsByRole(UUID roleId) {
        return Mono.fromCallable(() -> roleDocumentServices.getDocumentsByRole(roleId))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(docList -> (HttpResponse<List<DocumentResponseDTO>>) HttpResponse.ok(docList))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Put
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Mono<HttpResponse<RoleDocument>> update(@Body @Valid RoleDocument roleDocument) {
        return Mono.fromCallable(() -> roleDocumentServices.update(roleDocument))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(roleDoc -> (HttpResponse<RoleDocument>) HttpResponse.ok(roleDoc))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Delete("/{roleId}/{documentId}")
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public HttpResponse<?> delete(@NotNull UUID roleId, @NotNull UUID documentId) {
        roleDocumentServices.delete(new RoleDocumentId(roleId, documentId));
        return HttpResponse.noContent();
    }

    @Get
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Mono<HttpResponse<List<RoleDocumentResponseDTO>>> getAllDocuments() {
        return Mono.fromCallable(roleDocumentServices::getAllDocuments)
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(documents -> (HttpResponse<List<RoleDocumentResponseDTO>>) HttpResponse.ok(documents))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }
}

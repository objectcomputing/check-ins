package com.objectcomputing.checkins.services.settings;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;
import javax.validation.Valid;

import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/services/settings")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "settings")
public class SettingsController {
    
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;
    private final SettingsServices settingsServices;
    private final CurrentUserServices currentUserServices;

    public SettingsController(EventLoopGroup eventLoopGroup, ExecutorService ioExecutorService, SettingsServices settingsServices, CurrentUserServices currentUserServices) {
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
        this.settingsServices = settingsServices;
        this.currentUserServices = currentUserServices;
    }

    /**
     * Find setting by name.
     *
     * @param name {@link String} name of the setting
     * @return {@link <List<SettingResponseDTO>>} Returned setting
     */
    @Get("/{?name}")
    public Single<HttpResponse<List<SettingsResponseDTO>>> getById(@Nullable String name) {
        return Single.fromCallable(() -> settingsServices.findByName(name))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(settings -> (HttpResponse<List<SettingsResponseDTO>>) HttpResponse.ok(settings))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Create and save a new setting.
     *
     * @param settingDTO, {@link SettingsCreateDTO}
     * @return {@link HttpResponse<SettingsResponseDTO>}
     */
    @Post()
    public Single<HttpResponse<SettingsResponseDTO>> save(@Body @Valid SettingsCreateDTO settingDTO, HttpRequest<SettingsCreateDTO> request) {
        return Single.fromCallable(() -> settingsServices.save(fromDTO(settingDTO)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedSetting -> (HttpResponse<SettingsResponseDTO>) HttpResponse
                        .created(fromEntity(savedSetting))
                        .headers(headers -> headers.location(
                                URI.create(String.format("%s/%s", request.getPath(), savedSetting.getId())))))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }
  
    /**
     * Update the setting.
     *
     * @param settingDTO, {@link SettingsUpdateDTO}
     * @return {@link <SettingsReponseDTO>}
     */
    @Put()
    public Single<HttpResponse<SettingsResponseDTO>> update(@Body @Valid SettingsUpdateDTO settingDTO,
            HttpRequest<SettingsUpdateDTO> request) {
        return Single.fromCallable(() -> settingsServices.update(fromUpdateDTO(settingDTO)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedSetting -> (HttpResponse<SettingsResponseDTO>) HttpResponse.created(fromEntity(savedSetting))
                        .headers(headers -> headers
                                .location(URI.create(String.format("%s/%s", request.getPath(), savedSetting.getId())))))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }
       
    
     /**
     * Delete the setting.
     *
     * @param id, {@link UUID}
     */
    @Delete("/{id}")
    public HttpResponse<?> delete(UUID id) {
        settingsServices.delete(id);
        return HttpResponse.ok();
    }
                    
    private Setting fromDTO(SettingsCreateDTO settingsCreateDTO) {
        return new Setting(settingsCreateDTO.getName(), currentUserServices.getCurrentUser().getId(), settingsCreateDTO.getValue());
    }

    private Setting fromUpdateDTO(SettingsUpdateDTO settingsUpdateDTO) {
        return new Setting(settingsUpdateDTO.getId(), settingsUpdateDTO.getName(), currentUserServices.getCurrentUser().getId(),
                settingsUpdateDTO.getValue());
    }
      
    private SettingsResponseDTO fromEntity(Setting entity) {
        SettingsResponseDTO dto = new SettingsResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setValue(entity.getValue());
        return dto;
    }
}
                    
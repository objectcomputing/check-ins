package com.objectcomputing.checkins.services.settings;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;

import java.net.URI;
import java.util.concurrent.ExecutorService;

import javax.validation.Valid;

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
    private final SettingsServices settingsService;

    public SettingsController(EventLoopGroup eventLoopGroup, ExecutorService ioExecutorService, SettingsServices settingsService) {
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
        this.settingsService = settingsService;
    }
 /**
     * Create and save a new setting.
     *
     * @param setting, {@link SettingsCreateDTO}
     * @return {@link HttpResponse<SettingsResponseDTO>}
     */

    @Post()
    public Single<HttpResponse<SettingsResponseDTO>> save(@Body @Valid SettingsCreateDTO settingDTO, HttpRequest<SettingsCreateDTO> request) {
        return Single.fromCallable(() -> settingsService.save(fromDTO(settingDTO)))
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
     * @param setting, {@link SettingsUpdateDTO}
     * @return {@link HttpResponse<SettingsReponseDTO>}
     */

    @Put()
    public Single<HttpResponse<SettingsResponseDTO>> update(@Body @Valid SettingsUpdateDTO settingDTO, HttpRequest<SettingsUpdateDTO>request) {
            return Single.fromCallable(() -> settingsService.update(fromUpdateDTO(settingDTO)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedSetting -> (HttpResponse<SettingsResponseDTO>) HttpResponse
                        .created(fromEntity(savedSetting))
                        .headers(headers -> headers.location(
                            URI.create(String.format("%s/%s", request.getPath(), savedSetting.getId())))))
                            .subscribeOn(Schedulers.from(ioExecutorService));
        }
                        
    private Setting fromDTO(SettingsCreateDTO settingsCreateDTO) {
        return new Setting(settingsCreateDTO.getName(), settingsCreateDTO.getUserId(), settingsCreateDTO.getValue());
    }

    private Setting fromUpdateDTO(SettingsUpdateDTO settingsUpdateDTO) {
        return new Setting(settingsUpdateDTO.getId(), settingsUpdateDTO.getName(), settingsUpdateDTO.getUserId(),
                settingsUpdateDTO.getValue());
    }
      
    private SettingsResponseDTO fromEntity(Setting entity) {
        SettingsResponseDTO dto = new SettingsResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUserId(entity.getUserId());
        dto.setValue(entity.getValue());
        return dto;
    }
}
                    
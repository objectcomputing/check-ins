package com.objectcomputing.checkins.services.settings;

import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller("/services/settings")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "settings")
public class SettingsController {

    private final SettingsServices settingsServices;
    private final CurrentUserServices currentUserServices;

    public SettingsController(SettingsServices settingsServices, CurrentUserServices currentUserServices) {
        this.settingsServices = settingsServices;
        this.currentUserServices = currentUserServices;
    }

    /**
     * Find setting by its name, or if blank find all settings.
     *
     * @param name {@link String} name of the setting
     * @return {@link <List<SettingResponseDTO>>} Returned setting
     */
    @Get("/{?name}")
    public Mono<HttpResponse<List<SettingsResponseDTO>>> getByValue(@Nullable String name) {
        return Mono.fromCallable(() -> settingsServices.findByName(name))
                .map(HttpResponse::ok);
    }

    /**
     * Create and save a new setting.
     *
     * @param settingDTO, {@link SettingsCreateDTO}
     * @return {@link HttpResponse<SettingsResponseDTO>}
     */
    @Post()
    public Mono<HttpResponse<SettingsResponseDTO>> save(@Body @Valid SettingsCreateDTO settingDTO, HttpRequest<?> request) {
        return Mono.fromCallable(() -> settingsServices.save(fromDTO(settingDTO)))
                .map(savedSetting -> HttpResponse.created(fromEntity(savedSetting))
                        .headers(headers -> headers.location(
                                URI.create(String.format("%s/%s", request.getPath(), savedSetting.getId())))));
    }
  
    /**
     * Update the setting.
     *
     * @param settingDTO, {@link SettingsUpdateDTO}
     * @return {@link <SettingsReponseDTO>}
     */
    @Put()
    public Mono<HttpResponse<SettingsResponseDTO>> update(@Body @Valid SettingsUpdateDTO settingDTO, HttpRequest<?> request) {
        return Mono.fromCallable(() -> settingsServices.update(fromUpdateDTO(settingDTO)))
                .map(savedSetting -> HttpResponse.ok(fromEntity(savedSetting))
                        .headers(headers -> headers
                                .location(URI.create(String.format("%s/%s", request.getPath(), savedSetting.getId())))));
    }
       
    
     /**
     * Delete the setting.
     *
     * @param id, id of {@link Setting} to delete
     */
    @Delete("/{id}")
    public Mono<HttpResponse<?>> delete(UUID id) {
        return Mono.fromCallable(() -> settingsServices.delete(id))
                .thenReturn(HttpResponse.ok());
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
                    
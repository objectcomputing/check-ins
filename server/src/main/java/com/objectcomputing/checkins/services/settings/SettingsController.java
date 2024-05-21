package com.objectcomputing.checkins.services.settings;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.objectcomputing.checkins.services.settings.SettingsController.PATH;

@Controller(PATH)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "settings")
@Validated
public class SettingsController {
    public static final String PATH = "/services/settings";

    private final SettingsServices settingsServices;

    public SettingsController(SettingsServices settingsServices) {
        this.settingsServices = settingsServices;
    }

    /**
     * Find setting by its name, or if blank find all settings.
     *
     * @param name {@link String} name of the setting
     * @return {@link <List<SettingResponseDTO>>} Returned setting
     */
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Get("/{?name}")
    @RequiredPermission(Permission.CAN_VIEW_SETTINGS)
    public List<SettingsResponseDTO> findByName(@Nullable String name) {
        return settingsServices.findByName(name);
    }

    @Get("/options")
    @RequiredPermission(Permission.CAN_VIEW_SETTINGS)
    public List<SettingOption> getOptions() {
        return SettingOption.getOptions();
    }
    /**
     * Create and save a new setting.
     *
     * @param settingDTO, {@link SettingsCreateDTO}
     * @return {@link HttpResponse<SettingsResponseDTO>}
     */
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post
    @RequiredPermission(Permission.CAN_ADMINISTER_SETTINGS)
    public HttpResponse<SettingsResponseDTO> save(@Body @Valid SettingsCreateDTO settingDTO) {
        Setting savedSetting = settingsServices.save(fromDTO(settingDTO));
        URI location = UriBuilder.of(PATH).path(savedSetting.getId().toString()).build();
        return HttpResponse.created(fromEntity(savedSetting), location);
    }
  
    /**
     * Update the setting.
     *
     * @param settingDTO, {@link SettingsUpdateDTO}
     * @return {@link <SettingsReponseDTO>}
     */
    @Put
    @ExecuteOn(TaskExecutors.BLOCKING)
    @RequiredPermission(Permission.CAN_ADMINISTER_SETTINGS)
    public HttpResponse<SettingsResponseDTO> update(@Body @Valid SettingsUpdateDTO settingDTO) {
        Setting savedSetting = settingsServices.update(fromUpdateDTO(settingDTO));
        SettingsResponseDTO settingsResponseDTO = fromEntity(savedSetting);
        URI location = UriBuilder.of(PATH).path(savedSetting.getId().toString()).build();
        return HttpResponse.ok(settingsResponseDTO).headers(headers ->
            headers.location(location)
        );
    }
    
     /**
     * Delete the setting.
     *
     * @param id, id of {@link Setting} to delete
     */
    @Delete("/{id}")
    @ExecuteOn(TaskExecutors.BLOCKING)
    @RequiredPermission(Permission.CAN_ADMINISTER_SETTINGS)
    public HttpStatus delete(UUID id) {
        return settingsServices.delete(id) ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY;
    }
                    
    private Setting fromDTO(SettingsCreateDTO settingsCreateDTO) {
        return new Setting(settingsCreateDTO.getName(), settingsCreateDTO.getValue());
    }

    private Setting fromUpdateDTO(SettingsUpdateDTO settingsUpdateDTO) {
        return new Setting(settingsUpdateDTO.getId(), settingsUpdateDTO.getName(), settingsUpdateDTO.getValue());
    }
      
    private SettingsResponseDTO fromEntity(Setting entity) {
        SettingsResponseDTO dto = new SettingsResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setValue(entity.getValue());
        return dto;
    }
}
                    
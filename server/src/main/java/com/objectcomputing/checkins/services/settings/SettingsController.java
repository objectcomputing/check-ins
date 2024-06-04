package com.objectcomputing.checkins.services.settings;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
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
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller(SettingsController.PATH)
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "settings")
@Validated
public class SettingsController {
    public static final String PATH = "/services/settings";

    private final SettingsServices settingsServices;

    public SettingsController(SettingsServices settingsServices) {
        this.settingsServices = settingsServices;
    }

    /**
     * Find all settings that are currently configured
     *
     * @return {@link <List<SettingResponseDTO>>} Returned setting
     */
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Get
    @RequiredPermission(Permission.CAN_VIEW_SETTINGS)
    public List<SettingsResponseDTO> findAllSettings() {
        return settingsServices.findAllSettings().stream()
                .map(this::fromEntity).toList();
    }

    /**
     * Find setting by its name
     *
     * @param name {@link String} name of the setting
     * @return {@link <SettingResponseDTO>} Returned setting
     */
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Get("/{name}")
    @RequiredPermission(Permission.CAN_VIEW_SETTINGS)
    public SettingsResponseDTO findByName(@PathVariable @NotNull String name) {
        return fromEntity(settingsServices.findByName(name));
    }

    /**
     * Find all available setting options that can be used to configure a new setting.
     * Note: there can only be one setting per unique name
     * @return {@link <SettingOption>} Returned setting options
     */
    @Get("/options")
    @RequiredPermission(Permission.CAN_VIEW_SETTINGS)
    public List<SettingOption> getOptions() {
        return SettingOption.getOptions();
    }

    /**
     * Create and save a new setting.
     * Note: there can only be one setting unique name
     * @param settingDTO, {@link SettingsDTO}
     * @return {@link HttpResponse<SettingsResponseDTO>}
     */
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post
    @RequiredPermission(Permission.CAN_ADMINISTER_SETTINGS)
    public HttpResponse<SettingsResponseDTO> save(@Body @Valid SettingsDTO settingDTO) {
        Setting savedSetting = settingsServices.save(fromDTO(settingDTO));
        URI location = UriBuilder.of(PATH).path(savedSetting.getId().toString()).build();
        return HttpResponse.created(fromEntity(savedSetting), location);
    }
  
    /**
     * Update only the value field of a setting found by its name.
     *
     * @param settingsDTO, {@link SettingsDTO}
     * @return {@link <SettingsReponseDTO>}
     */
    @Put
    @ExecuteOn(TaskExecutors.BLOCKING)
    @RequiredPermission(Permission.CAN_ADMINISTER_SETTINGS)
    public HttpResponse<SettingsResponseDTO> update(@Body @Valid SettingsDTO settingsDTO) {
        Setting savedSetting = settingsServices.update(settingsDTO.getName(), settingsDTO.getValue());
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
                    
    private Setting fromDTO(SettingsDTO settingsDTO) {
        return new Setting(settingsDTO.getName(), settingsDTO.getValue());
    }
      
    private SettingsResponseDTO fromEntity(Setting entity) {
        SettingOption option = SettingOption.fromName(entity.getName());
        SettingsResponseDTO dto = new SettingsResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(option.getDescription());
        dto.setCategory(option.getCategory());
        dto.setType(option.getType());
        dto.setValue(entity.getValue());
        return dto;
    }
}

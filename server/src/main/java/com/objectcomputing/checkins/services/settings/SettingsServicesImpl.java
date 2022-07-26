package com.objectcomputing.checkins.services.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import static com.objectcomputing.checkins.services.validate.Validation.validate;

@Singleton
public class SettingsServicesImpl implements SettingsServices {

    private final SettingsRepository settingsRepository;
    private final CurrentUserServices currentUserServices;

    public SettingsServicesImpl(SettingsRepository settingsRepository, CurrentUserServices currentUserServices) {
        this.settingsRepository = settingsRepository;
        this.currentUserServices = currentUserServices;
    }

    public Setting save(Setting setting) {
        validate(setting.getId() == null).orElseThrow(() -> {
            throw new AlreadyExistsException("This setting already exists");
        });

        return settingsRepository.save(setting);
    }

    public Setting update(Setting setting) {
        validate(setting.getId() != null && settingsRepository.findById(setting.getId()).isPresent()).orElseThrow(() -> {
            throw new BadArgException("Setting %s does not exist, cannot update", setting.getId());
        });

        return settingsRepository.update(setting);
    }

    public List<SettingsResponseDTO> findByName(String name) {
        List<Setting> searchResult = settingsRepository.findByUserId(currentUserServices.getCurrentUser().getId());
        if (name != null) {
            searchResult = searchResult.stream().filter(setting -> name.equalsIgnoreCase(setting.getName()))
                    .collect(Collectors.toList());
        }
        return settingToSettingResponseDTO(searchResult);
    }
    
    public List<SettingsResponseDTO> settingToSettingResponseDTO(List<Setting> settings) {
        List<SettingsResponseDTO> settingResponseDTOs = new ArrayList<>();
        for(Setting setting: settings) {
            SettingsResponseDTO dto = new SettingsResponseDTO();
            dto.setId(setting.getId());
            dto.setName(setting.getName());
            dto.setValue(setting.getValue());
            settingResponseDTOs.add(dto);
        }
        return settingResponseDTOs;
    }

    public Boolean delete(@NotNull UUID id) {
        settingsRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("No setting with id " + id);
        });

        settingsRepository.deleteById(id);
        return true;
    }
}

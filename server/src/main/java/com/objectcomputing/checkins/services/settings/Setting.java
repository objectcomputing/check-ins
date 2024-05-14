package com.objectcomputing.checkins.services.settings;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Entity
@Introspected
@Table(name = "settings")
public class Setting {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the id of the setting")
    private UUID id;

    @NotNull
    @Column(name="name")
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the name of the setting")
    private String name;

    @NotNull
    @Column(name="userid")
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the userId of the setting")
    private UUID userId;

    @NotNull
    @Column(name="value")
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the value of the setting")
    private String value;

    public Setting() {
    }

    public Setting(UUID id, String name, UUID userId, String value) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.value = value;
    }
    
    public Setting(String name, UUID userId, String value) {
        this.name = name;
        this.userId = userId;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Setting)) {
            return false;
        }
        Setting setting = (Setting) o;
        return Objects.equals(id, setting.id) && Objects.equals(name, setting.name) && Objects.equals(userId, setting.userId) && Objects.equals(value, setting.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, userId, value);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", userId='" + getUserId() + "'" +
            ", value='" + getValue() + "'" +
            "}";
    }
    
}
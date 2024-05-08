package com.objectcomputing.checkins.services.settings;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "settings")
public class Setting {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the id of the setting", required = true)
    private UUID id;

    @NotNull
    @Column(name="name")
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the name of the setting", required = true)
    private String name;

    @NotNull
    @Column(name="userid")
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the userId of the setting", required = true)
    private UUID userId;

    @NotNull
    @Column(name="value")
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the value of the setting", required = true)
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

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
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
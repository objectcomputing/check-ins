package com.objectcomputing.checkins.services.reviews;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "review_periods")
public class ReviewPeriod {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "The id of the review period", required = true)
    private UUID id;

    @NotBlank
    @Column(name = "name", unique = true)
    @Schema(description = "The name of the review period", required = true)
    private String name;

    @Column(name = "open")
    @Schema(description = "Whether or not the review period is open")
    private boolean open = true;

    public ReviewPeriod() {
    }

    public ReviewPeriod(String name) {
        this(name, true);
    }

    public ReviewPeriod(UUID id, String name, boolean open) {
        this(name, open);
        this.id = id;
    }

    public ReviewPeriod(String name, boolean open) {
        this.name = name;
        this.open = open;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() { return open; }

    public void setOpen(boolean extraneous) { this.open = open; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewPeriod that = (ReviewPeriod) o;
        return open == that.open && Objects.equals(id, that.id) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, open);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReviewPeriod{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", open=").append(open);
        sb.append('}');
        return sb.toString();
    }
}

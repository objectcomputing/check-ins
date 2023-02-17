package com.objectcomputing.checkins.services.kudos;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Introspected
public class KudosUpdateDTO {
    @Nullable
    private String message;

    @NotNull
    private UUID kudosId;

    @Nullable
    private Boolean approved;

    @Nullable
    public String getMessage() {
        return message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    public UUID getKudosId() {
        return kudosId;
    }

    public void setKudosId(UUID kudosId) {
        this.kudosId = kudosId;
    }

    @Nullable
    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(@Nullable Boolean approved) {
        this.approved = approved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KudosUpdateDTO that = (KudosUpdateDTO) o;
        return Objects.equals(message, that.message) && kudosId.equals(that.kudosId) && Objects.equals(approved, that.approved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, kudosId, approved);
    }

    @Override
    public String toString() {
        return "KudosUpdateDTO{" +
                "message='" + message + '\'' +
                ", kudosId=" + kudosId +
                ", approved=" + approved +
                '}';
    }
}

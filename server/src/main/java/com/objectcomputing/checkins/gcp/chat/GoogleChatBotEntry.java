package com.objectcomputing.checkins.gcp.chat;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "gcp_entries")
public class GoogleChatBotEntry {
//space name, activation status,
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of database entry for google chat bot ", required = true)
    private UUID id;

    @Column(name = "space_id")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the space that corresponds to a certain member and bot chat ", required = true)
    private String spaceId;

    @Column(name = "member_id")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member that the chatbot sends messages to ", required = true)
    private UUID memberId;

    public GoogleChatBotEntry(UUID id, String spaceId, UUID memberId) {
        this.id = id;
        this.spaceId = spaceId;
        this.memberId = memberId;

    }

    public GoogleChatBotEntry( String spaceId, UUID memberId) {
        this.id = null;
        this.spaceId = spaceId;
        this.memberId = memberId;

    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoogleChatBotEntry that = (GoogleChatBotEntry) o;
        return id.equals(that.id) && spaceId.equals(that.spaceId) && memberId.equals(that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, spaceId, memberId);
    }

    //remove person if they deactivate
    //delete chat bot databse entry
    //create chat bot database entry
    //find by member id to repo

}

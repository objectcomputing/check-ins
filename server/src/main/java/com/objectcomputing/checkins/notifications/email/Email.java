package com.objectcomputing.checkins.notifications.email;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "email")
public class Email {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the email", required = true)
    private UUID id;

    @NotNull
    @Column(name = "subject")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "subject of the email", required = true)
    private String subject;

    @NotNull
    @Column(name = "contents")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the contents of the email", required = true)
    private String contents;

    @NotNull
    @Column(name = "sentby")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the user who sent the email", required = true)
    private UUID sentBy;

    @NotNull
    @Column(name = "recipient")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the user who received the email", required = true)
    private UUID recipient;

    @NotNull
    @Column(name = "senddate")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the date the email was sent", required = true)
    private LocalDate sendDate;

    public Email(String subject, String contents, UUID sentBy, UUID recipient, LocalDate sendDate) {
        this.subject = subject;
        this.contents = contents;
        this.sentBy = sentBy;
        this.recipient = recipient;
        this.sendDate = sendDate;
    }

    public Email() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public UUID getSentBy() {
        return sentBy;
    }

    public void setSentBy(UUID sentBy) {
        this.sentBy = sentBy;
    }

    public UUID getRecipient() {
        return recipient;
    }

    public void setRecipient(UUID recipient) {
        this.recipient = recipient;
    }

    public LocalDate getSendDate() {
        return sendDate;
    }

    public void setSendDate(LocalDate sendDate) {
        this.sendDate = sendDate;
    }

    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", subject='" + subject +
                ", contents='" + contents +
                ", sentBy=" + sentBy +
                ", recipient=" + recipient +
                ", sendDate=" + sendDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return id.equals(email.id) &&
                subject.equals(email.subject) &&
                contents.equals(email.contents) &&
                sentBy.equals(email.sentBy) &&
                recipient.equals(email.recipient) &&
                sendDate.equals(email.sendDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subject, contents, sentBy, recipient, sendDate);
    }
}

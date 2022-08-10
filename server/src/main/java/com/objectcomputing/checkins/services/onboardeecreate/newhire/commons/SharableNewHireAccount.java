package com.objectcomputing.checkins.services.onboardeecreate.newhire.commons;
import com.objectcomputing.checkins.services.onboardeecreate.commons.AccountState;
import com.objectcomputing.checkins.services.onboardeecreate.commons.Identifiable;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class SharableNewHireAccount implements Identifiable {
    private final UUID id;
    private final String emailAddress;
    private final AccountState state;
    private final Date createdDate;

    public SharableNewHireAccount(UUID id, String emailAddress, AccountState state, Date createdDate) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.state = state;
        this.createdDate = createdDate;
    }

    public UUID getId() {
        return id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public AccountState getState() {
        return state;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharableNewHireAccount that = (SharableNewHireAccount) o;
        return Objects.equals(id, that.id) && Objects.equals(emailAddress, that.emailAddress) && state == that.state &&  Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, emailAddress, state, createdDate);
    }
}

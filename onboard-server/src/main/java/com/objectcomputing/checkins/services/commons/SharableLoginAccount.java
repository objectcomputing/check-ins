package com.objectcomputing.checkins.services.commons;

import com.objectcomputing.checkins.services.commons.account.AccountRole;
import com.objectcomputing.checkins.services.commons.account.AccountState;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class SharableLoginAccount {
    private UUID id;
    private String emailAddress;
    private Date memberSince;
    private AccountState state;
    private AccountRole role;

    public SharableLoginAccount(UUID id, String emailAddress, Date memberSince, AccountState state, AccountRole role) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.memberSince = memberSince;
        this.state = state;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Date getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(Date memberSince) {
        this.memberSince = memberSince;
    }

    public AccountState getState() {
        return state;
    }

    public void setState(AccountState state) {
        this.state = state;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharableLoginAccount that = (SharableLoginAccount) o;
        return Objects.equals(id, that.id) && Objects.equals(emailAddress, that.emailAddress) && Objects.equals(memberSince, that.memberSince) && state == that.state && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, emailAddress, memberSince, state, role);
    }

    @Override
    public String toString() {
        return "SharableLoginAccount{" +
                "id=" + id +
                ", emailAddress='" + emailAddress + '\'' +
                ", memberSince=" + memberSince +
                ", state=" + state +
                ", role=" + role +
                '}';
    }
}

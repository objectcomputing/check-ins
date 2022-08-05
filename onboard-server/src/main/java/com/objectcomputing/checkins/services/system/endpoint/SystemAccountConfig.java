package com.objectcomputing.checkins.services.system.endpoint;



import java.util.Objects;


public class SystemAccountConfig {
    private String identity;
    private String salt;
    private String verifier;
    private Boolean administrator;
    private String requester;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    public Boolean getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Boolean administrator) {
        this.administrator = administrator;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemAccountConfig that = (SystemAccountConfig) o;
        return Objects.equals(identity, that.identity) && Objects.equals(salt, that.salt) && Objects.equals(verifier, that.verifier) && Objects.equals(administrator, that.administrator) && Objects.equals(requester, that.requester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identity, salt, verifier, administrator, requester);
    }

    @Override
    public String toString() {
        return "SystemAccountConfig{" +
                "identity='" + identity + '\'' +
                ", salt='" + salt + '\'' +
                ", verifier='" + verifier + '\'' +
                ", administrator=" + administrator +
                ", requester='" + requester + '\'' +
                '}';
    }

    com.objectcomputing.checkins.services.system.model
}

package com.objectcomputing.checkins.services.refresh_token;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {

  public RefreshToken() {}

  public RefreshToken(String username, String refreshToken){
    this.userName = username;
    this.refreshToken = refreshToken;
    this.revoked = false;
  }

  @Id
  @Column(name = "id")
  @AutoPopulated
  private UUID id;

  @Column(name = "refresh_token")
  private String refreshToken;

  @Column(name = "user_name")
  private String userName;

  private Boolean revoked;

  @DateCreated
  private Instant dateCreated;

  public String getRefreshToken() {
    return refreshToken;
  }
  public void setRefreshToken(String refreshToken){
    this.refreshToken = refreshToken;
  }

  public String getUserName(){
    return userName;
  }
  public void setUserName(String userName){
    this.userName = userName;
  }

  public UUID getId(){return this.id;}
  public void setId(UUID id){this.id = id;}

  public Boolean getRevoked() {
    return revoked;
  }

  public void setRevoked(Boolean revoked) {
    this.revoked = revoked;
  }

  public Instant getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Instant dateCreated) {
    this.dateCreated = dateCreated;
  }
}

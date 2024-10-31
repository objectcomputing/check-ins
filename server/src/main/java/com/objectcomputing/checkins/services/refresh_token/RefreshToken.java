package com.objectcomputing.checkins.services.refresh_token;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "refresh_token")
public class RefreshToken {

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

  public RefreshToken() {}

  public RefreshToken(String username, String refreshToken){
    this.userName = username;
    this.refreshToken = refreshToken;
    this.revoked = false;
  }

}

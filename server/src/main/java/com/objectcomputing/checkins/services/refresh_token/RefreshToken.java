package com.objectcomputing.checkins.services.refresh_token;

import io.micronaut.data.annotation.AutoPopulated;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {

  public RefreshToken() {}

  public RefreshToken(String username, String refreshToken){
    this.userName = username;
    this.refreshToken = refreshToken;
  }

  @Id
  @Column(name = "id")
  @AutoPopulated
  private UUID id;

  @Column(name = "refresh_token")
  private String refreshToken;

  @Column(name = "user_name")
  private String userName;

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
}

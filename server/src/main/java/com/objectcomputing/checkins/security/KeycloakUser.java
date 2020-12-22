package com.objectcomputing.checkins.security;

//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;

import java.util.List;

//@Getter
//@Setter
//@ToString
public class KeycloakUser {

	private String email;
	private String username;
	private List<String> roles;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "KeycloakUser{" +
				"email='" + email + '\'' +
				", username='" + username + '\'' +
				", roles=" + roles +
				'}';
	}
}

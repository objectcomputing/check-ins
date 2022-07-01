package com.oci.danie1r;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Signer {
    private String email;
    private String signed;
    private String viewed;


    public Signer(@JsonProperty("email")String email,
                  @JsonProperty("signed") String signed,
                  @JsonProperty("viewed") String viewed) {
        this.email = email;
        this.signed = signed;
        this.viewed = viewed;
    }

    public String getEmail() {
        return email;
    }

    public String isSigned() {
        return signed;
    }

    public String isViewed() {
        return viewed;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setSigned(String signed){
        this.signed = signed;
    }

    public void setView(String view){
        this.viewed = view;
    }
    public String toString(){
        return "Signer {" +
                "email '" + email +
                ", signed " + signed +
                ", viewed " + viewed +
                '}';
    }
}

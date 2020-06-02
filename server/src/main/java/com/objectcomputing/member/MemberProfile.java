package com.objectcomputing.member;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name ="member_profile")
public class MemberProfile {

    public MemberProfile() {}

    public MemberProfile(String name,String role,Long pdlId,String location
                        ,String workEmail, String insperityId, Date startDate,
                        String bioText) {
                        this.name=name;
                        this.role=role;
                        this.pdlId=pdlId;
                        this.location=location;
                        this.workEmail=workEmail;
                        this.insperityId=insperityId;
                        this.startDate=startDate;
                        this.bioText=bioText;
                        }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name="role")
    private String role ;

    @Column(name="pdlId")
    private Long pdlId;

    @Column(name="location")
    private String location;

    @Column(name="workEmail")
    private String workEmail;

    @Column(name="insperityId")
    private String insperityId; 

    @Column(name="startDate")
    private Date startDate;

    @Column(name="bioText")
    private String bioText;

}
package com.objectcomputing ;

import io.micronaut.http.annotation.Controller;

@Controller("/team-profile")
public class MemberProfileController {

    protected final MemberProfileRepository memberProfileRepository;

    public MemberProfileController(MemberProfileRepository memberProfileRepository){
        this.memberProfileRepository = memberProfileRepository;
    }

}
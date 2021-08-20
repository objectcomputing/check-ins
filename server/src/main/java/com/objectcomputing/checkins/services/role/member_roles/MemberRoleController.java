package com.objectcomputing.checkins.services.role.member_roles;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/services/role/member")
public class MemberRoleController {

    private MemberRoleService memberRoleService;

    public MemberRoleController(MemberRoleService memberRoleService) {
        this.memberRoleService = memberRoleService;
    }

    @Get
    HttpResponse<List<MemberRole>> getAllMemberRoles() {
        return HttpResponse.ok(memberRoleService.findAll());
    }


    @Delete("/{roleid}/{memberid}")
    HttpResponse<?> deleteMember(@NotNull String roleid, @NotNull String memberid){
        memberRoleService.deleteById(memberid, roleid);
        return HttpResponse.ok();
    }


}

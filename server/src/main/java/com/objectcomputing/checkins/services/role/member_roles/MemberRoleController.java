package com.objectcomputing.checkins.services.role.member_roles;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.List;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/services/roles/members")
public class MemberRoleController {

    private final MemberRoleServices memberRoleServices;

    public MemberRoleController(MemberRoleServices memberRoleServices) {
        this.memberRoleServices = memberRoleServices;
    }

    @Get
    HttpResponse<List<MemberRole>> getAllAssignedRoles() {
        return HttpResponse.ok(memberRoleServices.findAll());
    }


//    @Delete("/{roleid}/{memberid}")
//    HttpResponse<?> deleteMember(@NotNull UUID roleid, @NotNull UUID memberid){
//        memberRoleService.delete(new MemberRole(
//                new MemberRoleId(memberid, roleid), memberid, roleid
//        )
//        );
//        return HttpResponse.ok();
//    }


}

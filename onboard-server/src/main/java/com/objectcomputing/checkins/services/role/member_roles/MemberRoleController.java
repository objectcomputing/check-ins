package com.objectcomputing.checkins.services.role.member_roles;

import com.objectcomputing.checkins.services.role.MemberRoleDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/services/roles/members")
public class MemberRoleController {

    private final MemberRoleServices memberRoleServices;

    public MemberRoleController(MemberRoleServices memberRoleServices) {
        this.memberRoleServices = memberRoleServices;
    }

    @Get
    HttpResponse<List<MemberRoleDTO>> getAllMembersGroupedByRole() {
        List<MemberRoleDTO> membersGroupedByRole = memberRoleServices.getAllMembersGroupedByRole();
        return HttpResponse.ok(membersGroupedByRole);
    }

    @Delete("/{roleId}/{memberId}")
    HttpResponse<?> deleteMemberRole(@NotNull UUID roleId, @NotNull UUID memberId){
        memberRoleServices.delete(new MemberRoleId(memberId, roleId));
        return HttpResponse.ok();
    }

    @Post
    HttpResponse<MemberRole> saveMemberRole(@NotNull MemberRoleId id){
        MemberRole memberRole = memberRoleServices.saveByIds(id.getMemberId(), id.getRoleId());
        return HttpResponse.ok(memberRole);
    }

}

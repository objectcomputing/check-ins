package com.objectcomputing.checkins.services.role.member_roles;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.cache.annotation.CacheInvalidate;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/services/roles/members")
public class MemberRoleController {

    private final MemberRoleServices memberRoleServices;

    public MemberRoleController(MemberRoleServices memberRoleServices) {
        this.memberRoleServices = memberRoleServices;
    }

    @Get
    HttpResponse<List<MemberRole>> getAllAssignedMemberRoles() {
        return HttpResponse.ok(memberRoleServices.findAll());
    }

    @Delete("/{roleId}/{memberId}")
    HttpResponse<?> deleteMemberRole(@NotNull UUID roleId, @NotNull UUID memberId){
        memberRoleServices.delete(new MemberRoleId(memberId, roleId));
        return HttpResponse.ok();
    }

    @Post
    @CacheInvalidate(cacheNames = {"role-permission-cache"})
    HttpResponse<MemberRole> saveMemberRole(@NotNull @Body MemberRoleId id){
        MemberRole memberRole = memberRoleServices.saveByIds(id.getMemberId(), id.getRoleId());
        return HttpResponse.ok(memberRole);
    }

}

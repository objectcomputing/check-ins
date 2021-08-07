package com.objectcomputing.checkins.services.rale.member;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/rales/members")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "rale-member")
public class RaleMemberController {

    private final RaleMemberServices raleMemberServices;

    public RaleMemberController(RaleMemberServices raleMemberServices) {
        this.raleMemberServices = raleMemberServices;
    }

    /**
     * Create and save a new raleMember.
     *
     * @param raleMember, {@link RaleMemberResponseDTO}
     * @return {@link HttpResponse <RaleMember>}
     */
    @Post()
    public HttpResponse<RaleMember> createMembers(@Body @Valid RaleMemberCreateDTO raleMember,
                                                  HttpRequest<RaleMemberResponseDTO> request) {
        RaleMember newRaleMember = raleMemberServices.save(new RaleMember(raleMember.getRaleId(),
                raleMember.getMemberId(), raleMember.getLead()));
        return HttpResponse
                .created(newRaleMember)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), newRaleMember.getId()))));
    }

    /**
     * Update raleMember.
     *
     * @param raleMember, {@link RaleMember}
     * @return {@link HttpResponse<RaleMember>}
     */
    @Put()
    public HttpResponse<?> updateMembers(@Body @Valid RaleMemberUpdateDTO raleMember, HttpRequest<RaleMember> request) {
        RaleMember updatedRaleMember = raleMemberServices.update(new RaleMember(raleMember.getId(), raleMember.getRaleId(), raleMember.getMemberId(), raleMember.getLead()));
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), updatedRaleMember.getId()))))
                .body(updatedRaleMember);

    }

    /**
     * Get RaleMember based off id
     *
     * @param id {@link UUID} of the rale member entry
     * @return {@link RaleMember}
     */
    @Get("/{id}")
    public RaleMember readRaleMember(UUID id) {
        return raleMemberServices.read(id);
    }

    /**
     * Find rale members that match all filled in parameters, return all results when given no params
     *
     * @param raleId   {@link UUID} of rale
     * @param memberId {@link UUID} of member
     * @param lead,    is lead of the rale
     * @return {@link List < Rale > list of rales}
     */
    @Get("/{?raleId,memberId,lead}")
    public Set<RaleMember> findRaleMembers(@Nullable UUID raleId,
                                           @Nullable UUID memberId,
                                           @Nullable Boolean lead) {
        return raleMemberServices.findByFields(raleId, memberId, lead);
    }

    /**
     * Delete A RaleMember
     *
     * @param id, id of {@link UUID} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteRaleMember(@NotNull UUID id) {
        raleMemberServices.delete(id);
        return HttpResponse
                .ok();
    }
}
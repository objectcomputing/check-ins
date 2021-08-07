package com.objectcomputing.checkins.services.rale.member;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.rale.Rale;
import com.objectcomputing.checkins.services.rale.RaleRepository;

import io.micronaut.core.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class RaleMemberServicesImpl implements RaleMemberServices {

    private final RaleRepository raleRepo;
    private final RaleMemberRepository raleMemberRepo;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;
//    private final MemberHistoryRepository memberHistoryRepository;

    public RaleMemberServicesImpl(RaleRepository raleRepo,
                                  RaleMemberRepository raleMemberRepo,
                                  MemberProfileRepository memberRepo,
                                  CurrentUserServices currentUserServices) {
        this.raleRepo = raleRepo;
        this.raleMemberRepo = raleMemberRepo;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
    }


    public RaleMember save(@Valid @NotNull RaleMember raleMember) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        final UUID raleId = raleMember.getRaleId();
        final UUID memberId = raleMember.getMemberId();

        Optional<Rale> rale = raleRepo.findById(raleId);
        if (rale.isEmpty()) {
            throw new BadArgException(String.format("Rale %s doesn't exist", raleId));
        }

        Set<RaleMember> raleLeads = this.findByFields(raleId, null, true);

        if (raleMember.getId() != null) {
            throw new BadArgException(String.format("Found unexpected id %s for rale member", raleMember.getId()));
        } else if (memberRepo.findById(memberId).isEmpty()) {
            throw new BadArgException(String.format("Member %s doesn't exist", memberId));
        } else if (raleMemberRepo.findByRaleIdAndMemberId(raleMember.getRaleId(), raleMember.getMemberId()).isPresent()) {
            throw new BadArgException(String.format("Member %s already exists in rale %s", memberId, raleId));
        } else if (!isAdmin && raleLeads.size() > 0 && raleLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
            throw new BadArgException("You are not authorized to perform this operation");
        }

        RaleMember newRaleMember = raleMemberRepo.save(raleMember);
        return newRaleMember;
    }

    public RaleMember read(@NotNull UUID id) {
        return raleMemberRepo.findById(id).orElse(null);
    }

    public RaleMember update(@NotNull @Valid RaleMember raleMember) {

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        final UUID id = raleMember.getId();
        final UUID raleId = raleMember.getRaleId();
        final UUID memberId = raleMember.getMemberId();
        Optional<Rale> rale = raleRepo.findById(raleId);

        if (rale.isEmpty()) {
            throw new BadArgException(String.format("Rale %s doesn't exist", raleId));
        }

        Set<RaleMember> raleLeads = this.findByFields(raleId, null, true);

        if (id == null || raleMemberRepo.findById(id).isEmpty()) {
            throw new BadArgException(String.format("Unable to locate raleMember to update with id %s", id));
        } else if (memberRepo.findById(memberId).isEmpty()) {
            throw new BadArgException(String.format("Member %s doesn't exist", memberId));
        } else if (raleMemberRepo.findByRaleIdAndMemberId(raleMember.getRaleId(), raleMember.getMemberId()).isEmpty()) {
            throw new BadArgException(String.format("Member %s is not part of rale %s", memberId, raleId));
        } else if (!isAdmin && raleLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
            throw new BadArgException("You are not authorized to perform this operation");
        }

        RaleMember raleMemberUpdate = raleMemberRepo.update(raleMember);
        return raleMemberUpdate;
    }

    public Set<RaleMember> findByFields(@Nullable UUID raleId, @Nullable UUID memberId, @Nullable Boolean lead) {
        Set<RaleMember> raleMembers = new HashSet<>();
        raleMemberRepo.findAll().forEach(raleMembers::add);

        if (raleId != null) {
            raleMembers.retainAll(raleMemberRepo.findByRaleId(raleId));
        }
        if (memberId != null) {
            raleMembers.retainAll(raleMemberRepo.findByMemberId(memberId));
        }
        if (lead != null) {
            raleMembers.retainAll(raleMemberRepo.findByLead(lead));
        }

        return raleMembers;
    }

    public void delete(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        RaleMember raleMember = raleMemberRepo.findById(id).orElse(null);
        if (raleMember != null) {
            Set<RaleMember> raleLeads = this.findByFields(raleMember.getRaleId(), null, true);

            if (!isAdmin && raleLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
                throw new PermissionException("You are not authorized to perform this operation");
            } else {
                raleMemberRepo.deleteById(id);
            }
        } else {
            throw new NotFoundException(String.format("Unable to locate raleMember with id %s", id));
        }

        raleMemberRepo.delete(raleMember);
    }

    public void deleteByRale(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        List<RaleMember> raleMembers = raleMemberRepo.findByRaleId(id);
        if (raleMembers != null) {
            List<RaleMember> raleLeads = raleMembers.stream().filter((member) -> member.isLead()).collect(Collectors.toList());

            if (!isAdmin && raleLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
                throw new PermissionException("You are not authorized to perform this operation");
            } else {
                raleMembers.forEach(member -> {
                    raleMemberRepo.deleteById(member.getId());
                });
            }
        } else {
            throw new NotFoundException(String.format("Unable to locate rale with id %s", id));
        }
    }
}
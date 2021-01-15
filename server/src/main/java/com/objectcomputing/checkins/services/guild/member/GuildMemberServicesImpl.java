package com.objectcomputing.checkins.services.guild.member;

import com.objectcomputing.checkins.services.exceptions.BadArgException;
import com.objectcomputing.checkins.services.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.exceptions.PermissionException;
import com.objectcomputing.checkins.services.guild.GuildRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class GuildMemberServicesImpl implements GuildMemberServices {

    private final GuildRepository guildRepo;
    private final GuildMemberRepository guildMemberRepo;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;

    public GuildMemberServicesImpl(GuildRepository guildRepo, GuildMemberRepository guildMemberRepo, MemberProfileRepository memberRepo, CurrentUserServices currentUserServices) {
        this.guildRepo = guildRepo;
        this.guildMemberRepo = guildMemberRepo;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
    }

    public GuildMember save(GuildMember guildMember) {
        GuildMember guildMemberRet = null;
        if (guildMember != null) {
            final UUID guildId = guildMember.getGuildid();
            final UUID memberId = guildMember.getMemberid();

            if (guildId == null || memberId == null) {
                throw new BadArgException(String.format("Invalid guildMember %s", guildMember));
            } else if (guildMember.getId() != null) {
                throw new BadArgException(String.format("Found unexpected id %s for guild member", guildMember.getId()));
            } else if (guildRepo.findById(guildId).isEmpty()) {
                throw new BadArgException(String.format("Guild %s doesn't exist", guildId));
            } else if (memberRepo.findById(memberId).isEmpty()) {
                throw new BadArgException(String.format("Member %s doesn't exist", memberId));
            } else if (!guildMemberRepo.search(guildMember.getGuildid().toString(),
                    guildMember.getMemberid().toString(), guildMember.isLead()).isEmpty()) {
                throw new BadArgException(String.format("Member %s already exists in guild %s", memberId, guildId));
            }

            guildMemberRet = guildMemberRepo.save(guildMember);
        }
        return guildMemberRet;
    }

    public GuildMember read(@NotNull UUID id) {
        return guildMemberRepo.findById(id).orElse(null);
    }

    public GuildMember update(GuildMember guildMember) {
        GuildMember guildMemberRet = null;
        if (guildMember != null) {
            final UUID id = guildMember.getId();
            final UUID guildId = guildMember.getGuildid();
            final UUID memberId = guildMember.getMemberid();

            if (guildId == null || memberId == null) {
                throw new BadArgException(String.format("Invalid guildMember %s", guildMember));
            } else if (id == null || !guildMemberRepo.findById(id).isPresent()) {
                throw new BadArgException(String.format("Unable to locate guildMember to update with id %s", id));
            } else if (!guildRepo.findById(guildId).isPresent()) {
                throw new BadArgException(String.format("Guild %s doesn't exist", guildId));
            } else if (!memberRepo.findById(memberId).isPresent()) {
                throw new BadArgException(String.format("Member %s doesn't exist", memberId));
            }

            guildMemberRet = guildMemberRepo.update(guildMember);
        }
        return guildMemberRet;
    }

    public Set<GuildMember> findByFields(UUID guildid, UUID memberid, Boolean lead) {
        return guildMemberRepo.search(nullSafeUUIDToString(guildid), nullSafeUUIDToString(memberid), lead);
    }

    public Boolean delete(@NotNull UUID id) {

        GuildMember guildMemberResult = guildMemberRepo.findById(id).orElse(null);
        MemberProfile currentUser = currentUserServices.getCurrentUser();

        if (guildMemberResult == null) {
            throw new NotFoundException(String.format("No guild member for id %s", id));
        }

        if ( (!currentUserServices.isAdmin() ||
                (!guildMemberResult.isLead()) ||
                (!currentUser.getId().equals(guildMemberResult.getMemberid())))) {
            throw new PermissionException("You are not authorized to perform this operation");
        }

//        if (!currentUserServices.isAdmin()) {
//            throw new PermissionException("You are not authorized to perform this operation");
//        } else if (!guildMemberResult.isLead()) {
//            throw new PermissionException("You are not authorized to perform this operation");
//        } else if (currentUser.getId() != guildMemberResult.getMemberid()) {
//            throw new PermissionException("You are not authorized to perform this operation");
//        }

        guildMemberRepo.deleteById(id);
        return true;
    }

}

package com.objectcomputing.checkins.services.guild.member;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetConfig;
import com.objectcomputing.checkins.services.guild.Guild;
import com.objectcomputing.checkins.services.guild.GuildRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.Validation.validate;

@Singleton
public class GuildMemberServicesImpl implements GuildMemberServices {

    private final GuildRepository guildRepo;
    private final GuildMemberRepository guildMemberRepo;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;
    private final GuildMemberHistoryRepository guildMemberHistoryRepository;
    private EmailSender emailSender;
    private final String webAddress;
    public static final String WEB_ADDRESS = "check-ins.web-address";

    public GuildMemberServicesImpl(GuildRepository guildRepo,
                                   GuildMemberRepository guildMemberRepo,
                                   MemberProfileRepository memberRepo,
                                   CurrentUserServices currentUserServices,
                                   GuildMemberHistoryRepository guildMemberHistoryRepository,
                                   @Named(MailJetConfig.HTML_FORMAT) EmailSender emailSender,
                                   @Property(name = WEB_ADDRESS) String webAddress
    ) {
        this.guildRepo = guildRepo;
        this.guildMemberRepo = guildMemberRepo;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
        this.guildMemberHistoryRepository=guildMemberHistoryRepository;
        this.emailSender = emailSender;
        this.webAddress = webAddress;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public GuildMember save(@Valid @NotNull GuildMember guildMember) {
        final UUID guildId = guildMember.getGuildId();
        final UUID memberId = guildMember.getMemberId();
        Guild guild = guildRepo.findById(guildId).orElseThrow(() -> {
            throw new BadArgException("Guild %s doesn't exist", guildId);
        });
        MemberProfile currentUser = currentUserServices.getCurrentUser();

        Set<GuildMember> guildLeads = this.findByFields(guildMember.getGuildId(), null, true);
        boolean isLead = guildLeads.stream().anyMatch(o -> o.getMemberId().equals(currentUser.getId()));

        validate(guildMember.getId() == null).orElseThrow(() -> {
            throw new BadArgException("Found unexpected id %s for Guild member", guildMember.getId());
        });
        memberRepo.findById(memberId).orElseThrow(() -> {
            throw new BadArgException("Member %s doesn't exist", memberId);
        });
        validate(guildMemberRepo.findByGuildIdAndMemberId(guildMember.getGuildId(), guildMember.getMemberId()).isEmpty()).orElseThrow(() -> {
            throw new BadArgException("Member %s already exists in guild %s", memberId, guildId);
        });

        // only allow admins to create guild leads
        validate(currentUserServices.isAdmin() || !guildMember.isLead()).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to perform this operation");
        });
        // only admins and leads can add members to guilds unless a user adds themself
        validate(currentUserServices.isAdmin() || guildMember.getMemberId().equals(currentUser.getId()) || isLead).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to perform this operation");
        });

        emailSender
                .sendEmail("Membership changes have been made to the " + guild.getName() + " guild",
                        constructEmailContent(guildMember, true),
                        getGuildLeadsEmails(guildLeads, guildMember).toArray(new String[0])
                );

        GuildMember guildMemberSaved = guildMemberRepo.save(guildMember);
        guildMemberHistoryRepository.save(buildGuildMemberHistory(guildId,memberId,"Added", LocalDateTime.now()));
        return guildMemberSaved;
    }

    public GuildMember read(@NotNull UUID id) {
        return guildMemberRepo.findById(id).orElse(null);
    }

    public GuildMember update(@NotNull @Valid GuildMember guildMember) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        final UUID id = guildMember.getId();
        final UUID guildId = guildMember.getGuildId();
        final UUID memberId = guildMember.getMemberId();
        guildRepo.findById(guildId).orElseThrow(() -> {
            throw new BadArgException("Guild %s doesn't exist", guildId);
        });

        Set<GuildMember> guildLeads = this.findByFields(guildId, null, true);

        validate(id != null && guildMemberRepo.findById(id).isPresent()).orElseThrow(() -> {
            throw new BadArgException("Unable to locate guildMember to update with id %s", id);
        });
        memberRepo.findById(memberId).orElseThrow(() -> {
            throw new BadArgException("Member %s doesn't exist", memberId);
        });
        guildMemberRepo.findByGuildIdAndMemberId(guildMember.getGuildId(), guildMember.getMemberId()).orElseThrow(() -> {
            throw new BadArgException("Member %s is not part of guild %s", memberId, guildId);
        });

        validate(isAdmin || guildLeads.stream().anyMatch(o -> o.getMemberId().equals(currentUser.getId()))).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to perform this operation");
        });

        GuildMember guildMemberUpdate = guildMemberRepo.update(guildMember);
        guildMemberHistoryRepository.save(buildGuildMemberHistory(guildId,memberId,"Updated", LocalDateTime.now()));
        return guildMemberUpdate;
    }

    public Set<GuildMember> findByFields(@Nullable UUID guildId, @Nullable UUID memberId, @Nullable Boolean lead) {
        Set<GuildMember> guildMembers = new HashSet<>();
        guildMemberRepo.findAll().forEach(guildMembers::add);

        if (guildId != null) {
            guildMembers.retainAll(guildMemberRepo.findByGuildId(guildId));
        }
        if (memberId != null) {
            guildMembers.retainAll(guildMemberRepo.findByMemberId(memberId));
        }
        if (lead != null) {
            guildMembers.retainAll(guildMemberRepo.findByLead(lead));
        }

        return guildMembers;
    }

    public void delete(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        GuildMember guildMember = guildMemberRepo.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Unable to locate guildMember with id %s", id);
        });

        Set<GuildMember> guildLeads = this.findByFields(guildMember.getGuildId(), null, true);
        boolean currentUserIsLead = guildLeads.stream().anyMatch(o -> o.getMemberId().equals(currentUser.getId()));

        // don't allow guild lead to be removed if they are the only lead
        validate(!guildMember.isLead() || guildLeads.size() > 1).orElseThrow(() -> {
            throw new BadArgException("At least one guild lead must be present in the guild at all times");
        });

        // if the current user is not an admin, is not the same as the member in the request, and is not a lead in the guild -> don't delete
        validate(currentUserServices.isAdmin() || guildMember.getMemberId().equals(currentUser.getId()) || currentUserIsLead).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to perform this operation");
        });

        Guild guild = guildRepo.findById(guildMember.getGuildId()).orElseThrow(() -> {
            throw new NotFoundException("No Guild found with id %s", guildMember.getGuildId());
        });

        emailSender
                .sendEmail("Membership Changes have been made to the " + guild.getName() + " guild",
                        constructEmailContent(guildMember, false),
                        getGuildLeadsEmails(guildLeads, guildMember).toArray(new String[0])
                );

        guildMemberRepo.deleteById(id);
        guildMemberHistoryRepository.save(buildGuildMemberHistory(guildMember.getGuildId(),guildMember.getMemberId(),"Deleted", LocalDateTime.now()));
    }

    private Set<String> getGuildLeadsEmails(Set<GuildMember> guildLeads, GuildMember guildMember){
        // remove email from set of guildleads so they aren't included in email
        if (guildMember.isLead()){
            guildLeads.remove(guildMember);
        }
        Set<String> guildLeadEmails = new HashSet<>();
        guildLeads.forEach(o -> {
            memberRepo.findById(o.getMemberId()).ifPresent(memberProfile -> guildLeadEmails.add(memberProfile.getWorkEmail()));
        });
        return guildLeadEmails;
    }


    private String constructEmailContent (GuildMember guildMember, Boolean isAdded){
        MemberProfile memberProfile = memberRepo.findById(guildMember.getMemberId())
                .orElseThrow(() -> new NotFoundException("No member profile found for guild member with memberid " + guildMember.getMemberId()));
        Guild guild = guildRepo.findById(guildMember.getGuildId())
                .orElseThrow(() -> new NotFoundException("No guild found for guild id " + guildMember.getGuildId()));

        String joinedOrLeft = (isAdded) ? "joined" : "left";
        String emailHtml =
                "<h3>" + memberProfile.getFirstName() + " " + memberProfile.getLastName() + " has " + joinedOrLeft + " the " + guild.getName() + " guild.</h3>";

        emailHtml += "<a href=\"" + webAddress + "/guilds\">Click here</a> to view the changes in the Check-Ins app.";

        return emailHtml;
    }

    private static GuildMemberHistory buildGuildMemberHistory(UUID guildId, UUID memberId, String change, LocalDateTime date) {
        return new GuildMemberHistory(guildId,memberId,change,date);
    }
}

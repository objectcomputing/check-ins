package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.Environments;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetConfig;
import com.objectcomputing.checkins.services.guild.member.*;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.env.Environment;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import static com.objectcomputing.checkins.util.Validation.validate;

@Singleton
public class GuildServicesImpl implements GuildServices {

    private final GuildRepository guildsRepo;
    private final GuildMemberRepository guildMemberRepo;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    private final GuildMemberServices guildMemberServices;
    private EmailSender emailSender;
    private final Environment environment;
    private final String webAddress;
    public static final String WEB_ADDRESS = "check-ins.web-address";

    public GuildServicesImpl(GuildRepository guildsRepo,
                             GuildMemberRepository guildMemberRepo,
                             CurrentUserServices currentUserServices,
                             MemberProfileServices memberProfileServices,
                             GuildMemberServices guildMemberServices,
                             @Named(MailJetConfig.HTML_FORMAT) EmailSender emailSender,
                             Environment environment,
                             @Property(name = WEB_ADDRESS) String webAddress
    ) {
        this.guildsRepo = guildsRepo;
        this.guildMemberRepo = guildMemberRepo;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
        this.guildMemberServices = guildMemberServices;
        this.emailSender = emailSender;
        this.webAddress = webAddress;
        this.environment = environment;
    }

    public void setEmailSender(EmailSender emailSender){
        this.emailSender = emailSender;
    }

    public void validateLink(String link) {
        if (link == null) {
            return;
        }

        try {
            new URL(link).toURI();
        }  catch (Exception e) {
            throw new BadArgException("Link is invalid");
        }
    }

    public GuildResponseDTO save(GuildCreateDTO guildDTO) {
        Guild newGuildEntity = null;
        List<GuildMemberResponseDTO> newMembers = new ArrayList<>();
        if (guildDTO != null) {
            String link = guildDTO.getLink();
            validateLink(link);

            validate(guildsRepo.search(guildDTO.getName(), null).isEmpty()).orElseThrow(() -> {
                throw new BadArgException("Guild with name %s already exists", guildDTO.getName());
            });

            boolean guildHasLeads = guildDTO.getGuildMembers() != null &&
                    guildDTO.getGuildMembers().stream().anyMatch(GuildCreateDTO.GuildMemberCreateDTO::getLead);
            validate(guildHasLeads).orElseThrow(() -> {
                throw new BadArgException("Guild must include at least one guild lead");
            });

            newGuildEntity = guildsRepo.save(fromDTO(guildDTO));
            for (GuildCreateDTO.GuildMemberCreateDTO memberDTO : guildDTO.getGuildMembers()) {
                MemberProfile existingMember = memberProfileServices.getById(memberDTO.getMemberId());
                newMembers.add(fromMemberEntity(guildMemberRepo.save(fromMemberDTO(memberDTO, newGuildEntity.getId())), existingMember));
            }
        }

        return fromEntity(newGuildEntity, newMembers);
    }

    public GuildResponseDTO read(@NotNull UUID guildId) {
        Guild foundGuild = guildsRepo.findById(guildId).orElseThrow(() -> new NotFoundException("No such guild found"));

        List<GuildMemberResponseDTO> guildMembers = guildMemberRepo
                .findByGuildId(guildId)
                .stream()
                .filter(guildMember -> {
                    LocalDate terminationDate = memberProfileServices.getById(guildMember.getMemberId()).getTerminationDate();
                    return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
                })
                .map(guildMember ->
                        fromMemberEntity(guildMember, memberProfileServices.getById(guildMember.getMemberId()))).collect(Collectors.toList());

        return fromEntity(foundGuild, guildMembers);
    }

    public GuildResponseDTO update(GuildUpdateDTO guildDTO) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        boolean isGuildLead = currentUser != null &&
                !guildMemberServices.findByFields(guildDTO.getId(), currentUser.getId(), true).isEmpty();
        validate(isAdmin || isGuildLead).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to perform this operation");
        });

        // Guild newGuildEntity = null;
        GuildResponseDTO updated= null;
        List<GuildMemberResponseDTO> newMembers = new ArrayList<>();
        if (guildDTO != null) {

            validate(guildDTO.getId() != null && guildsRepo.findById(guildDTO.getId()).isPresent()).orElseThrow(() -> {
                throw new BadArgException("Guild ID %s does not exist, can't update.", guildDTO.getId());
            });

            validate(guildDTO.getGuildMembers() != null &&
                    guildDTO.getGuildMembers().stream().anyMatch(GuildUpdateDTO.GuildMemberUpdateDTO::getLead)).orElseThrow(() -> {
                throw new BadArgException("Guild must include at least one guild lead");
            });

            String link = guildDTO.getLink();
            validateLink(link);

            // track membership changes for email notification
            List<MemberProfile> addedMembers = new ArrayList<>();
            List<MemberProfile> removedMembers = new ArrayList<>();
            // emails of guild leads excluding the one making the change request

            Set<String> emailsOfGuildLeads = guildMemberServices.findByFields(guildDTO.getId(), null, true)
                    .stream()
                    .filter(lead -> currentUser != null && !lead.getMemberId().equals(currentUser.getId()))
                    .map(lead -> memberProfileServices.getById(lead.getMemberId()).getWorkEmail())
                    .collect(Collectors.toSet());

            Guild newGuildEntity  = guildsRepo.update(fromDTO(guildDTO));
            Set<GuildMember> existingGuildMembers = guildMemberServices.findByFields(guildDTO.getId(), null, null);

            //add new members to the guild
            guildDTO.getGuildMembers().forEach((updatedMember) -> {
                Optional<GuildMember> first = existingGuildMembers.stream().filter((existing) -> existing.getMemberId().equals(updatedMember.getMemberId())).findFirst();
                MemberProfile existingMember = memberProfileServices.getById(updatedMember.getMemberId());
                if (first.isEmpty()) {
                    newMembers.add(fromMemberEntity(guildMemberServices.save(fromMemberDTO(updatedMember, newGuildEntity.getId())), existingMember));
                    addedMembers.add(existingMember);
                } else {
                    newMembers.add(fromMemberEntity(guildMemberServices.update(fromMemberDTO(updatedMember, newGuildEntity.getId())), existingMember));
                }
            });

            //delete any removed members from guild
            existingGuildMembers.forEach((existingMember) -> {
                if (guildDTO.getGuildMembers().stream().noneMatch((updatedTeamMember) -> updatedTeamMember.getMemberId().equals(existingMember.getMemberId()))) {
                    guildMemberServices.delete(existingMember.getId());
                    removedMembers.add(memberProfileServices.getById(existingMember.getMemberId()));
                }
            });
            updated = fromEntity(newGuildEntity, newMembers);

            if (!emailsOfGuildLeads.isEmpty() && (!addedMembers.isEmpty() || !removedMembers.isEmpty())){
                sendGuildMemberChangeNotification(addedMembers, removedMembers, newGuildEntity.getName(), emailsOfGuildLeads);
            }
        }

        return updated;
    }


    public Set<GuildResponseDTO> findByFields(String name, UUID memberid) {
        Set<GuildResponseDTO> foundGuilds = guildsRepo.search(name, nullSafeUUIDToString(memberid)).stream().map(this::fromEntity).collect(Collectors.toSet());
        //TODO: revisit this in a way that will allow joins.
        for (GuildResponseDTO foundGuild : foundGuilds) {
            Set<GuildMember> foundMembers = guildMemberRepo.findByGuildId(foundGuild.getId()).stream().filter(guildMember -> {
                LocalDate terminationDate = memberProfileServices.getById(guildMember.getMemberId()).getTerminationDate();
                return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
            }).collect(Collectors.toSet());

            for (GuildMember foundMember : foundMembers) {
                foundGuild.getGuildMembers().add(fromMemberEntity(foundMember, memberProfileServices.getById(foundMember.getMemberId())));
            }
        }
        return foundGuilds;
    }

    public boolean delete(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        boolean isGuildLead = currentUser != null &&
                !guildMemberRepo.search(nullSafeUUIDToString(id), nullSafeUUIDToString(currentUser.getId()), true).isEmpty();
        validate(isAdmin || isGuildLead).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to perform this operation");
        });

        guildMemberRepo.deleteByGuildId(id.toString());
        guildsRepo.deleteById(id);

        return true;
    }

    private Guild fromDTO(GuildUpdateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Guild(dto.getId(), dto.getName(), dto.getDescription(), dto.getLink());
    }

    private GuildMember fromMemberDTO(GuildCreateDTO.GuildMemberCreateDTO memberDTO, UUID guildId) {
        return new GuildMember(null, guildId, memberDTO.getMemberId(), memberDTO.getLead());
    }

    private GuildMember fromMemberDTO(GuildMemberResponseDTO memberDTO, UUID guildId, MemberProfile savedMember) {
        return new GuildMember(memberDTO.getId() == null ? null : memberDTO.getId(), guildId, savedMember.getId(), memberDTO.isLead());
    }

    private GuildMember fromMemberDTO(GuildUpdateDTO.GuildMemberUpdateDTO memberDTO, UUID guildId) {
        return new GuildMember(memberDTO.getId(), guildId, memberDTO.getMemberId(), memberDTO.getLead());
    }

    private GuildResponseDTO fromEntity(Guild entity) {
        return fromEntity(entity, new ArrayList<>());
    }

    private GuildResponseDTO fromEntity(Guild entity, List<GuildMemberResponseDTO> memberEntities) {
        if (entity == null) {
            return null;
        }
        GuildResponseDTO dto = new GuildResponseDTO(entity.getId(), entity.getName(), entity.getDescription(), entity.getLink());
        dto.setGuildMembers(memberEntities);
        return dto;
    }

    private Guild fromDTO(GuildCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Guild(null, dto.getName(), dto.getDescription(), dto.getLink());
    }

    private GuildMemberResponseDTO fromMemberEntity(GuildMember guildMember, MemberProfile memberProfile) {
        if (guildMember == null || memberProfile == null) {
            return null;
        }
        return new GuildMemberResponseDTO(guildMember.getId(), memberProfile.getFirstName(), memberProfile.getLastName(),
                memberProfile.getId(), guildMember.isLead());
    }


    private void sendGuildMemberChangeNotification(List<MemberProfile> addedMembers, List<MemberProfile> removedMembers,
                                                    String guildName, Set<String> emailsOfGuildLeads) {
        // don't send emails in local environment
        if (environment.getActiveNames().contains(Environments.LOCAL)) return;

        String emailContent = constructEmailContent(addedMembers, removedMembers, guildName);
        String subject = "Membership Changes have been made to the " + guildName +" guild";
        emailSender.sendEmail(subject, emailContent, emailsOfGuildLeads.toArray(new String[0]));

    }

    private String constructEmailContent (List<MemberProfile> addedMembers, List<MemberProfile> removedMembers, String guildName){
        String emailHtml = "<h3>Changes have been made to the " + guildName + " guild.</h3>";
        if (!addedMembers.isEmpty()){
            StringBuilder addedMembersHtml = new StringBuilder("<h4>The following members have been added:</h4><ul>");
            for (MemberProfile member : addedMembers) {
                String li = "<li>" + member.getFirstName() + " " + member.getLastName() + "</li>";
                addedMembersHtml.append(li);
            }
            addedMembersHtml.append("</ul>");
            emailHtml += addedMembersHtml;
        }
        if (!removedMembers.isEmpty()){
            StringBuilder removedMembersHtml = new StringBuilder("<h4>The following members have been removed:</h4><ul>");
            for (MemberProfile member : removedMembers) {
                String li = "<li>" + member.getFirstName() + " " + member.getLastName() + "</li>";
                removedMembersHtml.append(li);
            }
            removedMembersHtml.append("</ul>");
            emailHtml += removedMembersHtml;
        }
        emailHtml += "<a href=\"" + webAddress + "/guilds\">Click here</a> to view the changes in the Check-Ins app.";
        return emailHtml;
    }
}

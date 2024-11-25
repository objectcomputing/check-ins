package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.notifications.social_media.SlackPoster;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipient;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipientRepository;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipientServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.TeamRepository;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.transaction.annotation.Transactional;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Set;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
class KudosServicesImpl implements KudosServices {
    private static final Logger LOG = LoggerFactory.getLogger(KudosServicesImpl.class);
    public static final String KUDOS_DOES_NOT_EXIST_MSG = "Kudos with id %s does not exist";
    public static final String KUDOS_EMAIL_SUBJECT = "Kudos";
    private final KudosRepository kudosRepository;
    private final KudosRecipientServices kudosRecipientServices;
    private final KudosRecipientRepository kudosRecipientRepository;
    private final TeamRepository teamRepository;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final CurrentUserServices currentUserServices;
    private final EmailSender emailSender;
    private final CheckInsConfiguration checkInsConfiguration;
    private final RoleServices roleServices;
    private final MemberProfileServices memberProfileServices;
    private final SlackPoster slackPoster;

    private enum NotificationType {
        creation, approval
    }

    KudosServicesImpl(KudosRepository kudosRepository,
                             KudosRecipientServices kudosRecipientServices,
                             KudosRecipientRepository kudosRecipientRepository,
                             TeamRepository teamRepository,
                             MemberProfileRetrievalServices memberProfileRetrievalServices,
                             CurrentUserServices currentUserServices,
                             RoleServices roleServices,
                             MemberProfileServices memberProfileServices,
                             @Named(MailJetFactory.HTML_FORMAT) EmailSender emailSender,
                             CheckInsConfiguration checkInsConfiguration,
                             SlackPoster slackPoster) {
        this.kudosRepository = kudosRepository;
        this.kudosRecipientServices = kudosRecipientServices;
        this.kudosRecipientRepository = kudosRecipientRepository;
        this.teamRepository = teamRepository;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
        this.memberProfileServices = memberProfileServices;
        this.roleServices = roleServices;
        this.currentUserServices = currentUserServices;
        this.emailSender = emailSender;
        this.checkInsConfiguration = checkInsConfiguration;
        this.slackPoster = slackPoster;
    }

    @Override
    @Transactional
    public Kudos save(KudosCreateDTO kudosDTO) {
        UUID senderId = kudosDTO.getSenderId();
        if (memberProfileRetrievalServices.getById(senderId).isEmpty()) {
            throw new BadArgException("Kudos sender %s does not exist".formatted(senderId));
        }

        if (kudosDTO.getTeamId() != null) {
            UUID teamId = kudosDTO.getTeamId();
            if (teamRepository.findById(teamId).isEmpty()) {
                throw new BadArgException("Team %s does not exist".formatted(teamId));
            }
        }

        if (kudosDTO.getRecipientMembers() == null || kudosDTO.getRecipientMembers().isEmpty()) {
            throw new BadArgException("Kudos must contain at least one recipient");
        }

        Kudos kudos = new Kudos(kudosDTO);

        Kudos savedKudos = kudosRepository.save(kudos);

        for (MemberProfile recipient : kudosDTO.getRecipientMembers()) {
            KudosRecipient kudosRecipient = new KudosRecipient(savedKudos.getId(), recipient.getId());
            kudosRecipientServices.save(kudosRecipient);
        }

        sendNotification(savedKudos, NotificationType.creation);
        return savedKudos;
    }

    @Override
    public Kudos approve(Kudos kudos) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        UUID kudosId = kudos.getId();
        Kudos existingKudos = kudosRepository.findById(kudosId).orElseThrow(() ->
                new BadArgException(KUDOS_DOES_NOT_EXIST_MSG.formatted(kudosId)));

        if (existingKudos.getDateApproved() != null) {
            throw new BadArgException("Kudos with id %s has already been approved".formatted(kudosId));
        }

        existingKudos.setDateApproved(LocalDate.now());

        Kudos updated = kudosRepository.update(existingKudos);
        sendNotification(updated, NotificationType.approval);
        return updated;
    }

    @Override
    public KudosResponseDTO getById(UUID id) {

        Kudos kudos = kudosRepository.findById(id).orElseThrow(() ->
                new NotFoundException(KUDOS_DOES_NOT_EXIST_MSG.formatted(id)));

        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean isSender = currentUserId.equals(kudos.getSenderId());

        if (kudos.getDateApproved() == null) {
            // If not yet approved, only admins and the sender can access the kudos
            if (!currentUserServices.isAdmin() && !isSender) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            }
        } else {
            // If approved, admins, the sender, and the recipient can access the kudos

            List<KudosRecipient> recipients = kudosRecipientRepository.findByKudosId(id);
            boolean isRecipient = recipients
                    .stream()
                    .anyMatch(recipient -> recipient.getMemberId().equals(currentUserId));

            if (!currentUserServices.isAdmin() && !isSender && !isRecipient) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            }
        }

        return constructKudosResponseDTO(kudos);

    }

    @Override
    public void delete(UUID id) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        Kudos kudos = kudosRepository.findById(id).orElseThrow(() ->
                new NotFoundException(KUDOS_DOES_NOT_EXIST_MSG.formatted(id)));

        // Delete all KudosRecipients associated with this kudos
        List<KudosRecipient> recipients = kudosRecipientServices.getAllByKudosId(kudos.getId());
        kudosRecipientRepository.deleteAll(recipients);

        kudosRepository.deleteById(id);
    }

    @Override
    public List<KudosResponseDTO> findByValues(@Nullable UUID recipientId, @Nullable UUID senderId, @Nullable Boolean isPending) {
        if (isPending != null) {
            return findByPending(Boolean.TRUE.equals(isPending));
        } else if (recipientId != null) {
            return findAllToMember(recipientId);
        } else if (senderId != null) {
            return findAllFromMember(senderId);
        } else {
            if (!currentUserServices.isAdmin()) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            }

            return kudosRepository.findAll()
                    .stream()
                    .map(this::constructKudosResponseDTO)
                    .toList();
        }
    }

    public List<KudosResponseDTO> getRecent() {
        return kudosRepository.getRecentPublic()
                .stream()
                .map(this::constructKudosResponseDTO)
                .toList();
    }

    private List<KudosResponseDTO> findByPending(boolean isPending) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        List<Kudos> kudosList = new ArrayList<>();

        if (isPending) {
            kudosList.addAll(kudosRepository.getAllPending());
        } else {
            kudosList.addAll(kudosRepository.getAllApproved());
        }

        return kudosList
                .stream()
                .map(this::constructKudosResponseDTO)
                .toList();
    }


    private List<KudosResponseDTO> findAllToMember(UUID memberId) {
        boolean isAdmin = currentUserServices.isAdmin();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();

        if (!currentUserId.equals(memberId) && !isAdmin) {
            throw new PermissionException("You are not authorized to retrieve the kudos another user has received");
        }

        List<KudosRecipient> kudosRecipients = kudosRecipientRepository.findByMemberId(memberId);

        List<KudosResponseDTO> kudosList = new ArrayList<>();

        kudosRecipients.forEach(kudosRecipient -> {
            UUID kudosId = kudosRecipient.getKudosId();
            Kudos relatedKudos = kudosRepository.findById(kudosId).orElseThrow(() ->
                    new NotFoundException(KUDOS_DOES_NOT_EXIST_MSG.formatted(kudosId)));

            if (relatedKudos.getDateApproved() != null) {
                kudosList.add(constructKudosResponseDTO(relatedKudos));
            }
        });

        return kudosList;
    }

    private List<KudosResponseDTO> findAllFromMember(UUID senderId) {

        boolean isAdmin = currentUserServices.isAdmin();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();

        if (!currentUserId.equals(senderId) && !isAdmin) {
            throw new PermissionException("You are not authorized to retrieve the kudos another user has sent");
        }

        String senderIdString = Util.nullSafeUUIDToString(senderId);
        List<Kudos> kudosList = kudosRepository.search(senderIdString, true);

        return kudosList
                .stream()
                .map(this::constructKudosResponseDTO)
                .toList();
    }

    private KudosResponseDTO constructKudosResponseDTO(Kudos kudos) {
        KudosResponseDTO kudosResponseDTO = new KudosResponseDTO(
                kudos.getId(),
                kudos.getMessage(),
                kudos.getSenderId(),
                kudos.getDateCreated(),
                kudos.getDateApproved(),
                kudos.getPubliclyVisible()
        );

        List<KudosRecipient> recipients = kudosRecipientServices.getAllByKudosId(kudos.getId());

        if (recipients.isEmpty()) {
            throw new NotFoundException("Could not find recipients for kudos with id %s".formatted(kudos.getId()));
        }

        UUID teamId = kudos.getTeamId();
        if (teamId != null) {
            Team recipientTeam = teamRepository.findById(teamId).orElseThrow(() ->
                    new NotFoundException("Team %s does not exist".formatted(teamId)));
            kudosResponseDTO.setRecipientTeam(recipientTeam);
        }

        List<MemberProfile> members = recipients
                .stream()
                .map(recipient -> memberProfileRetrievalServices.getById(recipient.getMemberId()).orElseThrow(() ->
                        new NotFoundException("Member id %s of KudosRecipient %s does not exist".formatted(recipient.getMemberId(), recipient.getId()))
                ))
                .toList();

        kudosResponseDTO.setRecipientMembers(members);

        return kudosResponseDTO;
    }

    public static String getApprovalEmailContent(
                             CheckInsConfiguration checkInsConfiguration) {
        return "You have received new kudos!<br>\nClick " +
               checkInsConfiguration.getWebAddress() +
               "/kudos to view them.";
    }

    public static String getAdminEmailContent(
                             CheckInsConfiguration checkInsConfiguration) {
        return "There are new kudos to review.<br>\nClick " +
               checkInsConfiguration.getWebAddress() +
               "/admin/manage-kudos to review them.";
    }

    private void sendNotification(Kudos kudos, NotificationType notificationType) {
        try {
            // Only deal with public kudos here.
            if (kudos.getPubliclyVisible()) {
                List<KudosRecipient> recipients = kudosRecipientServices.getAllByKudosId(kudos.getId());
                if (!recipients.isEmpty()) {
                    // Send email to receivers of kudos that they have new kudos...
                    MemberProfile sender = memberProfileRetrievalServices.getById(kudos.getSenderId()).orElse(null);
                    if (sender == null) {
                        LOG.error("Unable to locate member {}", kudos.getSenderId());
                    } else {
                        String fromEmail = sender.getWorkEmail();
                        String fromName = sender.getFirstName() + " " + sender.getLastName();
                        String content = "";
                        List<String> recipientAddresses = new ArrayList<String>();
                        switch(notificationType) {
                        case NotificationType.approval:
                            content = getApprovalEmailContent(checkInsConfiguration);
                            for (KudosRecipient kudosRecipient : recipients) {
                                MemberProfile member = memberProfileRetrievalServices.getById(kudosRecipient.getMemberId()).orElse(null);
                                if (member == null) {
                                    LOG.error("Unable to locate member {}", kudosRecipient.getMemberId());
                                } else {
                                    recipientAddresses.add(member.getWorkEmail());
                                }
                            }
                            slackApprovedKudos(kudos);
                            break;
                        case NotificationType.creation:
                            content = getAdminEmailContent(checkInsConfiguration);
                            String adminRole = RoleType.ADMIN.toString();
                            for (MemberProfile profile : memberProfileServices.findAll()) {
                                Set<Role> userRoles = roleServices.findUserRoles(profile.getId());
                                for (Role role : userRoles) {
                                    if (role.getRole().equals(adminRole)) {
                                        recipientAddresses.add(profile.getWorkEmail());
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                        if (recipientAddresses.size() > 0) {
                            emailSender.sendEmail(fromName, fromEmail, KUDOS_EMAIL_SUBJECT, content, recipientAddresses.toArray(new String[recipientAddresses.size()]));
                        }
                    }
                }
            }
        } catch(Exception ex) {
          LOG.error("An unexpected error occurred while sending notifications: {}", ex.getLocalizedMessage(), ex);
        }
    }

    private void slackApprovedKudos(Kudos kudos) {
        KudosConverter converter = new KudosConverter(memberProfileServices,
                                                      kudosRecipientServices);

        String slackBlock = converter.toSlackBlock(kudos);
        HttpResponse httpResponse =
            slackPoster.post(converter.toSlackBlock(kudos));
        if (httpResponse.status() != HttpStatus.OK) {
            LOG.error("Unable to POST to Slack: " + httpResponse.reason());
        }
    }
}

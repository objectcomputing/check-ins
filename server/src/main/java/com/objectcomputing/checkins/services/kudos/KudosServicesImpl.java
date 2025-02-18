package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.notifications.social_media.SlackSender;
import com.objectcomputing.checkins.services.slack.SlackReader;
import com.objectcomputing.checkins.services.slack.kudos.BotSentKudosLocator;
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
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.transaction.annotation.Transactional;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

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
    private final SlackSender slackSender;
    private final KudosConverter converter;
    private final BotSentKudosLocator botSentKudosLocator;

    private enum NotificationType {
        creation, approval
    }

    @Inject
    private CheckInsConfiguration configuration;

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
                             SlackSender slackSender,
                             KudosConverter converter,
                             BotSentKudosLocator botSentKudosLocator
                      ) {
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
        this.slackSender = slackSender;
        this.converter = converter;
        this.botSentKudosLocator = botSentKudosLocator;
    }

    @Override
    @Transactional
    @RequiredPermission(Permission.CAN_CREATE_KUDOS)
    public Kudos save(KudosCreateDTO kudosDTO) {
        Kudos savedKudos = saveCommon(kudosDTO, true);
        sendNotification(savedKudos, NotificationType.creation);
        return savedKudos;
    }

    @Override
    @RequiredPermission(Permission.CAN_ADMINISTER_KUDOS)
    public Kudos approve(Kudos kudos) {
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
    public Kudos savePreapproved(KudosCreateDTO kudos) {
        Kudos savedKudos = saveCommon(kudos, false);
        savedKudos.setDateApproved(LocalDate.now());
        return kudosRepository.update(savedKudos);
    }

    @Override
    public Kudos update(KudosUpdateDTO kudos) {
        // Find the corresponding kudos and make sure we have permission.
        final UUID kudosId = kudos.getId();
        final Kudos existingKudos =
            kudosRepository.findById(kudosId).orElseThrow(() ->
            new BadArgException(KUDOS_DOES_NOT_EXIST_MSG.formatted(kudosId)));

        final MemberProfile currentUser = currentUserServices.getCurrentUser();
        if (!currentUser.getId().equals(existingKudos.getSenderId()) &&
            !hasAdministerKudosPermission()) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        if (kudos.getRecipientMembers() == null ||
            kudos.getRecipientMembers().isEmpty()) {
            throw new BadArgException(
                          "Kudos must contain at least one recipient");
        }

        // Begin modifying the existing kudos to reflect desired changes.
        final String originalMessage = existingKudos.getMessage();
        existingKudos.setMessage(kudos.getMessage());

        boolean existingPublic = existingKudos.getPubliclyVisible();
        boolean proposedPublic = kudos.getPubliclyVisible();
        boolean removePublicSlack = false;
        if (existingPublic && !proposedPublic) {
            removePublicSlack = true;
            existingKudos.setDateApproved(null);
        } else if ((!existingPublic && proposedPublic) ||
                   (proposedPublic &&
                    !originalMessage.equals(existingKudos.getMessage()))) {
            // Clear the date approved when going from private to public or
            // if public and the text changed, require approval again.
            existingKudos.setDateApproved(null);
        }

        existingKudos.setPubliclyVisible(kudos.getPubliclyVisible());

        List<KudosRecipient> recipients = kudosRecipientRepository
                                              .findByKudosId(kudosId);
        Set<UUID> proposed = kudos.getRecipientMembers()
                                           .stream()
                                           .map(r -> r.getId())
                                           .collect(Collectors.toSet());
        boolean different = (recipients.size() != proposed.size());
        if (!different) {
            Set<UUID> existing = recipients.stream()
                                           .map(r -> r.getMemberId())
                                           .collect(Collectors.toSet());
            different = !existing.equals(proposed);
        }

        // First, update the Kudos so that we only change recipients if they
        // are different and we were able to update the Kudos.
        final Kudos updated = kudosRepository.update(existingKudos);

        // Change recipients, if necessary.
        if (different) {
            updateRecipients(updated, recipients, proposed);
        }

        // The kudos has been updated.  Send notification to admin, if going
        // from private to public.
        if (!existingPublic && proposedPublic) {
            sendNotification(updated, NotificationType.creation);
        }

        if (removePublicSlack) {
            // Search for and remove the Slack Kudos that the Check-Ins
            // Integration posted.
            removeSlackMessage(existingKudos);
        }

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
            if (!hasAdministerKudosPermission() && !isSender) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            }
        } else {
            // If approved, admins, the sender, and the recipient can access the kudos

            List<KudosRecipient> recipients = kudosRecipientRepository.findByKudosId(id);
            boolean isRecipient = recipients
                    .stream()
                    .anyMatch(recipient -> recipient.getMemberId().equals(currentUserId));

            if (!hasAdministerKudosPermission() && !isSender && !isRecipient) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            }
        }

        return constructKudosResponseDTO(kudos);

    }

    @Override
    public void delete(UUID id) {
        Kudos kudos = kudosRepository.findById(id).orElseThrow(() ->
                new NotFoundException(KUDOS_DOES_NOT_EXIST_MSG.formatted(id)));

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        if (!currentUser.getId().equals(kudos.getSenderId()) &&
            !hasAdministerKudosPermission()) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        // Delete all KudosRecipients associated with this kudos
        List<KudosRecipient> recipients = kudosRecipientServices.getAllByKudosId(kudos.getId());
        kudosRecipientRepository.deleteAll(recipients);

        kudosRepository.deleteById(id);

        if (kudos.getPubliclyVisible()) {
            // Search for and remove the Slack Kudos that the Check-Ins
            // Integration posted.
            removeSlackMessage(kudos);
        }
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
            if (!hasAdministerKudosPermission()) {
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
        if (!hasAdministerKudosPermission()) {
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
        UUID currentUserId = currentUserServices.getCurrentUser().getId();

        if (!currentUserId.equals(memberId) &&
            !hasAdministerKudosPermission()) {
            throw new PermissionException("You are not authorized to retrieve the kudos another user has received");
        }

        List<KudosRecipient> kudosRecipients = kudosRecipientRepository.findByMemberId(memberId);

        List<KudosResponseDTO> kudosList = new ArrayList<>();

        kudosRecipients.forEach(kudosRecipient -> {
            UUID kudosId = kudosRecipient.getKudosId();
            Kudos relatedKudos = kudosRepository.findById(kudosId).orElseThrow(() ->
                    new NotFoundException(KUDOS_DOES_NOT_EXIST_MSG.formatted(kudosId)));

            if (!relatedKudos.getPubliclyVisible() ||
                relatedKudos.getDateApproved() != null) {
                kudosList.add(constructKudosResponseDTO(relatedKudos));
            }
        });

        return kudosList;
    }

    private List<KudosResponseDTO> findAllFromMember(UUID senderId) {

        UUID currentUserId = currentUserServices.getCurrentUser().getId();

        if (!currentUserId.equals(senderId) &&
            !hasAdministerKudosPermission()) {
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
        slackSender.send(configuration.getApplication()
                                      .getSlack().getKudosChannel(),
                         converter.toSlackBlock(kudos));
    }

    private boolean hasAdministerKudosPermission() {
        return currentUserServices.hasPermission(Permission.CAN_ADMINISTER_KUDOS);
    }

    private Kudos saveCommon(KudosCreateDTO kudosDTO, boolean verifyAndNotify) {
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

        Kudos savedKudos = kudosRepository.save(new Kudos(kudosDTO));

        for (MemberProfile recipient : kudosDTO.getRecipientMembers()) {
            KudosRecipient kudosRecipient = new KudosRecipient(savedKudos.getId(), recipient.getId());
            if (verifyAndNotify) {
                // Going through the service verifies the sender and recipient
                // and sends email notification after saving.
                kudosRecipientServices.save(kudosRecipient);
            } else {
                // This does none of that and just stores it in the database.
                kudosRecipientRepository.save(kudosRecipient);
            }
        }

        return savedKudos;
    }

    private void updateRecipients(Kudos updated,
                                  List<KudosRecipient> recipients,
                                  Set<UUID> proposed) {
        // Add the new recipients.
        Set<UUID> existing = recipients.stream()
                                       .map(r -> r.getMemberId())
                                       .collect(Collectors.toSet());
        for (UUID id : proposed) {
            if (!existing.contains(id)) {
                kudosRecipientServices.save(
                    new KudosRecipient(updated.getId(), id));
            }
        }

        // Remove any that are no longer designated as recipients.
        for (KudosRecipient recipient : recipients) {
            if (!proposed.contains(recipient.getMemberId())) {
                kudosRecipientRepository.delete(recipient);
            }
        }
    }

    private void removeSlackMessage(Kudos kudos) {
        String ts = botSentKudosLocator.find(kudos);
        if (ts != null) {
            slackSender.delete(configuration.getApplication()
                                            .getSlack().getKudosChannel(),
                               ts);
        }
    }
}

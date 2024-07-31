package com.objectcomputing.checkins.services.kudos.kudos_recipient;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.kudos.Kudos;
import com.objectcomputing.checkins.services.kudos.KudosRepository;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
public class KudosRecipientServicesImpl implements KudosRecipientServices {
    public static final String KUDOS_EMAIL_SUBJECT = "Kudos";
    private static final Logger LOG = LoggerFactory.getLogger(KudosRecipientServicesImpl.class);
    private final KudosRecipientRepository kudosRecipientRepository;
    private final CurrentUserServices currentUserServices;
    private final KudosRepository kudosRepository;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final MemberProfileServices memberProfileServices;
    private final RoleServices roleServices;
    private final EmailSender emailSender;
    private final CheckInsConfiguration checkInsConfiguration;

    public KudosRecipientServicesImpl(KudosRecipientRepository kudosRecipientRepository,
                                      CurrentUserServices currentUserServices,
                                      KudosRepository kudosRepository,
                                      MemberProfileRetrievalServices memberProfileRetrievalServices,
                                      MemberProfileServices memberProfileServices,
                                      RoleServices roleServices,
                                      @Named(MailJetFactory.HTML_FORMAT) EmailSender emailSender,
                                      CheckInsConfiguration checkInsConfiguration) {
        this.kudosRecipientRepository = kudosRecipientRepository;
        this.currentUserServices = currentUserServices;
        this.kudosRepository = kudosRepository;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
        this.memberProfileServices = memberProfileServices;
        this.roleServices = roleServices;
        this.emailSender = emailSender;
        this.checkInsConfiguration = checkInsConfiguration;
    }

    @Override
    public KudosRecipient save(KudosRecipient kudosRecipient) {

        if (kudosRecipient.getId() != null) {
            throw new BadArgException("KudosRecipient id must be null");
        }

        Kudos kudos = kudosRepository.findById(kudosRecipient.getKudosId()).orElseThrow(() ->
                new NotFoundException("No kudos with id %s"));

        boolean isKudosCreator = currentUserServices.getCurrentUser().getId().equals(kudos.getSenderId());
        boolean isAdmin = currentUserServices.isAdmin();
        if (!isAdmin && !isKudosCreator) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        MemberProfile member = memberProfileRetrievalServices.getById(kudosRecipient.getMemberId()).orElseThrow(() ->
                new BadArgException("Cannot save KudosRecipient: member %s does not exist"));

        if (member.getTerminationDate() != null && member.getTerminationDate().isBefore(LocalDate.now())) {
            throw new BadArgException("Cannot save KudosRecipient for terminated member %s");
        }

        KudosRecipient recipient = kudosRecipientRepository.save(kudosRecipient);
        sendNotification(kudos, member);
        return recipient;
    }

    @Override
    public List<KudosRecipient> getAllByKudosId(UUID kudosId) {
        return kudosRecipientRepository.findByKudosId(kudosId);
    }

    public static String getAdminEmailContent(
                             CheckInsConfiguration checkInsConfiguration) {
        return "There are new kudos to review.<br>\nClick " +
               checkInsConfiguration.getWebAddress() +
               "/admin/manage-kudos to review them.";
    }

    private void sendNotification(Kudos kudos, MemberProfile member) {
        try {
            MemberProfile sender = memberProfileRetrievalServices.getById(kudos.getSenderId()).orElse(null);
            if (sender == null) {
                LOG.error(String.format("Unable to locate member %s.", kudos.getSenderId().toString()));
            } else {
                String fromEmail = sender.getWorkEmail();
                String fromName = sender.getFirstName() + " " + sender.getLastName();
                List<String> recipients = new ArrayList<String>();
                String content = "";
                if (kudos.getPubliclyVisible()) {
                    // Build a list of admin email addresses.
                    String adminRole = RoleType.ADMIN.toString();
                    for (MemberProfile profile : memberProfileServices.findAll()) {
                        Set<Role> userRoles = roleServices.findUserRoles(profile.getId());
                        for (Role role : userRoles) {
                            if (role.getRole().equals(adminRole)) {
                                recipients.add(profile.getWorkEmail());
                                break;
                            }
                        }
                    }
                    content = getAdminEmailContent(checkInsConfiguration);
                } else {
                    // This is a private kudos, so notify the receiver directly.
                    recipients.add(member.getWorkEmail());
                    content = kudos.getMessage();
                }

                emailSender.sendEmail(fromName, fromEmail, KUDOS_EMAIL_SUBJECT, content, recipients.toArray(new String[recipients.size()]));
            }
        } catch(Exception ex) {
          LOG.error("An unexpected error occurred while sending notifications: {}", ex.getLocalizedMessage(), ex);
        }
    }
}

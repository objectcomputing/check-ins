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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
public class KudosRecipientServicesImpl implements KudosRecipientServices {

    private final KudosRecipientRepository kudosRecipientRepository;
    private final CurrentUserServices currentUserServices;
    private final KudosRepository kudosRepository;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final MemberProfileServices memberProfileServices;
    private final RoleServices roleServices;
    private final EmailSender emailSender;
    private final String webAddress;

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
        this.webAddress = checkInsConfiguration.getWebAddress();
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

    private void sendNotification(Kudos kudos, MemberProfile member) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        String fromEmail = currentUser.getWorkEmail();
        String fromName = currentUser.getFirstName() + " " + currentUser.getLastName();
        String subject = "Kudos";
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
            content = "There are new kudos to review.<br>\nClick " +
                      webAddress + "/admin/manage-kudos to review them.";
        } else {
            // This is a private kudos, so notify the receiver directly.
            recipients.add(member.getWorkEmail());
            content = kudos.getMessage();
        }

        for (String recipient : recipients) {
            emailSender.sendEmail(fromName, fromEmail, subject, content, recipient);
        }
    }
}

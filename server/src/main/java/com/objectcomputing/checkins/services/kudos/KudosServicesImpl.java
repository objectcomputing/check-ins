package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipient;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipientRepository;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipientServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.TeamRepository;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class KudosServicesImpl implements KudosServices {

    private final KudosRepository kudosRepository;
    private final KudosRecipientServices kudosRecipientServices;
    private final KudosRecipientRepository kudosRecipientRepository;
    private final TeamRepository teamRepository;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final CurrentUserServices currentUserServices;

    public static final Logger LOG = LoggerFactory.getLogger(KudosServicesImpl.class);

    public KudosServicesImpl(KudosRepository kudosRepository,
                             KudosRecipientServices kudosRecipientServices,
                             KudosRecipientRepository kudosRecipientRepository,
                             TeamRepository teamRepository,
                             MemberProfileRetrievalServices memberProfileRetrievalServices,
                             CurrentUserServices currentUserServices) {
        this.kudosRepository = kudosRepository;
        this.kudosRecipientServices = kudosRecipientServices;
        this.kudosRecipientRepository = kudosRecipientRepository;
        this.teamRepository = teamRepository;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public Kudos save(KudosCreateDTO kudosDTO) {

        memberProfileRetrievalServices.getById(kudosDTO.getSenderId()).orElseThrow(() -> {
            throw new BadArgException("Kudos sender %s does not exist");
        });

        if (kudosDTO.getTeamId() != null) {
            teamRepository.findById(kudosDTO.getTeamId()).orElseThrow(() -> {
               throw new BadArgException("Team %s does not exist");
            });
        }

        if (kudosDTO.getMessage() == null || kudosDTO.getMessage().isBlank()) {
            throw new BadArgException("Kudos message cannot be blank");
        }

        if (kudosDTO.getRecipientMembers() == null || kudosDTO.getRecipientMembers().isEmpty()) {
            throw new BadArgException("Kudos must contain at least one recipient");
        }

        Kudos kudos = new Kudos(kudosDTO);

        Kudos savedKudos = kudosRepository.save(kudos);
        List<KudosRecipient> savedRecipients = new ArrayList<>();

        kudosDTO.getRecipientMembers().forEach(recipient -> {
            KudosRecipient kudosRecipient = new KudosRecipient(savedKudos.getId(), recipient.getId());
            try {
                kudosRecipientServices.save(kudosRecipient);
                savedRecipients.add(kudosRecipient);
            } catch (RuntimeException e) {
                // If saving KudosRecipient fails, abort save
                savedRecipients.forEach(savedRecipient ->
                        kudosRecipientRepository.delete(kudosRecipient)
                );
                kudosRepository.delete(savedKudos);
                throw e;
            }
        });

        return savedKudos;
    }

    @Override
    public Kudos update(KudosUpdateDTO kudos) {

        Kudos existingKudos = kudosRepository.findById(kudos.getKudosId()).orElseThrow(() -> {
            throw new BadArgException("Kudos with id %s does not exist");
        });

        if(kudos.getApproved() != null && kudos.getApproved() == Boolean.TRUE) {
            if (!currentUserServices.isAdmin()) {
                throw new PermissionException("You are not authorized to perform this operation");
            }

            if (existingKudos.getDateApproved() != null) {
                throw new BadArgException("Kudos with id %s has already been approved");
            }

            existingKudos.setDateApproved(LocalDate.now());
        } else {
            if(!currentUserServices.getCurrentUser().getId().equals(existingKudos.getSenderId())) {
                throw new PermissionException("You are not authorized to perform this operation");
            }

            existingKudos.setMessage(kudos.getMessage());
            existingKudos.setDateApproved(null);
        }

        return kudosRepository.update(existingKudos);
    }

    @Override
    public KudosResponseDTO getById(UUID id) {

        Kudos kudos = kudosRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Kudos with id %s does not exist");
        });

        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean isSender = currentUserId.equals(kudos.getSenderId());

        if (kudos.getDateApproved() == null) {
            // If not yet approved, only admins and the sender can access the kudos
            if (!currentUserServices.isAdmin() && !isSender) {
                throw new PermissionException("You are not authorized to perform this operation");
            }
        } else {
            // If approved, admins, the sender, and the recipient can access the kudos

            List<KudosRecipient> recipients = kudosRecipientRepository.findByKudosId(id);
            boolean isRecipient = recipients
                    .stream()
                    .anyMatch(recipient -> recipient.getMemberId().equals(currentUserId));

            if (!currentUserServices.isAdmin() && !isSender && !isRecipient) {
                throw new PermissionException("You are not authorized to perform this operation");
            }
        }

        return constructKudosResponseDTO(kudos);

    }

    @Override
    public boolean delete(UUID id) {
        Kudos kudos = kudosRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Kudos with id %s does not exist");
        });

        if (!currentUserServices.isAdmin() && !currentUserServices.getCurrentUser().getId().equals(kudos.getSenderId())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        // Delete all KudosRecipients associated with this kudos
        List<KudosRecipient> recipients = kudosRecipientServices.getAllByKudosId(kudos.getId());
        if(recipients != null && recipients.size() > 0) {
            kudosRecipientRepository.deleteAll(recipients);
        }

        kudosRepository.deleteById(id);
        return true;
    }

    @Override
    public List<KudosResponseDTO> findByValues(@Nullable UUID recipientId, @Nullable UUID senderId, @Nullable Boolean isPending, @Nullable Boolean Public) {

        if (isPending != null) {
            return findByPending(isPending);
        } else if (recipientId != null) {
            return findAllToMember(recipientId);
        } else if (senderId != null) {
            return findAllFromMember(senderId);
        } else {
            if (!currentUserServices.isAdmin()) {
                throw new PermissionException("You are not authorized to do this operation");
            }

            List<KudosResponseDTO> kudosList = new ArrayList<>();
            kudosRepository.findAll().forEach(kudos -> kudosList.add(constructKudosResponseDTO(kudos)));
            return kudosList;
        }
    }

    private List<KudosResponseDTO> findByPending(Boolean isPending) {
        boolean isAdmin = currentUserServices.isAdmin();

        // By default, find all non-pending kudos
        if (isPending == null) {
            isPending = false;
        }

        if (!isAdmin) {
            throw new PermissionException("You are not authorized to do this operation");
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
                .collect(Collectors.toList());
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
            Kudos relatedKudos = kudosRepository.findById(kudosRecipient.getKudosId()).orElseThrow(() -> {
                throw new NotFoundException("Kudos with id %s does not exist");
            });

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
                .collect(Collectors.toList());
    }

    private KudosResponseDTO constructKudosResponseDTO(Kudos kudos) {

        KudosResponseDTO kudosResponseDTO = new KudosResponseDTO();
        kudosResponseDTO.setId(kudos.getId());
        kudosResponseDTO.setMessage(kudos.getMessage());
        kudosResponseDTO.setSenderId(kudos.getSenderId());
        kudosResponseDTO.setDateCreated(kudos.getDateCreated());
        kudosResponseDTO.setDateApproved(kudos.getDateApproved());
        kudosResponseDTO.setPubliclyVisible(kudos.getPubliclyVisible());

        List<KudosRecipient> recipients = kudosRecipientServices.getAllByKudosId(kudos.getId());

        if (kudos.getTeamId() != null) {
            Team recipientTeam = teamRepository.findById(kudos.getTeamId()).orElseThrow(() -> {
                throw new NotFoundException(String.format("Team %s does not exist", kudos.getTeamId()));
            });
            kudosResponseDTO.setRecipientTeam(recipientTeam);
        }

        List<MemberProfile> members = recipients
                .stream()
                .map(recipient -> memberProfileRetrievalServices.getById(recipient.getMemberId()).orElseThrow(() -> {
                    throw new NotFoundException(String.format("Member id %s of KudosRecipient %s does not exist", recipient.getMemberId(), recipient.getId()));
                }))
                .collect(Collectors.toList());

        kudosResponseDTO.setRecipientMembers(members);

        return kudosResponseDTO;
    }

}

package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipientRepository;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipient;
import com.objectcomputing.checkins.services.kudos.KudosRepository;
import com.objectcomputing.checkins.services.kudos.Kudos;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.nio.ByteBuffer;

public class ReportDataCollation {
    private UUID memberId;
    private LocalDate startDate;
    private LocalDate endDate;
    private CompensationHistory compensationHistory;
    private KudosRepository kudosRepository;
    private KudosRecipientRepository kudosRecipientRepository;
    private MemberProfileRepository memberProfileRepository;
    private ReportDataUploadServices reportDataUploadServices;

    public ReportDataCollation(
                          UUID memberId,
                          LocalDate startDate, LocalDate endDate,
                          KudosRepository kudosRepository,
                          KudosRecipientRepository kudosRecipientRepository,
                          MemberProfileRepository memberProfileRepository,
                          ReportDataUploadServices reportDataUploadServices) {
        this.memberId = memberId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.compensationHistory = new CompensationHistory();
        this.kudosRepository = kudosRepository;
        this.kudosRecipientRepository = kudosRecipientRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.reportDataUploadServices = reportDataUploadServices;
    }

    LocalDate getStartDate() {
        return startDate;
    }

    LocalDate getEndDate() {
        return endDate;
    }

    /// Get the kudos given to the member during the start and end date range.
    List<Kudos> getKudos() {
        List<KudosRecipient> recipients = kudosRecipientRepository.findByMemberId(memberId);
        List<Kudos> kudosList = new ArrayList<Kudos>();
        for (KudosRecipient recipient : recipients) {
            Kudos kudos = kudosRepository.findById(recipient.getId())
                                         .orElse(null);
            if (kudos != null) {
                LocalDate created = kudos.getDateCreated();
                if ((created.isEqual(startDate) ||
                     created.isAfter(startDate)) && created.isBefore(endDate)) {
                    kudosList.add(kudos);
                }
            }
        }
        return kudosList;
    }

    /// Get the member name, title, and start date among others.
    MemberProfile getProfile() {
        return memberProfileRepository.findById(memberId).orElseThrow(() ->
            new NotFoundException("Member not found")
        );
    }

    List<CompensationHistory.Compensation> getCompensationHistory() {
        List<CompensationHistory.Compensation> history = compensationHistory.getHistory();
        if (history.isEmpty()) {
            try {
                ByteBuffer buffer = reportDataUploadServices.get(memberId,
                    ReportDataUploadServices.DataType.compensationHistory);
                compensationHistory.load(buffer);
                history = compensationHistory.getHistory();
            } catch(Exception ex) {
            }
        }
        return history;
    }
}

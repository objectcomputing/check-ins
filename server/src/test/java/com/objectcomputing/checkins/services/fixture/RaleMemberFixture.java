package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.guild.GuildCreateDTO;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.rale.Rale;
import com.objectcomputing.checkins.services.rale.RaleCreateDTO;
import com.objectcomputing.checkins.services.rale.RaleUpdateDTO;
import com.objectcomputing.checkins.services.rale.member.RaleMember;
import com.objectcomputing.checkins.services.rale.member.RaleMemberCreateDTO;
import com.objectcomputing.checkins.services.rale.member.RaleMemberResponseDTO;
import com.objectcomputing.checkins.services.rale.member.RaleMemberUpdateDTO;


public interface RaleMemberFixture extends RepositoryFixture{
    default RaleMember createDefaultRaleMember(Rale raleEntity, MemberProfile memberProfile) {
        return getRaleMemberRepository().save(new RaleMember(null, raleEntity.getId(), memberProfile.getId(), false));
    }

    default RaleMember createLeadRaleMember(Rale raleEntity, MemberProfile memberProfile) {
        return getRaleMemberRepository().save(new RaleMember(null, raleEntity.getId(), memberProfile.getId(), true));
    }

    default RaleCreateDTO.RaleMemberCreateDTO createDefaultRaleMemberDto(MemberProfile memberProfile, Boolean lead) {
        return new RaleCreateDTO.RaleMemberCreateDTO(memberProfile.getId(), lead);
    }

    default RaleUpdateDTO.RaleMemberUpdateDTO updateDefaultRaleMemberDto(Rale entity, MemberProfile memberProfile, Boolean lead) {
        return new RaleUpdateDTO.RaleMemberUpdateDTO(null, entity.getId(), memberProfile.getId(), lead);
    }

    default RaleMemberCreateDTO createDefaultRaleMemberDto(Rale raleEntity, MemberProfile memberProfile, Boolean lead) {
        return new RaleMemberCreateDTO(raleEntity.getId(), memberProfile.getId(), true);
    }

    default RaleMemberResponseDTO dtoFromEntity(RaleMember memberEntity, MemberProfile memberProfile) {
        return new RaleMemberResponseDTO(memberEntity.getId(), memberProfile.getFirstName(), memberProfile.getLastName(),
                memberProfile.getId(), memberEntity.isLead());
    }
}

package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.guild.Guild;
import com.objectcomputing.checkins.services.guild.GuildCreateDTO;
import com.objectcomputing.checkins.services.guild.GuildUpdateDTO;
import com.objectcomputing.checkins.services.guild.member.GuildMember;
import com.objectcomputing.checkins.services.guild.member.GuildMemberResponseDTO;
import com.objectcomputing.checkins.services.guild.member.GuildMemberUpdateDTO;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

public interface GuildMemberFixture extends RepositoryFixture{
    default GuildMember createDefaultGuildMember(Guild guildEntity, MemberProfile memberProfile) {
        return getGuildMemberRepository().save(new GuildMember(null, guildEntity.getId(), memberProfile.getId(), false));
    }

    default GuildMember createLeadGuildMember(Guild guildEntity, MemberProfile memberProfile) {
        return getGuildMemberRepository().save(new GuildMember(null, guildEntity.getId(), memberProfile.getId(), true));
    }

    default GuildMemberResponseDTO createDefaultGuildMemberDto(Guild guildEntity, MemberProfile memberProfile) {
        return dtoFromEntity(createDefaultGuildMember(guildEntity, memberProfile), memberProfile);
    }

    default GuildUpdateDTO.GuildMemberUpdateDTO guildMemberUpdateDTOFromNonExistingMember(MemberProfile memberProfile, Boolean lead){
        return new GuildUpdateDTO.GuildMemberUpdateDTO(null, memberProfile.getId(), lead);
    }

    default GuildCreateDTO.GuildMemberCreateDTO createDefaultGuildMemberDto(MemberProfile memberProfile, Boolean lead) {
        return new GuildCreateDTO.GuildMemberCreateDTO(memberProfile.getId(), lead);
    }

    default GuildMemberResponseDTO dtoFromEntity(GuildMember memberEntity, MemberProfile memberProfile) {
        return new GuildMemberResponseDTO(memberEntity.getId(), memberProfile.getFirstName(), memberProfile.getLastName(),
                memberProfile.getId(), memberEntity.isLead());
    }

    default GuildUpdateDTO.GuildMemberUpdateDTO updateDefaultGuildMemberDto(GuildMember guildMember, boolean lead ){
        return new GuildUpdateDTO.GuildMemberUpdateDTO(guildMember.getId(),guildMember.getMemberId(),lead);
    }
}

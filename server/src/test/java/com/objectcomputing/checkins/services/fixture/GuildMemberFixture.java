package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.guild.Guild;
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

    default GuildMemberResponseDTO createDefaultGuildMemberDto(MemberProfile memberProfile, Boolean lead) {
        return new GuildMemberResponseDTO(null, memberProfile.getFirstName(), memberProfile.getLastName(),
                memberProfile.getId(), lead);
    }

    default GuildMemberResponseDTO dtoFromEntity(GuildMember memberEntity, MemberProfile memberProfile) {
        return new GuildMemberResponseDTO(memberEntity.getId(), memberProfile.getFirstName(), memberProfile.getLastName(),
                memberProfile.getId(), memberEntity.isLead());
    }

    default GuildMemberUpdateDTO updateDefaultGuildMemberDto(Guild guildEntity, MemberProfile memberProfile, boolean leead){
        return new GuildMemberUpdateDTO(null,guildEntity.getId(),memberProfile.getId(),true);
    }
}

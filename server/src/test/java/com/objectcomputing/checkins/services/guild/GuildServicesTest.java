package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.services.guild.member.GuildMemberRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.inject.Inject;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GuildServicesTest {

    @Mock
    private GuildRepository guildRepository;

    @Mock
    private GuildMemberRepository guildMemberRepository;

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @InjectMocks
    private GuildServices services;

    @Test
    public void testRead() {
       
    }
}

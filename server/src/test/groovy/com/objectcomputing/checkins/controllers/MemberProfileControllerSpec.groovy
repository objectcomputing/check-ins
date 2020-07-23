package com.objectcomputing.checkins.controllers

import com.objectcomputing.checkins.EmbeddedServerSpecification
import com.objectcomputing.checkins.fixtures.MemberProfileFixture
import com.objectcomputing.checkins.services.memberprofile.MemberProfile
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.uri.UriBuilder
import io.micronaut.http.HttpStatus
import spock.lang.Subject

class MemberProfileControllerSpec extends EmbeddedServerSpecification implements MemberProfileFixture{

    @Subject
    MemberProfileRepository memberProfileRepository = applicationContext.getBean(MemberProfileRepository)

    void 'find a member profile by name'() {
        given: 'an existing member profile'
        MemberProfile memberProfile = saveMemberProfile()

        String testUser = "testName"

        and: 'an http request'
        URI uri = UriBuilder.of('/member-profile/?')
                .queryParam('name', memberProfile.name)
                .build()

        HttpRequest request = HttpRequest.GET(uri)

        when:
        HttpResponse<MemberProfile> response = client.exchange(request, MemberProfile)

        then:
        response.status() == HttpStatus.OK

        when:
        MemberProfile teamMember = response.body()

        then:
        teamMember.getName() == memberProfile.name
        teamMember.getPdlId() == memberProfile.pdlId

        cleanup:
        memberProfileRepository.deleteAll()

    }

}

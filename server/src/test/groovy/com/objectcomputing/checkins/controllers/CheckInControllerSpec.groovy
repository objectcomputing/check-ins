package com.objectcomputing.checkins.controllers

import com.objectcomputing.checkins.services.checkins.CheckIn
import com.objectcomputing.checkins.services.checkins.CheckInRepository
import com.objectcomputing.checkins.EmbeddedServerSpecification
import com.objectcomputing.checkins.fixtures.CheckInFixture
import com.objectcomputing.checkins.services.memberprofile.MemberProfile
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.uri.UriBuilder
import spock.lang.Subject

import java.time.LocalDate

class CheckInControllerSpec extends EmbeddedServerSpecification implements CheckInFixture {

    @Subject
    CheckInRepository checkInRepository = applicationContext.getBean(CheckInRepository)

    void 'find a checkIn name'() {
        UUID id = UUID.randomUUID()
        LocalDate testDate = LocalDate.now()

        given: 'an existing checkIn'
        // the fkey in member profile is necessary to have a passing test for check-in,
        // so an insert is made to member-profile before inserting into check-in with the uuid from member-profile
        MemberProfile memberProfile = new MemberProfile("testName", "testRole", null, "testLocation",
                "testEmail", "testInsperityId", testDate, "testBio")
        memberProfileRepository.save(memberProfile)

        CheckIn checkIn = new CheckIn(memberProfile.uuid, id, testDate)
        checkInRepository.save(checkIn)


        and: 'an http request'
        URI uri = UriBuilder.of('/check-in/?')
            .queryParam('teamMemberId', checkIn.getTeamMemberId())
            .build()

        HttpRequest request = HttpRequest.GET(uri)

        when:
        HttpResponse<CheckIn> response = client.exchange(request, CheckIn)

        then:
        response.status() == HttpStatus.OK

        when:
        CheckIn teamMember = response.body()

        then:
        teamMember.getTeamMemberId() == checkIn.teamMemberId
        teamMember.getPdlId() == checkIn.pdlId

        cleanup:
        checkInRepository.deleteAll()
        memberProfileRepository.deleteAll()
        skillRepository.deleteAll()

    }

    void 'try to find a user that does not exist returns 404'() {
        given: 'an http request'
        HttpRequest request = HttpRequest.GET("/bar?order=foo")

        when:
        client.exchange(request, CheckIn)

        then:
        HttpClientResponseException e = thrown(HttpClientResponseException)
        e.status == HttpStatus.NOT_FOUND

        cleanup:
        checkInRepository.deleteAll()

    }

    void 'try to find a member id that does not exist returns empty body'() {
        UUID id = UUID.randomUUID()

        given: 'an http request'
        URI uri = UriBuilder.of('/check-in/?')
                .queryParam('teamMemberId', id)
                .build()

        HttpRequest request = HttpRequest.GET(uri)

        when:
        HttpResponse<?> response = client.exchange(request)

        then:
        response.status() == HttpStatus.OK
        response.getContentLength() == 2

        cleanup:
        checkInRepository.deleteAll()

    }

}

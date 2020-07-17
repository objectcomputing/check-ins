package com.objectcomputing.checkins.controllers

import com.objectcomputing.checkins.services.checkins.CheckIn
import com.objectcomputing.checkins.services.checkins.CheckInRepository
import com.objectcomputing.checkins.EmbeddedServerSpecification
import com.objectcomputing.checkins.fixtures.CheckInFixture
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.uri.UriBuilder
import spock.lang.Subject

class CheckInControllerSpec extends EmbeddedServerSpecification implements CheckInFixture {

    @Subject
    CheckInRepository checkInRepository = applicationContext.getBean(CheckInRepository)

    void 'find a checkIn name'() {
        given: 'an existing checkIn'
        CheckIn checkIn = saveCheckIn()

        and: 'an http request'
        URI uri = UriBuilder.of('/check-in/?')
            .queryParam('teamMemberId', checkIn.teamMemberId)
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

    void 'try to find a member by target year and quarter that does not exist returns empty body'() {
        String testTargetYear = "2019"
        String testTargetQuarter = "Q4"

        given: 'an http request'
        URI uri = UriBuilder.of('/check-in/?')
                .queryParam('targetYear', testTargetYear)
                .queryParam('targetQtr', testTargetQuarter)
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

    void 'try to find a member by pdl id that does not exist returns empty body'() {
        UUID id = UUID.randomUUID()

        given: 'an http request'
        URI uri = UriBuilder.of('/check-in/?')
                .queryParam('pdlId', id)
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

//    @Override
//    CheckInRepository getCheckInRepository() {
//        return null
//    }
}

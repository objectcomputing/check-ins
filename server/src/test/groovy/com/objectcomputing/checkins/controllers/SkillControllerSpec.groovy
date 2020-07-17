package com.objectcomputing.checkins.controllers

import com.objectcomputing.checkins.EmbeddedServerSpecification
import com.objectcomputing.checkins.fixtures.SkillFixture
import com.objectcomputing.checkins.services.skills.Skill
import com.objectcomputing.checkins.services.skills.SkillRepository
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.uri.UriBuilder
import io.micronaut.http.HttpStatus
import spock.lang.Subject

class SkillControllerSpec extends EmbeddedServerSpecification implements SkillFixture {

    @Subject
    SkillRepository skillRepository = applicationContext.getBean(SkillRepository)

    void 'find a member profile by name'() {
        given: 'an existing member profile'
        Skill skill = saveSkill()

        and: 'an http request'
        URI uri = UriBuilder.of('/skill/?')
                .queryParam('name', skill.name)
                .build()

        HttpRequest request = HttpRequest.GET(uri)

        when:
        HttpResponse<Skill> response = client.exchange(request, Skill)

        then:
        response.status() == HttpStatus.OK

        when:
        Skill skillResponse = response.body()

        then:
        skillResponse.getName() == skill.name
        skillResponse.isPending() == skill.isPending()

        cleanup:
        skillRepository.deleteAll()

    }
}

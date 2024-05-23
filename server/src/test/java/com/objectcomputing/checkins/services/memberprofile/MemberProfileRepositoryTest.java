package com.objectcomputing.checkins.services.memberprofile;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class MemberProfileRepositoryTest {

    @Inject
    MemberProfileRepository memberProfileRepository;

    MemberProfile ceo;
    MemberProfile lead1;
    MemberProfile lead1sub1;
    MemberProfile lead1sub2;
    MemberProfile lead2;
    MemberProfile lead2sub1;
    MemberProfile lead2sub1sub1;
    MemberProfile lead2sub1sub2fired;
    MemberProfile wildcard;

    //                         ┌─────────┐
    //                ┌────────┤   CEO   ├────┐
    //                │        └─────────┘    │
    //                │                       │                ┌──────────┐
    //            ┌───▼───┐               ┌───▼───┐            │ Wildcard │
    //      ┌─────┤ Lead1 ├────┐          │ Lead2 │            └──────────┘
    //      │     └───────┘    │          └───┬───┘
    //      │                  │              │
    // ┌────▼──────┐    ┌──────▼────┐   ┌─────▼─────┐
    // │ Lead1Sub1 │    │ Lead1Sub2 │ ┌─┤ Lead2Sub1 ├───┐
    // └───────────┘    └───────────┘ │ └───────────┘   │
    //                                │                 │
    //                       ┌────────▼──────┐   ┌──────▼─────────────┐
    //                       │ Lead2Sub1Sub1 │   │ Lead2Sub1Sub2Fired │
    //                       └───────────────┘   └────────────────────┘
    @BeforeEach
    void setupHierarchy() {
        ceo = memberWithoutBoss("ceo");
        lead1 = memberWithSupervisor("lead1", ceo);
        lead1sub1 = memberWithSupervisor("lead1sub1", lead1);
        lead1sub2 = memberWithSupervisor("lead1sub2", lead1);
        lead2 = memberWithSupervisor("lead2", ceo);
        lead2sub1 = memberWithSupervisor("lead2sub1", lead2);
        lead2sub1sub1 = memberWithSupervisor("lead2sub1sub1", lead2sub1);
        lead2sub1sub2fired = memberWithSupervisor("lead2sub1sub2fired", lead2sub1, LocalDate.now());
        wildcard = memberWithoutBoss("wildcard");
    }

    @Test
    void testSupervisors() {
        // when we get the ceo supervisors
        var ceoSubs = memberProfileRepository.findSupervisorsForId(ceo.getId());

        // then we get nobody
        assertEquals(0, ceoSubs.size());

        // when we get the lead2 supervisors
        var lead2Sups = memberProfileRepository.findSupervisorsForId(lead2.getId());

        // then we get the CEO
        assertEquals(1, lead2Sups.size());
        assertEquals(Set.of(ceo), new HashSet<>(lead2Sups));

        // when we get a leaf employee
        var leafSups = memberProfileRepository.findSupervisorsForId(lead1sub2.getId());

        // then we get the lead and the CEO
        assertEquals(2, leafSups.size());
        assertEquals(Set.of(lead1, ceo), new HashSet<>(leafSups));

        // when we get a deeper leaf employee
        var deepLeafSups = memberProfileRepository.findSupervisorsForId(lead2sub1sub1.getId());

        // then we get the chain back up to the CEO
        assertEquals(3, deepLeafSups.size());
        assertEquals(Set.of(lead2sub1, lead2, ceo), new HashSet<>(deepLeafSups));

        // when we get a disconnected employee
        var wildSups = memberProfileRepository.findSupervisorsForId(wildcard.getId());

        // then we get an empty set
        assertEquals(0, wildSups.size());
    }

    @Test
    void testSubordinates() {
        // when we get the ceo subordinates
        var ceoSubs = memberProfileRepository.findSubordinatesForId(ceo.getId());

        // then we get everyone who isn't fired
        assertEquals(6, ceoSubs.size());
        assertEquals(Set.of(lead1, lead2, lead1sub1, lead1sub2, lead2sub1, lead2sub1sub1), new HashSet<>(ceoSubs));

        // when we get the lead2 subordinates
        var lead2Subs = memberProfileRepository.findSubordinatesForId(lead2.getId());

        // then we get everyone who isn't fired
        assertEquals(2, lead2Subs.size());
        assertEquals(Set.of(lead2sub1, lead2sub1sub1), new HashSet<>(lead2Subs));

        // when we get a leaf employee
        var leafSubs = memberProfileRepository.findSubordinatesForId(lead1sub2.getId());

        // then we get an empty set
        assertEquals(0, leafSubs.size());

        // when we get a disconnected employee
        var wildSubs = memberProfileRepository.findSubordinatesForId(wildcard.getId());

        // then we get an empty set
        assertEquals(0, wildSubs.size());
    }

    private MemberProfile memberWithoutBoss(String name) {
        return memberWithSupervisor(name, null, null);
    }

    private MemberProfile memberWithSupervisor(String name, MemberProfile supervisor) {
        return memberWithSupervisor(name, supervisor, null);
    }

    private MemberProfile memberWithSupervisor(String name, MemberProfile supervisor, LocalDate terminationDate) {
        return memberProfileRepository.save(
                new MemberProfile(
                        name,
                        null,
                        name,
                        null,
                        null,
                        null,
                        null,
                        name + "@work.com",
                        null,
                        null,
                        null,
                        supervisor != null ? supervisor.getId() : null,
                        terminationDate,
                        null,
                        null,
                        null
                )
        );
    }
}

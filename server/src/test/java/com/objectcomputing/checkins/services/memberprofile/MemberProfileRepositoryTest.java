package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemberProfileRepositoryTest extends TestContainersSuite implements MemberProfileFixture {

    @Inject
    MemberProfileRepository memberProfileRepository;

    Map<String, MemberProfile> hierarchy;

    @BeforeEach
    void setupHierarchy() {
        hierarchy = createHierarchy();
    }

    @Test
    void testSupervisors() {
        // when we get the ceo supervisors
        var ceoSubs = memberProfileRepository.findSupervisorsForId(id(HIERARCHY_CEO));

        // then we get nobody
        assertEquals(0, ceoSubs.size());

        // when we get the lead2 supervisors
        var lead2Sups = memberProfileRepository.findSupervisorsForId(id(HIERARCHY_LEAD2));

        // then we get the CEO
        assertEquals(1, lead2Sups.size());
        assertEquals(profiles(HIERARCHY_CEO), new HashSet<>(lead2Sups));

        // when we get a leaf employee
        var leafSups = memberProfileRepository.findSupervisorsForId(id(HIERARCHY_LEAD1_SUB2));

        // then we get the lead and the CEO
        assertEquals(2, leafSups.size());
        assertEquals(profiles(HIERARCHY_LEAD1, HIERARCHY_CEO), new HashSet<>(leafSups));

        // when we get a deeper leaf employee
        var deepLeafSups = memberProfileRepository.findSupervisorsForId(id(HIERARCHY_LEAD2_SUB1_SUB1));

        // then we get the chain back up to the CEO
        assertEquals(3, deepLeafSups.size());
        assertEquals(profiles(HIERARCHY_LEAD2_SUB1, HIERARCHY_LEAD2, HIERARCHY_CEO), new HashSet<>(deepLeafSups));

        // when we get a disconnected employee
        var wildSups = memberProfileRepository.findSupervisorsForId(id(HIERARCHY_WILDCARD));

        // then we get an empty set
        assertEquals(0, wildSups.size());
    }

    @Test
    void testSubordinates() {
        // when we get the ceo subordinates
        var ceoSubs = memberProfileRepository.findSubordinatesForId(id(HIERARCHY_CEO));

        // then we get everyone who isn't fired
        assertEquals(6, ceoSubs.size());
        assertEquals(profiles(HIERARCHY_LEAD1, HIERARCHY_LEAD2, HIERARCHY_LEAD1_SUB1, HIERARCHY_LEAD1_SUB2, HIERARCHY_LEAD2_SUB1, HIERARCHY_LEAD2_SUB1_SUB1), new HashSet<>(ceoSubs));

        // when we get the lead2 subordinates
        var lead2Subs = memberProfileRepository.findSubordinatesForId(id(HIERARCHY_LEAD2));

        // then we get everyone who isn't fired
        assertEquals(2, lead2Subs.size());
        assertEquals(profiles(HIERARCHY_LEAD2_SUB1, HIERARCHY_LEAD2_SUB1_SUB1), new HashSet<>(lead2Subs));

        // when we get a leaf employee
        var leafSubs = memberProfileRepository.findSubordinatesForId(id(HIERARCHY_LEAD1_SUB2));

        // then we get an empty set
        assertEquals(0, leafSubs.size());

        // when we get a disconnected employee
        var wildSubs = memberProfileRepository.findSubordinatesForId(id(HIERARCHY_WILDCARD));

        // then we get an empty set
        assertEquals(0, wildSubs.size());
    }

    private MemberProfile profile(String key) {
        return hierarchy.get(key);
    }

    private Set<MemberProfile> profiles(String... key) {
        return Arrays.stream(key).map(this::profile).collect(Collectors.toSet());
    }

    private UUID id(String key) {
        return profile(key).getId();
    }
}

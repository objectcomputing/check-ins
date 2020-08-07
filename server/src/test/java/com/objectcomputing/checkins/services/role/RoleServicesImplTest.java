package com.objectcomputing.checkins.services.role;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoleServicesImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @InjectMocks
    private RoleServicesImpl services;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(roleRepository, roleRepository, memberProfileRepository);
    }

    @Test
    void testRead() {
        Role role = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());

        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

        assertEquals(role, services.read(role.getId()));

        verify(roleRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testReadNullId() {
        assertNull(services.read(null));

        verify(roleRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSave() {
        Role role = new Role(RoleType.ADMIN, UUID.randomUUID());

        when(memberProfileRepository.findById(eq(role.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));
        when(roleRepository
                .findByRoleAndMemberid(eq(role.getRole()), eq(role.getMemberid())))
                .thenReturn(Optional.empty());
        when(roleRepository.save(eq(role))).thenReturn(role);

        assertEquals(role, services.save(role));

        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(roleRepository, times(1))
                .findByRoleAndMemberid(any(RoleType.class), any(UUID.class));
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void testSaveWithId() {
        Role role = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());

        RoleBadArgException exception = assertThrows(RoleBadArgException.class, () -> services.save(role));
        assertEquals(String.format("Found unexpected id %s for role", role.getId()), exception.getMessage());

        verify(roleRepository, never()).save(any(Role.class));
        verify(roleRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(roleRepository, never())
                .findByRoleAndMemberid(any(RoleType.class), any(UUID.class));
    }

    @Test
    void testSaveRoleNullRole() {
        Role role = new Role(null, UUID.randomUUID());

        RoleBadArgException exception = assertThrows(RoleBadArgException.class, () -> services.save(role));
        assertEquals(String.format("Invalid role %s", role), exception.getMessage());

        verify(roleRepository, never()).save(any(Role.class));
        verify(roleRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(roleRepository, never())
                .findByRoleAndMemberid(any(RoleType.class), any(UUID.class));
    }

    @Test
    void testSaveRoleNullMemberId() {
        Role role = new Role(RoleType.MEMBER, null);

        RoleBadArgException exception = assertThrows(RoleBadArgException.class, () -> services.save(role));
        assertEquals(String.format("Invalid role %s", role), exception.getMessage());

        verify(roleRepository, never()).save(any(Role.class));
        verify(roleRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(roleRepository, never())
                .findByRoleAndMemberid(any(RoleType.class), any(UUID.class));
    }

    @Test
    void testSaveNullRole() {
        assertNull(services.save(null));

        verify(roleRepository, never()).save(any(Role.class));
        verify(roleRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(roleRepository, never())
                .findByRoleAndMemberid(any(RoleType.class), any(UUID.class));
    }

    @Test
    void testSaveRoleNonExistingMember() {
        Role role = new Role(RoleType.MEMBER, UUID.randomUUID());

        when(memberProfileRepository.findById(eq(role.getMemberid()))).thenReturn(Optional.empty());

        RoleBadArgException exception = assertThrows(RoleBadArgException.class, () -> services.save(role));
        assertEquals(String.format("Member %s doesn't exist", role.getMemberid()), exception.getMessage());

        verify(roleRepository, never()).save(any(Role.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(roleRepository, never())
                .findByRoleAndMemberid(any(RoleType.class), any(UUID.class));
    }

    @Test
    void testSaveRoleAlreadyExistingMember() {
        Role role = new Role(RoleType.MEMBER, UUID.randomUUID());

        when(memberProfileRepository.findById(eq(role.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));
        when(roleRepository.findByRoleAndMemberid(eq(role.getRole()), eq(role.getMemberid())))
                .thenReturn(Optional.of(role));

        RoleBadArgException exception = assertThrows(RoleBadArgException.class, () -> services.save(role));
        assertEquals(String.format("Member %s already has role %s",
                role.getMemberid(), role.getRole()), exception.getMessage());

        verify(roleRepository, never()).save(any(Role.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(roleRepository, times(1))
                .findByRoleAndMemberid(any(RoleType.class), any(UUID.class));
    }

    @Test
    void testUpdate() {
        Role role = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());

        when(memberProfileRepository.findById(eq(role.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
        when(roleRepository.update(eq(role))).thenReturn(role);

        assertEquals(role, services.update(role));

        verify(roleRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(roleRepository, times(1)).update(any(Role.class));
    }

    @Test
    void testUpdateWithoutId() {
        Role role = new Role(RoleType.ADMIN, UUID.randomUUID());

        RoleBadArgException exception = assertThrows(RoleBadArgException.class, () -> services.update(role));
        assertEquals(String.format("Unable to locate role to update with id %s", role.getId()), exception.getMessage());

        verify(roleRepository, never()).update(any(Role.class));
        verify(roleRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(roleRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateRoleNullRole() {
        Role role = new Role(null, UUID.randomUUID());

        RoleBadArgException exception = assertThrows(RoleBadArgException.class, () -> services.update(role));
        assertEquals(String.format("Invalid role %s", role), exception.getMessage());

        verify(roleRepository, never()).update(any(Role.class));
        verify(roleRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(roleRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateRoleNullMemberId() {
        Role role = new Role(RoleType.MEMBER, null);

        RoleBadArgException exception = assertThrows(RoleBadArgException.class, () -> services.update(role));
        assertEquals(String.format("Invalid role %s", role), exception.getMessage());

        verify(roleRepository, never()).update(any(Role.class));
        verify(roleRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(roleRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateMemberDoesNotExist() {
        Role role = new Role(UUID.randomUUID(), RoleType.ADMIN, UUID.randomUUID());
        when(roleRepository.findById(eq(role.getId()))).thenReturn(Optional.of(role));
        when(memberProfileRepository.findById(eq(role.getMemberid()))).thenReturn(Optional.empty());

        RoleBadArgException exception = assertThrows(RoleBadArgException.class, () -> services.update(role));
        assertEquals(String.format("Member %s doesn't exist", role.getMemberid()), exception.getMessage());

        verify(roleRepository, never()).update(any(Role.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(roleRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateNullRole() {
        assertNull(services.update(null));

        verify(roleRepository, never()).update(any(Role.class));
        verify(roleRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(roleRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testFindByFieldsNullParams() {
        Set<Role> roleSet = Set.of(
                new Role(RoleType.ADMIN, UUID.randomUUID()),
                new Role(RoleType.ADMIN, UUID.randomUUID()),
                new Role(RoleType.ADMIN, UUID.randomUUID())
        );

        when(roleRepository.findAll()).thenReturn(roleSet);

        assertEquals(roleSet, services.findByFields(null, null));

        verify(roleRepository, times(1)).findAll();
        verify(roleRepository, never()).findByRole(any(RoleType.class));
        verify(roleRepository, never()).findByMemberid(any(UUID.class));
    }

    @Test
    void testFindByFieldsRole() {
        List<Role> roles = List.of(
                new Role(RoleType.ADMIN, UUID.randomUUID()),
                new Role(RoleType.ADMIN, UUID.randomUUID()),
                new Role(RoleType.ADMIN, UUID.randomUUID())
        );

        List<Role> rolesToFind = List.of(roles.get(1));
        Role role = rolesToFind.get(0);

        when(roleRepository.findAll()).thenReturn(roles);
        when(roleRepository.findByRole(role.getRole())).thenReturn(rolesToFind);

        assertEquals(new HashSet<>(rolesToFind), services.findByFields(role.getRole(), null));

        verify(roleRepository, times(1)).findAll();
        verify(roleRepository, times(1)).findByRole(any(RoleType.class));
        verify(roleRepository, never()).findByMemberid(any(UUID.class));
    }

    @Test
    void testFindByFieldsMemberId() {
        List<Role> roles = List.of(
                new Role(RoleType.ADMIN, UUID.randomUUID()),
                new Role(RoleType.ADMIN, UUID.randomUUID()),
                new Role(RoleType.ADMIN, UUID.randomUUID())
        );

        List<Role> rolesToFind = List.of(roles.get(1));
        Role role = rolesToFind.get(0);

        when(roleRepository.findAll()).thenReturn(roles);
        when(roleRepository.findByMemberid(role.getMemberid())).thenReturn(rolesToFind);

        assertEquals(new HashSet<>(rolesToFind), services.findByFields(null, role.getMemberid()));

        verify(roleRepository, times(1)).findAll();
        verify(roleRepository, times(1)).findByMemberid(any(UUID.class));
        verify(roleRepository, never()).findByRole(any(RoleType.class));
    }

    @Test
    void testFindByFieldsAll() {
        List<Role> roles = List.of(
                new Role(RoleType.ADMIN, UUID.randomUUID()),
                new Role(RoleType.ADMIN, UUID.randomUUID()),
                new Role(RoleType.ADMIN, UUID.randomUUID())
        );

        List<Role> rolesToFind = List.of(roles.get(1));

        Role role = rolesToFind.get(0);
        when(roleRepository.findAll()).thenReturn(roles);
        when(roleRepository.findByMemberid(role.getMemberid())).thenReturn(rolesToFind);
        when(roleRepository.findByRole(role.getRole())).thenReturn(rolesToFind);

        assertEquals(new HashSet<>(rolesToFind), services
                .findByFields(role.getRole(), role.getMemberid()));

        verify(roleRepository, times(1)).findAll();
        verify(roleRepository, times(1)).findByMemberid(any(UUID.class));
        verify(roleRepository, times(1)).findByRole(any(RoleType.class));
    }
}

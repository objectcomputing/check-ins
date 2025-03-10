import { createSelector } from 'reselect';

export const selectMemberProfiles = state => state.memberProfiles || [];
export const selectTerminatedMembers = state => state.terminatedMembers || [];
export const selectMemberSkills = state => state.memberSkills || [];
export const selectSkills = state => state.skills || [];
export const selectTeamMembers = state => state.teamMembers;
export const selectUserProfile = state => state.userProfile || {};
export const selectCheckins = state => state.checkins || [];
export const selectCsrfToken = state => state.csrf;
export const selectRoles = state => state.roles || [];
export const selectMemberRoles = state => state.memberRoles || [];
export const selectTeams = state => state.teams || [];
export const selectGuilds = state => state.guilds || [];
export const selectLoading = state => state.loading;
export const selectReviewPeriods = state => state.reviewPeriods;
export const selectPermissions = state => state.permissions;

export const noPermission = 'You do not have permission to view this page.';

const hasPermission = permissionName =>
  createSelector(
    selectUserProfile,
    userProfile =>
      userProfile &&
      userProfile.permissions &&
      userProfile.permissions.some(p => p?.permission?.includes(permissionName))
  );

export const selectTeamsLoading = createSelector(selectLoading, loading => {
  return loading.teams;
});

export const selectMemberProfilesLoading = createSelector(
  selectLoading,
  loading => loading.memberProfiles
);

export const selectCurrentUser = createSelector(
  selectUserProfile,
  selectMemberProfiles,
  (userProfile, memberProfiles) =>
    memberProfiles.find((current) => current?.id === userProfile?.id) || {}
);

export const selectIsAdmin = createSelector(
  selectUserProfile,
  userProfile =>
    userProfile && userProfile.role && userProfile.role.includes('ADMIN')
);

export const selectCurrentUserRoles = createSelector(
    selectUserProfile,
    userProfile => userProfile.role || []
);

export const selectHasPermissionAssignmentPermission = hasPermission(
  'CAN_ASSIGN_ROLE_PERMISSIONS'
);

export const selectHasViewPermissionPermission = hasPermission(
  'CAN_VIEW_ROLE_PERMISSIONS'
);

export const selectHasReportPermission = hasPermission('REPORT');

export const selectCanViewFeedbackAnswerPermission = hasPermission(
  'CAN_VIEW_FEEDBACK_ANSWER'
);

export const selectCanViewFeedbackRequestPermission = hasPermission(
  'CAN_VIEW_FEEDBACK_REQUEST'
);

export const selectCanViewReviewPeriodPermission = hasPermission(
  'CAN_VIEW_REVIEW_PERIOD'
);

export const selectHasAnniversaryReportPermission = hasPermission(
  'CAN_VIEW_ANNIVERSARY_REPORT'
);

export const selectHasBirthdayReportPermission = hasPermission(
  'CAN_VIEW_BIRTHDAY_REPORT'
);

export const selectHasCheckinsReportPermission = hasPermission(
  'CAN_VIEW_CHECKINS_REPORT'
);

export const selectHasProfileReportPermission = hasPermission(
  'CAN_VIEW_PROFILE_REPORT'
);

export const selectHasViewPulseReportPermission = hasPermission(
  'CAN_VIEW_ALL_PULSE_RESPONSES'
);

export const selectHasSkillsReportPermission = hasPermission(
  'CAN_VIEW_SKILLS_REPORT'
);

export const selectHasTeamSkillsReportPermission = hasPermission(
  'CAN_VIEW_SKILLS_REPORT'
);

export const selectHasCreateReviewAssignmentsPermission = hasPermission(
  'CAN_CREATE_REVIEW_ASSIGNMENTS'
);

export const selectHasDeleteReviewAssignmentsPermission = hasPermission(
  'CAN_DELETE_REVIEW_ASSIGNMENTS'
);

export const selectHasUpdateReviewAssignmentsPermission = hasPermission(
  'CAN_UPDATE_REVIEW_ASSIGNMENTS'
);

export const selectHasViewReviewAssignmentsPermission = hasPermission(
  'CAN_VIEW_REVIEW_ASSIGNMENTS'
);

export const selectHasCloseReviewPeriodPermission = hasPermission(
  'CAN_CLOSE_REVIEW_PERIOD'
);

export const selectHasCreateReviewPeriodPermission = hasPermission(
  'CAN_CREATE_REVIEW_PERIOD'
);

export const selectHasDeleteReviewPeriodPermission = hasPermission(
  'CAN_DELETE_REVIEW_PERIOD'
);

export const selectHasLaunchReviewPeriodPermission = hasPermission(
  'CAN_LAUNCH_REVIEW_PERIOD'
);

export const selectHasUpdateReviewPeriodPermission = hasPermission(
  'CAN_UPDATE_REVIEW_PERIOD'
);

export const selectHasMeritReportPermission = hasPermission(
  'CAN_CREATE_MERIT_REPORT'
);

export const selectHasUploadHoursPermission = hasPermission(
  'CAN_UPLOAD_HOURS'
);

export const selectHasEarnedCertificationsPermission = hasPermission(
  'CAN_MANAGE_EARNED_CERTIFICATIONS'
);

export const selectHasVolunteeringEventsPermission = hasPermission(
  'CAN_ADMINISTER_VOLUNTEERING_EVENTS'
);

export const selectHasVolunteeringOrganizationsPermission = hasPermission(
  'CAN_ADMINISTER_VOLUNTEERING_ORGANIZATIONS'
);

export const selectHasVolunteeringRelationshipsPermission = hasPermission(
  'CAN_ADMINISTER_VOLUNTEERING_RELATIONSHIPS'
);

export const selectCanEditMemberRolesPermission = hasPermission(
  'CAN_EDIT_MEMBER_ROLES'
);

export const selectHasCreateFeedbackPermission = hasPermission(
  'CAN_CREATE_FEEDBACK_REQUEST'
);

export const selectHasAdministerKudosPermission = hasPermission(
  'CAN_ADMINISTER_KUDOS'
);

export const selectHasCreateKudosPermission = hasPermission(
  'CAN_CREATE_KUDOS'
);

export const selectHasDeleteMembersPermission = hasPermission(
  'CAN_DELETE_ORGANIZATION_MEMBERS'
);

export const selectHasCreateMembersPermission = hasPermission(
  'CAN_CREATE_ORGANIZATION_MEMBERS'
);

export const selectHasImpersonateMembersPermission = hasPermission(
  'CAN_IMPERSONATE_MEMBERS'
);

export const selectHasViewSettingsPermission = hasPermission(
  'CAN_VIEW_SETTINGS'
);

export const selectHasAdministerSettingsPermission = hasPermission(
  'CAN_ADMINISTER_SETTINGS'
);

export const selectHasSendEmailPermission = hasPermission(
  'CAN_SEND_EMAIL'
);

export const selectCanViewCheckinsPermission = hasPermission(
  'CAN_VIEW_CHECKINS'
);

export const selectCanUpdateCheckinsPermission = hasPermission(
  'CAN_UPDATE_CHECKINS'
);

export const selectCanCreateCheckinsPermission = hasPermission(
  'CAN_CREATE_CHECKINS'
);

export const selectCanUpdateAllCheckinsPermission = hasPermission(
  'CAN_UPDATE_ALL_CHECKINS'
);

export const selectCanEditSkills = hasPermission(
  'CAN_EDIT_SKILLS'
);

export const selectCanViewPrivateNotesPermission = hasPermission(
  'CAN_VIEW_PRIVATE_NOTE'
);

export const selectCanCreatePrivateNotesPermission = hasPermission(
  'CAN_CREATE_PRIVATE_NOTE'
);

export const selectCanUpdatePrivateNotesPermission = hasPermission(
  'CAN_UPDATE_PRIVATE_NOTE'
);

export const selectCanAdministerCheckinDocuments = hasPermission(
  'CAN_ADMINISTER_CHECKIN_DOCUMENTS'
);

export const selectCanAdministerFeedbackRequests = hasPermission(
  'CAN_ADMINISTER_FEEDBACK_REQUEST'
);

export const selectCanEditAllOrganizationMembers = hasPermission(
  'CAN_EDIT_ALL_ORGANIZATION_MEMBERS',
);

export const selectCanViewTerminatedMembers = createSelector(
    selectCanEditAllOrganizationMembers,
    hasPermission(
        'CAN_VIEW_TERMINATED_MEMBERS'
    ),
    (canEdit, canView) => canEdit || canView
);

export const selectIsPDL = createSelector(
  selectUserProfile,
  userProfile =>
    userProfile && userProfile.role && userProfile.role.includes('PDL')
);

export const selectCurrentUserId = createSelector(
  selectCurrentUser,
  profile => profile?.id
);

export const selectOrderedSkills = createSelector(selectSkills, skills =>
  skills.slice().sort((last, next) => {
    var lastName = last.name.toUpperCase(); // ignore upper and lowercase
    var nextName = next.name.toUpperCase(); // ignore upper and lowercase
    if (lastName < nextName) {
      return -1;
    }
    if (lastName > nextName) {
      return 1;
    }

    // names must be equal
    return 0;
  })
);

export const selectCurrentMembers = createSelector(
  selectMemberProfiles,
  memberProfiles =>
    memberProfiles
      ?.filter(profile => {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        return (
          profile.terminationDate === null ||
          profile.terminationDate === undefined ||
          today <= new Date(profile.terminationDate)
        );
      })
      .sort((a, b) => a?.lastName?.localeCompare(b?.lastName))
);

export const selectCurrentMemberIds = createSelector(
  selectCurrentMembers,
  members => members.map(member => member.id)
);

export const selectTerminatedMemberIds = createSelector(
  selectTerminatedMembers,
  members => members.map(member => member.id)
);

export const selectProfileMap = createSelector(
  selectCurrentMembers,
  currentMembers => {
    if (currentMembers && currentMembers.length) {
      currentMembers = currentMembers.reduce((mappedById, member) => {
        mappedById[member.id] = member;
        return mappedById;
      }, {});
    }
    return currentMembers;
  }
);

export const selectProfileMapForTerminatedMembers = createSelector(
  selectTerminatedMembers,
  terminatedMembers => {
    if (terminatedMembers && terminatedMembers.length) {
      terminatedMembers = terminatedMembers.reduce((mappedById, member) => {
        mappedById[member.id] = member;
        return mappedById;
      }, {});
    }
    return terminatedMembers;
  }
);

export const selectProfile = createSelector(
  selectProfileMap,
  (state, profileId) => profileId,
  (profileMap, profileId) => profileMap[profileId]
);

export const selectActiveOrInactiveProfile = createSelector(
  selectProfileMap,
  selectProfileMapForTerminatedMembers,
  (state, profileId) => profileId,
  (profileMap, termedProfileMap, profileId) => profileMap[profileId] || termedProfileMap[profileId]
);

export const selectSkill = createSelector(
  selectSkills,
  (state, skillId) => skillId,
  (skills, skillId) => skills.find(skill => skill.id === skillId)
);

export const selectMySkills = createSelector(
  selectCurrentUserId,
  selectMemberSkills,
  (id, skills) => skills?.filter(skill => skill.memberid === id)
);

export const selectPendingSkills = createSelector(selectSkills, skills =>
  skills?.filter(skill => skill.pending)
);

export const selectPdlRoles = createSelector(selectRoles, roles =>
  roles?.filter(role => role.role?.includes('PDL'))
);

export const selectTerminatedMemberRoles = createSelector(
  selectMemberRoles,
  selectTerminatedMemberIds,
  (memberRoles, memberIds) => {
    return memberRoles?.filter(memberRole =>
      memberIds.includes(memberRole.memberRoleId.memberId)
    );
  }
);

export const selectTerminatedMembersAsOfDate = createSelector(
  selectTerminatedMembers,
  (_, date) => date,
  (terminatedMembers, date) =>
    terminatedMembers.filter(
      member => new Date(member.terminationDate) >= new Date(date)
    )
);

export const selectTerminatedMembersWithPDLRole = createSelector(
  selectTerminatedMemberRoles,
  selectPdlRoles,
  selectProfileMapForTerminatedMembers,
  (memberRoles, pdlRoles, terminatedMembersProfileMap) => {
    const terminatedPDLs = memberRoles?.filter(memberRole =>
      pdlRoles.find(role => role.id === memberRole?.memberRoleId?.roleId)
    );
    /** @type {MemberProfile[]} */
    const terminatedMembersWithPDLRole = terminatedPDLs?.map(
      memberRole => terminatedMembersProfileMap[memberRole?.memberRoleId?.memberId]
    );
    return terminatedMembersWithPDLRole;
  }
);

export const selectTerminatedMembersAsOfDateWithPDLRole = createSelector(
  selectTerminatedMembersAsOfDate,
  selectTerminatedMembersWithPDLRole,
  (terminatedMembers, terminatedPDLs) => {
    /** @type {MemberProfile[]} */
    const terminatedMembersWithPDLRole = terminatedMembers.filter(
      member => terminatedPDLs.find(pdl => pdl.id === member.id) !== undefined
    );
    return terminatedMembersWithPDLRole;
  }
);

export const selectActiveMemberRoles = createSelector(
  selectMemberRoles,
  selectCurrentMemberIds,
  (memberRoles, memberIds) =>
    memberRoles?.filter(memberRole =>
      memberIds.includes(memberRole.memberRoleId.memberId)
    )
);

export const selectMappedMemberRoles = createSelector(
  selectMemberRoles,
  selectRoles,
  (memberRoles, roles) => {
    const mappedMemberRoles = {};
    memberRoles.forEach(memberRole => {
      const memberId = memberRole.memberRoleId.memberId;
      const role = roles.find(role => role.id === memberRole.memberRoleId.roleId);
      if (!(memberId in mappedMemberRoles)) {
        mappedMemberRoles[memberId] = new Set();
      }
      mappedMemberRoles[memberId].add(role.role);
    });
    return mappedMemberRoles;
  }
);

export const selectMappedPdls = createSelector(
  selectProfileMap,
  selectPdlRoles,
  selectActiveMemberRoles,
  (memberProfileMap, roles, memberRoles) =>
    memberRoles
      ?.filter(
        memberRole =>
          roles.find(role => role.id === memberRole?.memberRoleId?.roleId) !==
          undefined
      )
      ?.map(memberRole =>
        memberRole?.memberRoleId?.memberId in memberProfileMap
          ? memberProfileMap[memberRole?.memberRoleId?.memberId]
          : {}
      )
);

export const selectOrderedCurrentMemberProfiles = createSelector(
  selectCurrentMembers,
  (mappedMemberProfiles) =>
    mappedMemberProfiles?.sort((a, b) => a.lastName.localeCompare(b.lastName))
);

export const selectOrderedPdls = createSelector(selectMappedPdls, mappedPdls =>
  mappedPdls?.sort((a, b) => {
    if (a.lastName < b.lastName) return -1;
    if (a.lastName > b.lastName) return 1;
    return 0;
  })
);

export const selectOrderedMemberFirstName = createSelector(
  selectCurrentMembers,
  mappedMemberProfiles =>
    mappedMemberProfiles.sort((a, b) => a.firstName.localeCompare(b.firstName))
);

export const selectCheckinMap = createSelector(selectCheckins, checkins => {
  if (checkins && checkins.length) {
    checkins = checkins.reduce((mappedById, checkin) => {
      mappedById[checkin.id] = checkin;
      return mappedById;
    }, {});
  }
  return checkins;
});

export const selectCheckin = createSelector(
  selectCheckinMap,
  (state, id) => id,
  (checkins, id) => checkins[id]
);

const toDate = ([year, month, day, hour, minute]) =>
  new Date(year, month - 1, day, hour, minute, 0);

export const selectCheckinsForMember = createSelector(
  selectCheckins,
  (state, memberId) => memberId,
  (checkins, memberId) =>
    checkins
      .filter(checkin => checkin.teamMemberId === memberId)
      .sort((last, next) => toDate(last.checkInDate) - toDate(next.checkInDate))
);

export const selectOpenCheckinsForMember = createSelector(
  selectCheckinsForMember,
  checkins => checkins.filter(checkin => !checkin.completed)
);

export const selectMostRecentCheckin = createSelector(
  selectCheckinsForMember,
  checkins => {
    if (checkins && checkins.length > 0) {
      return checkins && checkins[checkins.length - 1];
    }
  }
);

export const selectPDLCheckinMap = createSelector(selectCheckins, checkins =>
  checkins.reduce((accu, currentCheckin) => {
    if (
      accu[currentCheckin.pdlId] === undefined ||
      accu[currentCheckin.pdlId] === null
    ) {
      accu[currentCheckin.pdlId] = [];
    }
    accu[currentCheckin.pdlId].push(currentCheckin);
    return accu;
  }, {})
);

export const selectSupervisors = createSelector(
  selectCurrentMembers,
  selectProfileMap,
  (currentMembers, memberProfileMap) => {
    const filteredMembers = currentMembers?.filter(
      member => member.supervisorid
    );

    const supervisorIds = filteredMembers?.map(member => member.supervisorid);
    const uniqueSupervisorIds = [...new Set(supervisorIds)];

    const supervisors = uniqueSupervisorIds.map(id => memberProfileMap[id]);
    return supervisors;
  }
);

const buildSupervisorHierarchy = (allSupervisors, member, supervisorChain) => {
  const memberSupervisor = allSupervisors?.find(
    supervisor => supervisor?.id === member?.supervisorid
  );
  supervisorChain.push(memberSupervisor);
  return !memberSupervisor?.supervisorid
    ? supervisorChain
    : buildSupervisorHierarchy(
        allSupervisors,
        memberSupervisor,
        supervisorChain
      );
};

export const selectSupervisorHierarchyIds = selectedMember =>
  createSelector(selectSupervisors, allSupervisors =>
    buildSupervisorHierarchy(allSupervisors, selectedMember, []).map(
      supervisor => supervisor?.id
    )
  );

export const selectIsSupervisor = createSelector(
  selectCurrentUserId,
  selectSupervisors,
  (userId, supervisors) => {
    const isSupervisor = supervisors?.find(
      supervisor => supervisor?.id === userId
    );
    if (isSupervisor !== undefined) {
      return true;
    } else {
      return false;
    }
  }
);

const filterMembersBySupervisor = (currentMembers, supervisorId) =>
  currentMembers?.filter(currentTeamMember => {
    return currentTeamMember?.supervisorid === supervisorId;
  });

export const selectTeamMembersBySupervisorId = createSelector(
  selectCurrentMembers,
  (state, supervisorId) => supervisorId,
  filterMembersBySupervisor
);

export const selectMyTeam = createSelector(
  selectCurrentMembers,
  selectCurrentUserId,
  filterMembersBySupervisor
);

/* Internal Selector, not for export */
const selectSubordinatesPreventCycle = createSelector(
  selectTeamMembersBySupervisorId,
  (_, managerId) => managerId,
  (_, __, previouslyIncluded) => previouslyIncluded || [],
  state => state,
  (team, managerId, previouslyIncluded, state) =>
    team.reduce(
      (subordinates, teamMember) => {
        if (previouslyIncluded.some(current => current === teamMember.id))
          return subordinates;
        else
          return [
            ...subordinates,
            ...selectSubordinatesPreventCycle(state, teamMember.id, [
              ...previouslyIncluded,
              managerId
            ])
          ];
      },
      [...team]
    )
);

export const selectSubordinates = createSelector(
  selectTeamMembersBySupervisorId,
  (_, managerId) => managerId,
  state => state,
  (team, managerId, state) =>
    team.reduce(
      (subordinates, teamMember) => {
        return [
          ...subordinates,
          ...selectSubordinatesPreventCycle(state, teamMember.id, [managerId])
        ];
      },
      [...team]
    )
);

export const selectCurrentUserSubordinates = createSelector(
  selectCurrentUserId,
  state => state,
  (currentUserId, state) => selectSubordinates(state, currentUserId)
);

export const selectIsSubordinateOfCurrentUser = createSelector(
  selectCurrentUserSubordinates,
  (_, teamMemberId) => teamMemberId,
  (subordinates, teamMemberId) =>
    subordinates.some(teamMember => teamMember.id === teamMemberId)
);

export const selectTeamMembersWithCheckinPDL = createSelector(
  (_, pdlId) => pdlId,
  selectPDLCheckinMap,
  selectProfileMap,
  (pdlId, pdlCheckinMap, profileMap) =>
    pdlCheckinMap[pdlId]
      .map(checkin => checkin.teamMemberId)
      .reduce((accu, memberId) => {
        if (!accu.find(e => e?.id === memberId)) {
          accu.push(profileMap[memberId]);
        }
        return accu;
      }, [])
);

export const selectCheckinsForTeamMemberAndPDL = createSelector(
  selectCheckinsForMember,
  (state, teamMemberId, pdlId) => pdlId,
  (checkins, pdlId) => checkins.filter(checkin => checkin.pdlId === pdlId)
);

const getCheckinDate = checkin => {
  if (!checkin || !checkin.checkInDate) return;
  const [year, month, day, hour, minute] = checkin.checkInDate;
  return new Date(year, month - 1, day, hour, minute, 0);
};

const pastCheckin = checkin => Date.now() >= getCheckinDate(checkin).getTime();

export const selectFilteredCheckinsForTeamMemberAndPDL = createSelector(
  selectCheckinsForTeamMemberAndPDL,
  (state, teamMemberId, pdlId, includeClosed, includePlanned) => includeClosed,
  (state, teamMemberId, pdlId, includeClosed, includePlanned) => includePlanned,
  (checkins, includeClosed, includePlanned) =>
    checkins
      .filter(checkin => includeClosed || !checkin.completed)
      .filter(checkin => includePlanned || pastCheckin(checkin))
);

export const selectCheckinPDLS = createSelector(
  selectMemberProfiles,
  selectCheckins,
  (state, includeClosed, includePlanned) => includeClosed,
  (state, includeClosed, includePlanned) => includePlanned,
  (memberProfiles, checkins, includeClosed, includePlanned) => {
    const pdlSet = new Set();
    checkins
      .filter(checkin => includeClosed || !checkin.completed)
      .filter(checkin => includePlanned || pastCheckin(checkin))
      .forEach(checkin => pdlSet.add(checkin.pdlId));
    return memberProfiles.filter(member => pdlSet.has(member.id));
  }
);

export const selectNormalizedMembers = createSelector(
  selectCurrentMembers,
  (state, searchText) => searchText,
  (currentMembers, searchText) =>
    currentMembers
      ?.filter(member => {
        let normName = member.name
          .normalize('NFD')
          .replace(/[\u0300-\u036f]/g, '');
        let normSearchText = searchText
          .normalize('NFD')
          .replace(/[\u0300-\u036f]/g, '');
        return normName.toLowerCase().includes(normSearchText.toLowerCase());
      })
      .sort((a, b) => a.lastName.localeCompare(b.lastName))
);

export const selectNormalizedMembersAdmin = createSelector(
  selectMemberProfiles,
  selectTerminatedMembers,
  (state, searchText) => searchText,
  (memberProfiles, terminatedProfiles, searchText) =>
    memberProfiles
      .concat(terminatedProfiles)
      ?.filter(member => {
        let normName = member.name
          .normalize('NFD')
          .replace(/[\u0300-\u036f]/g, '');
        let normSearchText = searchText
          .normalize('NFD')
          .replace(/[\u0300-\u036f]/g, '');
        return normName.toLowerCase().includes(normSearchText.toLowerCase());
      })
      .sort((a, b) => a.lastName.localeCompare(b.lastName))
);

export const selectNormalizedTeams = createSelector(
  selectTeams,
  (state, searchText) => searchText,
  (teams, searchText) =>
    teams?.filter(team => {
      let normName = team.name.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
      let normSearchText = searchText
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '');
      return normName.toLowerCase().includes(normSearchText.toLowerCase());
    })
);

export const selectActiveTeams = createSelector(
  selectTeams,
  (teams, searchText) => teams?.filter(team => team.active)
);

export const selectActiveGuilds = createSelector(
  selectGuilds,
  (guilds, searchText) => guilds?.filter(guild => guild.active)
);

export const selectMyGuilds = createSelector(
  selectCurrentUserId,
  selectGuilds,
  (id, guilds) =>
    guilds?.filter(guild => guild.active &&
      guild.guildMembers?.some(member => member.memberId === id)
    )
);

export const selectMyTeams = createSelector(
  selectCurrentUserId,
  selectTeams,
  (id, teams) =>
    teams?.filter(team => team.active &&
      team.teamMembers?.some(member => member.memberId === id)
    )
);

export const selectReviewPeriodMap = createSelector(
  selectReviewPeriods,
  reviewPeriods => {
    if (reviewPeriods && reviewPeriods.length) {
      reviewPeriods = reviewPeriods.reduce((mappedById, period) => {
        mappedById[period.id] = period;
        return mappedById;
      }, {});
    }
    return reviewPeriods;
  }
);

export const selectReviewPeriod = createSelector(
  selectReviewPeriodMap,
  (state, periodId) => periodId,
  (periodMap, periodId) => periodMap[periodId]
);

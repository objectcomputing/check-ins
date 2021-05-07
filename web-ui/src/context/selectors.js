import { createSelector } from "reselect";

export const selectMemberProfiles = (state) => state.memberProfiles;
export const selectMemberSkills = (state) => state.memberSkills;
export const selectSkills = (state) => state.skills;
export const selectTeamMembers = (state) => state.teamMembers;
export const selectUserProfile = (state) => state.userProfile;
export const selectCheckins = (state) => state.checkins;
export const selectCsrfToken = (state) => state.csrf;
export const selectMemberRoles = (state) => state.roles;

export const selectCurrentUser = createSelector(
  selectUserProfile,
  (userProfile) =>
    userProfile && userProfile.memberProfile ? userProfile.memberProfile : {}
);

export const selectIsAdmin = createSelector(
  selectUserProfile,
  (userProfile) =>
    userProfile && userProfile.role && userProfile.role.includes("ADMIN")
);

export const selectIsPDL = createSelector(
  selectUserProfile,
  (userProfile) =>
    userProfile && userProfile.role && userProfile.role.includes("PDL")
);

export const selectCurrentUserId = createSelector(
  selectCurrentUser,
  (profile) => profile.id
);

export const selectOrderedSkills = createSelector(selectSkills, (skills) =>
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

export const selectProfileMap = createSelector(
  selectMemberProfiles,
  (memberProfiles) => {
    if (memberProfiles && memberProfiles.length) {
      memberProfiles = memberProfiles.reduce((mappedById, profile) => {
        mappedById[profile.id] = profile;
        return mappedById;
      }, {});
    }
    return memberProfiles;
  }
);

export const selectProfile = createSelector(
  selectProfileMap,
  (state, profileId) => profileId,
  (profileMap, profileId) => profileMap[profileId]
);

export const selectSkill = createSelector(
  selectSkills,
  (state, skillId) => skillId,
  (skills, skillId) => skills.find((skill) => skill.id === skillId)
);

export const selectMySkills = createSelector(
  selectCurrentUserId,
  selectMemberSkills,
  (id, skills) => skills?.filter((skill) => skill.memberid === id)
);

export const selectPendingSkills = createSelector(selectSkills, (skills) =>
  skills?.filter((skill) => skill.pending)
);

export const selectPdlRoles = createSelector(selectMemberRoles, (roles) =>
  roles?.filter((role) => role.role.includes("PDL"))
);

export const selectMappedPdls = createSelector(
  selectProfileMap,
  selectPdlRoles,
  (memberProfileMap, roles) =>
    roles?.map((role) => memberProfileMap[role.memberid])
);

export const selectOrderedPdls = createSelector(
  selectMappedPdls,
  (mappedPdls) =>
    mappedPdls?.sort((a, b) => {
      if (a.lastName < b.lastName) return -1;
      if (a.lastName > b.lastName) return 1;
      return 0;
    })
);

export const selectOrderedMemberProfiles = createSelector(
  selectMemberProfiles,
  (mappedMemberProfiles) =>
    mappedMemberProfiles.sort((a, b) => a.lastName.localeCompare(b.lastName))
);

export const selectCheckinMap = createSelector(selectCheckins, (checkins) => {
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
      .filter((checkin) => checkin.teamMemberId === memberId)
      .sort((last, next) => toDate(last.checkInDate) - toDate(next.checkInDate))
);

export const selectMostRecentCheckin = createSelector(
  selectCheckins,
  (state, memberid) => memberid,
  (checkins, memberid) => {
    if (checkins && checkins.length) {
      return checkins
        .filter((currentCheckin) => currentCheckin.teamMemberId === memberid)
        .reduce((mostRecent, checkin) => {
          return mostRecent === undefined ||
            toDate(checkin.checkInDate) > toDate(mostRecent.checkInDate)
            ? checkin
            : mostRecent;
        }, undefined);
    }
    return undefined;
  }
);

export const selectPDLCheckinMap = createSelector(selectCheckins, (checkins) =>
  checkins.reduce((accu, currentCheckin) => {
    if (accu[currentCheckin.pdlId] === undefined) {
      accu[currentCheckin.pdlId] = [];
    }
    accu[currentCheckin.pdlId].push(currentCheckin);
    return accu;
  }, {})
);

export const selectTeamMembersWithCheckinPDL = createSelector(
  (state, pdlId) => pdlId,
  selectPDLCheckinMap,
  selectProfileMap,
  (pdlId, pdlCheckinMap, profileMap) =>
    pdlCheckinMap[pdlId]
      .map((checkin) => checkin.teamMemberId)
      .reduce((accu, memberId) => {
        if (!accu.find((e) => e.id === memberId)) {
          accu.push(profileMap[memberId]);
        }
        return accu;
      }, [])
);

export const selectCheckinsForTeamMemberAndPDL = createSelector(
  selectCheckinsForMember,
  (state, teamMemberId, pdlId) => pdlId,
  (checkins, pdlId) => checkins.filter((checkin) => checkin.pdlId === pdlId)
);

export const selectCheckinPDLS = createSelector(
  selectMemberProfiles,
  selectCheckins,
  (memberProfiles, checkins) => {
    const pdlSet = new Set();
    checkins.forEach((checkin) => pdlSet.add(checkin.pdlId));
    return memberProfiles
      .filter((member) => pdlSet.has(member.id))
  }
);

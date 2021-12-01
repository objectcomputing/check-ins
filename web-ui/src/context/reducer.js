import {
  ADD_CHECKIN,
  ADD_MEMBER_SKILL,
  ADD_SKILL,
  ADD_TEAM,
  ADD_GUILD,
  DELETE_MEMBER_PROFILE,
  DELETE_MEMBER_SKILL,
  DELETE_ROLE,
  DELETE_SKILL,
  MY_PROFILE_UPDATE,
  SET_CSRF,
  SET_ROLES,
  SET_USER_ROLES,
  UPDATE_CHECKIN,
  UPDATE_CHECKINS,
  UPDATE_MEMBER_PROFILES,
  UPDATE_TERMINATED_MEMBERS,
  UPDATE_MEMBER_SKILLS,
  UPDATE_SKILL,
  UPDATE_SKILLS,
  UPDATE_GUILD,
  UPDATE_GUILDS,
  UPDATE_ROLES,
  UPDATE_TEAMS,
  UPDATE_TEAM_MEMBERS,
  UPDATE_TOAST,
  UPDATE_USER_BIO,
} from "./actions";

export const initialState = {
  checkins: [],
  csrf: undefined,
  index: 0,
  memberProfiles: [],
  terminatedMembers: [],
  memberSkills: [],
  roles: [],
  userRoles: [],
  skills: [],
  teams: [],
  guilds: [],
  toast: {
    severity: "",
    toast: "",
  },
  userProfile: {},
};

export const reducer = (state, action) => {
  switch (action.type) {
    case MY_PROFILE_UPDATE:
      state.userProfile = action.payload;
      break;
    case UPDATE_USER_BIO:
      state.userProfile.memberProfile.bioText = action.payload;
      break;
    case ADD_CHECKIN:
      state.checkins = [...state.checkins, action.payload];
      break;
    case UPDATE_CHECKINS:
      if (state?.checkins?.length > 0) {
        state.checkins = [...state.checkins];
        action.payload.forEach((checkin) => {
          const checkInIndex = state.checkins.findIndex(
            (current) => current.id === checkin.id
          );
          if (checkInIndex >= 0) {
            state.checkins[checkInIndex] = checkin;
          } else {
            state.checkins.push(checkin);
          }
        });
      } else {
        state.checkins = action.payload;
      }
      break;
    case UPDATE_CHECKIN:
      state.checkins = [...state.checkins];
      const checkInIndex = state.checkins.findIndex(
        (checkin) => checkin.id === action.payload.id
      );
      if (checkInIndex >= 0) {
        state.checkins[checkInIndex] = action.payload;
      } else {
        state.checkins.push(action.payload);
      }
      break;
    case ADD_SKILL:
      state.skills = [...state.skills, action.payload];
      break;
    case DELETE_SKILL:
      state.skills = state.skills.filter(
        (skill) => skill.id !== action.payload
      );
      break;
    case UPDATE_SKILL:
      state.skills = [...state.skills];
      const index = state.skills.findIndex(
        (skill) => skill.id === action.payload.id
      );
      state.skills[index] = action.payload;
      break;
    case UPDATE_SKILLS:
      state.skills = action.payload;
      break;
    case SET_CSRF:
      state.csrf = action.payload;
      break;
    case UPDATE_TOAST:
      state.toast = action.payload;
      break;
    case ADD_TEAM:
      state.teams = [...state.teams, action.payload];
      //sort by name
      state.teams.sort((a, b) => a.name.localeCompare(b.name));
      break;
    case UPDATE_TEAMS:
      state.teams = action.payload;
      //sort by name
      state.teams.sort((a, b) => a.name.localeCompare(b.name));
      break;
    case UPDATE_MEMBER_PROFILES:
      action.payload.forEach((member) => {
        member.birthDay = Array.isArray(member.birthDay)
          ? new Date(member.birthDay.join("/"))
          : member && member.birthDay
          ? member.birthDay
          : null;
        member.startDate = Array.isArray(member.startDate)
          ? new Date(member.startDate.join("/"))
          : member && member.startDate
          ? member.startDate
          : new Date();
        member.terminationDate = Array.isArray(member.terminationDate)
          ? new Date(member.terminationDate.join("/"))
          : member && member.terminationDate
          ? member.terminationDate
          : null;
      });
      state.memberProfiles = action.payload;
      break;
    case UPDATE_TERMINATED_MEMBERS:
      state.terminatedMembers = action.payload;
      break;
    case UPDATE_TEAM_MEMBERS:
      state.teamMembers
        ? (state.teamMembers = [...state.teamMembers, action.payload])
        : (state.teamMembers = action.payload);
      break;
    case UPDATE_MEMBER_SKILLS:
      state.memberSkills = action.payload;
      break;
    case DELETE_MEMBER_SKILL:
      state.memberSkills = [
        ...state.memberSkills.filter(
          (mSkill) => mSkill.skillid !== action.payload
        ),
      ];
      break;
    case DELETE_MEMBER_PROFILE:
      state.memberProfiles = [
        ...state.memberProfiles.filter(
          (profile) => profile.id !== action.payload
        ),
      ];
      break;
    case ADD_MEMBER_SKILL:
      state.memberSkills = [...state.memberSkills, action.payload];
      break;
    case SET_ROLES:
      state.roles = action.payload;
      break;
    case SET_USER_ROLES:
      state.userRoles = action.payload;
      break;
    case DELETE_ROLE:
      state.roles = state.roles.filter((role) => role.id !== action.payload);
      break;
    case UPDATE_ROLES:
      state.roles = [...state.roles, action.payload];
      break;
    case ADD_GUILD:
      state.guilds = [...state.guilds, action.payload];
      //sort by name
      state.guilds.sort((a, b) => a.name.localeCompare(b.name));
      break;
    case UPDATE_GUILD:
      const { id } = action.payload;
      const idx = state.guilds.findIndex((guild) => guild.id === id);
      state.guilds[idx] = action.payload;
      state.guilds = [...state.guilds];
      break;
    case UPDATE_GUILDS:
      state.guilds = action.payload;
      //sort by name
      state.guilds.sort((a, b) => a.name.localeCompare(b.name));
      state.guilds = [...state.guilds];
      break;
    default:
  }
  return { ...state };
};

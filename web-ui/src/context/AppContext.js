import React, { useEffect, useReducer, useMemo } from "react";
import { createSelector } from 'reselect';
import { getCurrentUser, updateMember, getAllMembers } from "../api/member";
import { getAllTeamMembers } from "../api/team";
import { getCheckinByMemberId, createCheckin } from "../api/checkins";
import { BASE_API_URL } from "../api/api";
import axios from "axios";

export const MY_PROFILE_UPDATE = "@@check-ins/update_profile";
export const UPDATE_USER_BIO = "@@check-ins/update_bio";
export const UPDATE_CHECKINS = "@@check-ins/update_checkins";
export const UPDATE_INDEX = "@@check-ins/update_index";
export const UPDATE_TOAST = "@@check-ins/update_toast";
export const UPDATE_CURRENT_CHECKIN = "@@check-ins/update_current_checkin";
export const UPDATE_TEAMS = "@@check-ins/update_teams";
export const UPDATE_MEMBER_PROFILES = "@@check-ins/update_member_profiles";
export const UPDATE_TEAM_MEMBERS = "@@check-ins/update_team_members";
export const UPDATE_SELECTED_PROFILE = "@@check-ins/update_selected_profile";
const SET_CSRF = "@@check-ins/update_csrf";

const AppContext = React.createContext();

const reducer = (state, action) => {
  switch (action.type) {
    case MY_PROFILE_UPDATE:
      state.userProfile = action.payload;
      break;
    case UPDATE_USER_BIO:
      state.userProfile.memberProfile.bioText = action.payload;
      updateMember(state.userProfile.memberProfile);
      break;
    case UPDATE_CHECKINS:
      state.checkins = action.payload;
      //sort by date
      state.checkins.sort(function (a, b) {
        return new Date(...a.checkInDate) - new Date(...b.checkInDate);
      });
      state.currentCheckin = state.checkins[state.checkins.length - 1];
      break;
    case SET_CSRF:
      state.csrf = action.payload;
      break;
    case UPDATE_TOAST:
      state.toast = action.payload;
      break;
    case UPDATE_TEAMS:
      state.teams = action.payload;
      break;
    case UPDATE_MEMBER_PROFILES:
      state.memberProfiles = action.payload;
      break;
    case UPDATE_TEAM_MEMBERS:
      state.teamMembers = action.payload;
      break;
    case UPDATE_CURRENT_CHECKIN:
      state.currentCheckin = action.payload;
      break;
    case UPDATE_SELECTED_PROFILE:
      const { payload } = action;
      if (state.selectedProfile !== payload) {
        state.selectedProfile = payload;
        state.currentCheckin = payload ? payload.checkIn : {};
      }
      if (payload === undefined) {
        state.checkins = [];
      }
      break;
    default:
  }
  return { ...state };
};

const initialState = {
  checkins: [],
  csrf: undefined,
  currentCheckin: {},
  teams: [],
  memberProfiles: [],
  index: 0,
  selectedProfile: undefined,
  toast: {
    severity: "",
    toast: "",
  },
  userProfile: undefined,
};

const getCheckins = async (id, pdlId, date, dispatch, csrf) => {
  const res = await getCheckinByMemberId(id, csrf);
  let data =
    res.payload && res.payload.data && res.payload.status === 200 && !res.error
      ? res.payload.data
      : null;
  if (data && data.length > 0) {
    const allComplete = data.every((checkin) => checkin.completed === true);
    if (allComplete) {
      const prevCheckinDate = data[data.length - 1].checkInDate;
      if (pdlId) {
        const res = await createCheckin({
          teamMemberId: id,
          pdlId: pdlId,
          checkInDate: date(3, prevCheckinDate),
          completed: false,
        });
        const checkin =
          res.payload && res.payload.data && !res.error
            ? res.payload.data
            : null;
        data.push(checkin);
      }
    }
  } else if (data && data.length === 0) {
    if (pdlId) {
      const res = await createCheckin({
        teamMemberId: id,
        pdlId: pdlId,
        checkInDate: date(1),
        completed: false,
      });
      const checkin =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      data = [checkin];
    }
  }
  //without this check you get infinite checkin calls
  if (data.length > 0) {
    dispatch({ type: UPDATE_CHECKINS, payload: data });
  }
};

const AppContextProvider = (props) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const memberProfile =
    state && state.userProfile && state.userProfile.memberProfile
      ? state.userProfile.memberProfile
      : undefined;
  const id = memberProfile ? memberProfile.id : undefined;
  const selectedProfile = state && state.selectedProfile;
  const selectedId = selectedProfile ? selectedProfile.id : undefined;

  const pdlId = memberProfile ? memberProfile.pdlId : undefined;

  const { csrf } = state;

  useEffect(() => {
    const getCsrf = async () => {
      if (!csrf) {
        const res = await axios({
          url: `${BASE_API_URL}/csrf/cookie`,
          responseType: "text",
          withCredentials: true,
        });
        if (res && res.data) {
          dispatch({ type: SET_CSRF, payload: res.data });
        }
      }
    };
    getCsrf();
  }, [csrf]);

  useEffect(() => {
    const updateUserProfile = async () => {
      let res = await getCurrentUser(csrf);
      let profile =
        res.payload && res.payload.data && !res.error
          ? res.payload.data
          : undefined;

      if (profile) {
        dispatch({ type: MY_PROFILE_UPDATE, payload: profile });
      }
    };
    if (csrf) {
      updateUserProfile();
    }
  }, [csrf]);

  const date = (months, prevCheckinDate) => {
    let currentMonth = new Date().getMonth() + 1;
    let newDate = prevCheckinDate ? new Date(...prevCheckinDate) : new Date();
    if (prevCheckinDate) {
      let prevCheckinMonth = prevCheckinDate.getMonth() + 1;
      newDate.setMonth(
        newDate.getMonth() + currentMonth - prevCheckinMonth >= 3 ? 1 : months
      );
    } else {
      newDate.setMonth(newDate.getMonth() + months);
    }
    const year = newDate.getFullYear();
    const month = newDate.getMonth() + 1;
    const day = newDate.getDate();
    const hours = newDate.getHours();
    const minutes = newDate.getMinutes();
    const dateTimeArray = [year, month, day, hours, minutes, 0];
    return dateTimeArray;
  };

  useEffect(() => {
    async function getMemberProfiles() {
      let res = await getAllMembers(csrf);
      let profiles =
        res.payload && res.payload.data && !res.error
          ? res.payload.data
          : undefined;

      if (profiles) {
        dispatch({ type: UPDATE_MEMBER_PROFILES, payload: profiles });
      }
    }
    if (csrf) {
      getMemberProfiles();
    }
  }, [csrf]);

  useEffect(() => {
    async function getTeamMembers() {
      let res = await getAllTeamMembers(csrf);
      let teamMembers =
        res.payload && res.payload.data && !res.error
          ? res.payload.data
          : undefined;

      if (teamMembers) {
        dispatch({ type: UPDATE_TEAM_MEMBERS, payload: teamMembers });
      }
    }

    if (csrf) {
      getTeamMembers();
    }
  }, [csrf]);

  useEffect(() => {
    if (id && state.checkins.length === 0 && csrf) {
      getCheckins(id, pdlId, date, dispatch, csrf);
    }
  }, [csrf, state.checkins, id, pdlId]);

  useEffect(() => {
    if (selectedId && csrf) {
      getCheckins(selectedId, id, date, dispatch, csrf);
    }
  }, [csrf, selectedId, id]);

  const value = useMemo(() => {
    return { state, dispatch };
  }, [state]);
  return (
    <AppContext.Provider value={props.value || value}>
      {props.children}
    </AppContext.Provider>
  );
};

// const selectProfileMap = ({ memberProfiles }) => {
//   if (memberProfiles && memberProfiles.length) {
//     memberProfiles = memberProfiles.reduce((mappedById, profile) => {
//       mappedById[profile.id] = profile;
//       return mappedById;
//     }, {});
//   }
//   return memberProfiles;
// };

export const selectMemberProfiles = state => state.memberProfiles;
export const selectTeamMembers = state => state.teamMembers;

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

export const selectMembersByTeamId = id => createSelector(
  selectTeamMembers,
  (teamMembers) => {
    let members = [];
    if (teamMembers && teamMembers.length) {
      members = teamMembers.filter((member) => member.teamid === id);
    }
    return members;
  }
);

export const selectMemberProfilesByTeamId = id => createSelector(
  selectMembersByTeamId(id),
  selectProfileMap,
  (members, profileMap) => members.map((member) => {
      return { ...profileMap[member.memberid], ...member };
    })
);

export { AppContext, AppContextProvider };

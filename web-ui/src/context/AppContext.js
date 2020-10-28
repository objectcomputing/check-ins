import React, { useEffect, useReducer, useMemo } from "react";
import { getCurrentUser, updateMember, getAllMembers } from "../api/member";
import { getAllTeamMembers } from "../api/team";
import { getCheckinByMemberId, createCheckin } from "../api/checkins";
import Cookies from 'universal-cookie';
import axios from "axios";
import {resolve} from '../api/api.js'



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
export const GET_COOKIE="@@check-ins/get_cookie";

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
    case GET_COOKIE:
       state._csrf = action.payload
    default:
  }
  return { ...state };
};

const initialState = {
  checkins: [],
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
  _csrf:undefined,
};

const getCheckins = async (id, pdlId, date, dispatch) => {
  const res = await getCheckinByMemberId(id);
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
  } else if (data.length === 0) {
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
  dispatch({ type: UPDATE_CHECKINS, payload: data });
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
  const getCookie = async () => {
  return await resolve(
    axios({
      method: "get",
      url: 'http://localhost:8080/csrf/cookie',
      responseType: "json",
      withCredentials: true,
    })
  );
};
//let _csrf;
 useEffect(async()  => {
 let res = await getCookie()
 console.log({res})
 if(res&&res.payload&&res.payload.data){
    const {_csrf}= res.payload.data
    dispatch({type: GET_COOKIE, payload: _csrf})}
    }, []);
 if(state._csrf){
 const cookies = new Cookies();
 cookies.set('_csrf', state._csrf, { path: '/' });
 console.log(state._csrf)
 }
  useEffect(() => {
    async function updateUserProfile() {
      let res = await getCurrentUser();
      let profile =
        res.payload && res.payload.data && !res.error
          ? res.payload.data
          : undefined;

      if (profile) {
        dispatch({ type: MY_PROFILE_UPDATE, payload: profile });
      }
    }
    updateUserProfile();
  }, []);

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
      let res = await getAllMembers();
      let profiles =
        res.payload && res.payload.data && !res.error
          ? res.payload.data
          : undefined;

      if (profiles) {
        dispatch({ type: UPDATE_MEMBER_PROFILES, payload: profiles });
      }
    }
    getMemberProfiles();
  }, []);

  useEffect(() => {
    async function getTeamMembers() {
      let res = await getAllTeamMembers();
      let teamMembers =
        res.payload && res.payload.data && !res.error
          ? res.payload.data
          : undefined;

      if (teamMembers) {
        dispatch({ type: UPDATE_TEAM_MEMBERS, payload: teamMembers });
      }
    }
    getTeamMembers();
  }, []);

  useEffect(() => {
    if (id && state.checkins.length === 0) {
      getCheckins(id, pdlId, date, dispatch);
    }
  }, [state.checkins, id, pdlId]);

  useEffect(() => {
    if (selectedId) {
      getCheckins(selectedId, id, date, dispatch);
    }
  }, [selectedId, id]);

  const value = useMemo(() => {
    return { state, dispatch };
  }, [state]);
  return (
    <AppContext.Provider value={props.value || value}>
      {props.children}
    </AppContext.Provider>
  );
};

const selectProfileMap = ({ memberProfiles }) => {
  if (memberProfiles && memberProfiles.length) {
    memberProfiles = memberProfiles.reduce((mappedById, profile) => {
      mappedById[profile.id] = profile;
      return mappedById;
    }, {});
  }
  return memberProfiles;
};

const selectMembersByTeamId = ({ teamMembers }) => (id) => {
  let members = [];
  if (teamMembers && teamMembers.length) {
    members = teamMembers.filter((member) => member.teamid === id);
  }
  return members;
};

const selectMemberProfilesByTeamId = (state) => (id) =>
  selectMembersByTeamId(state)(id).map((member) => {
    return { ...selectProfileMap(state)[member.memberid], ...member };
  });

AppContext.selectProfileById = selectProfileMap;
AppContext.selectMemberProfilesByTeamId = selectMemberProfilesByTeamId;

export { AppContext, AppContextProvider };

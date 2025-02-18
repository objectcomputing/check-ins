import React, { useEffect, useReducer, useMemo } from 'react';
import { reducer, initialState } from './reducer';
import { getCheckins, getAllCheckinsForAdmin } from './thunks';
import {
  MY_PROFILE_UPDATE,
  SET_CSRF,
  SET_ROLES,
  SET_USER_ROLES,
  UPDATE_GUILDS,
  UPDATE_MEMBER_SKILLS,
  UPDATE_MEMBER_PROFILES,
  UPDATE_TERMINATED_MEMBERS,
  UPDATE_SKILLS,
  UPDATE_CERTIFICATIONS,
  UPDATE_TEAMS,
  UPDATE_PEOPLE_LOADING,
  UPDATE_TEAMS_LOADING
} from './actions';
import {
  getCurrentUser,
  getAllMembers,
  getAllTerminatedMembers
} from '../api/member';
import {
  selectCanViewCheckinsPermission,
  selectCanViewTerminatedMembers,
} from './selectors';
import { getAllRoles, getAllUserRoles } from '../api/roles';
import { getMemberSkills } from '../api/memberskill';
import { BASE_API_URL } from '../api/api';
import { getAllGuilds } from '../api/guild';
import { getSkills } from '../api/skill';
import { getAllTeams } from '../api/team';
import {getCertifications} from "../api/certification.js";

const AppContext = React.createContext();

function getSessionCookieValue(name) {
  const cookies = document?.cookie?.split(';');
  for (let i = 0; i < cookies.length; i++) {
    const cookie = cookies[i].trim();
    if (cookie.startsWith(name + '=')) {
      return decodeURIComponent(cookie.substring(name.length + 1));
    }
  }
  return null;
}

const AppContextProvider = props => {
  const [state, dispatch] = useReducer(
    reducer,
    props?.value?.state || initialState
  );
  const userProfile =
    state && state.userProfile ? state.userProfile : undefined;
  const memberProfile =
    userProfile && userProfile.memberProfile
      ? userProfile.memberProfile
      : undefined;

  const id = memberProfile ? memberProfile.id : undefined;
  const pdlId = memberProfile ? memberProfile.pdlId : undefined;
  const {
    csrf,
    guilds,
    teams,
    memberSkills,
    memberProfiles,
    checkins,
    skills,
    certifications,
    roles,
    userRoles
  } = state;
  const url = `${BASE_API_URL}/csrf/cookie`;
  useEffect(() => {
    const getCsrf = async () => {
      if (!csrf) {
        const payload = getSessionCookieValue('_csrf');
        if (payload) {
          dispatch({ type: SET_CSRF, payload });
        } else {
          const res = await fetch(url, {
            responseType: 'text',
            credentials: 'include'
          });
          if (res && res.ok) {
            dispatch({ type: SET_CSRF, payload: await res.text() });
          }
        }
      }
    };
    getCsrf();
  }, [csrf]);

  useEffect(() => {
    async function getGuilds() {
      let res = await getAllGuilds(csrf);
      let data =
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data) {
        dispatch({ type: UPDATE_GUILDS, payload: data });
      }
    }
    if (csrf && !guilds) {
      getGuilds();
    }
  }, [csrf, guilds]);

  useEffect(() => {
    async function getTeams() {
      let res = await getAllTeams(csrf);
      let data =
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data) {
        dispatch({ type: UPDATE_TEAMS, payload: data });
        dispatch({ type: UPDATE_TEAMS_LOADING });
      }
    }
    if (csrf && !teams) {
      dispatch({ type: UPDATE_TEAMS_LOADING });
      getTeams();
    }
  }, [csrf, teams, dispatch]);

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
    if (csrf && !userProfile) {
      updateUserProfile();
    }
  }, [csrf, userProfile]);

  useEffect(() => {
    const getAllMemberSkills = async () => {
      const res = await getMemberSkills(csrf);
      const memberSkills =
        res && res.payload && res.payload.data ? res.payload.data : null;
      if (memberSkills) {
        dispatch({ type: UPDATE_MEMBER_SKILLS, payload: memberSkills });
      }
    };
    if (csrf && !memberSkills) {
      getAllMemberSkills();
    }
  }, [csrf, memberSkills]);

  useEffect(() => {
    async function getMemberProfiles() {
      let res = await getAllMembers(csrf);
      let profiles =
        res.payload && res.payload.data && !res.error
          ? res.payload.data
          : undefined;

      if (profiles) {
        dispatch({ type: UPDATE_MEMBER_PROFILES, payload: profiles });
        dispatch({ type: UPDATE_PEOPLE_LOADING, payload: false });
      }
    }
    async function getTerminatedMembers() {
      let res = await getAllTerminatedMembers(csrf);
      let profiles =
        res.payload && res.payload.data && !res.error
          ? res.payload.data
          : undefined;

      if (profiles) {
        dispatch({ type: UPDATE_TERMINATED_MEMBERS, payload: profiles });
      }
    }
    if (csrf && userProfile && !memberProfiles) {
      dispatch({ type: UPDATE_PEOPLE_LOADING, payload: true });
      getMemberProfiles();
      if (selectCanViewTerminatedMembers(state)) {
        getTerminatedMembers();
      }
    }
  }, [csrf, userProfile, memberProfiles]);

  useEffect(() => {
    function getAllTheCheckins() {
      if (
        userProfile &&
        userProfile.permissions?.some(p =>
          p?.permission?.includes('CAN_VIEW_CHECKINS_REPORT')
        ) &&
        id &&
        csrf
      ) {
        getAllCheckinsForAdmin(dispatch, csrf);
      } else if (id && csrf && selectCanViewCheckinsPermission(state)) {
        getCheckins(id, pdlId, dispatch, csrf);
      }
    }
    if (csrf && !checkins) {
      getAllTheCheckins();
    }
  }, [csrf, pdlId, id, userProfile, checkins]);

  useEffect(() => {
    const getAllSkills = async () => {
      const res = await getSkills(csrf);
      const data =
        res &&
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data && data.length > 0) {
        dispatch({ type: UPDATE_SKILLS, payload: data });
      }
    };
    if (csrf && !skills) {
      getAllSkills();
    }
  }, [csrf, skills]);

  useEffect(() => {
    const getAllCertifications = async () => {
      const res = await getCertifications(csrf);
      const data =
          res &&
          res.payload &&
          res.payload.data &&
          res.payload.status === 200 &&
          !res.error
              ? res.payload.data
              : null;
      if (data && data.length > 0) {
        dispatch({ type: UPDATE_CERTIFICATIONS, payload: data });
      }
    };
    if (csrf && !certifications) {
      getAllCertifications();
    }
  }, [csrf, certifications]);

  useEffect(() => {
    const getRoles = async () => {
      const res = await getAllRoles(csrf);
      const data =
        res &&
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data && Array.isArray(data) && data.length > 0) {
        dispatch({ type: SET_ROLES, payload: data });
      }
    };
    if (csrf && !roles) {
      getRoles();
    }
  }, [csrf, roles]);

  useEffect(() => {
    const getUserRoles = async () => {
      // make call to the API
      let res = await getAllUserRoles(csrf);
      return res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
        ? res.payload.data
        : null;
    };

    if (csrf && !userRoles) {
      getUserRoles().then(userRoles => {
        dispatch({ type: SET_USER_ROLES, payload: userRoles });
      });
    }
  }, [csrf, userRoles]);

  const value = useMemo(() => {
    return { state, dispatch };
  }, [state]);
  return (
    <AppContext.Provider value={props.value || value}>
      {props.children}
    </AppContext.Provider>
  );
};

export { AppContext, AppContextProvider };

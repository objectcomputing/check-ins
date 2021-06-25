import React, { useEffect, useReducer, useMemo } from "react";
import { reducer, initialState } from "./reducer";
import { getCheckins, getAllCheckinsForAdmin } from "./thunks";
import {
  MY_PROFILE_UPDATE,
  SET_CSRF,
  SET_ROLES,
  UPDATE_GUILDS,
  UPDATE_MEMBER_SKILLS,
  UPDATE_MEMBER_PROFILES,
  UPDATE_TERMINATED_MEMBERS,
  UPDATE_SKILLS,
  UPDATE_TEAMS,
} from "./actions";
import { getCurrentUser, getAllMembers, getAllRoles } from "../api/member";
import { getMemberSkills } from "../api/memberskill";
import { BASE_API_URL } from "../api/api";
import { getAllGuilds } from "../api/guild";
import { getSkills } from "../api/skill";
import { getAllTeams } from "../api/team";
import axios from "axios";

const AppContext = React.createContext();

const AppContextProvider = (props) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const userProfile = state && state.userProfile ? state.userProfile : undefined;
  const memberProfile =
    userProfile && userProfile.memberProfile
    ? userProfile.memberProfile
    : undefined;

  const id = memberProfile ? memberProfile.id : undefined;
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
    if (csrf) {
      getGuilds();
    }
  }, [csrf]);

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
      }
    }
    if (csrf) {
      getTeams();
    }
  }, [csrf, dispatch]);

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

  useEffect(() => {
    const getAllMemberSkills = async () => {
      const res = await getMemberSkills(csrf);
      const memberSkills =
        res && res.payload && res.payload.data ? res.payload.data : null;
      if (memberSkills) {
        dispatch({ type: UPDATE_MEMBER_SKILLS, payload: memberSkills });
      }
    };
    if (csrf) {
      getAllMemberSkills();
    }
  }, [csrf]);

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
    async function getTerminatedMembers() {
      let res = await getTerminatedMembers(csrf);
      let profiles =
        res.payload && res.payload.data && !res.error
          ? res.payload.data
          : undefined;

      if (profiles) {
        dispatch({ type: UPDATE_TERMINATED_MEMBERS, payload: profiles });
      }
    }

    if (csrf && userProfile) {
      getMemberProfiles();
      if (userProfile.role.includes("ADMIN")) {
        getTerminatedMembers();
      }
    }
  }, [csrf, userProfile]);

  useEffect(() => {
    function getAllTheCheckins() {
      if (userProfile && userProfile.role.includes("ADMIN") && id && csrf) {
        getAllCheckinsForAdmin(dispatch, csrf);
      } else if (id && csrf) {
        getCheckins(id, pdlId, dispatch, csrf);
      }
    }
    if (csrf) {
      getAllTheCheckins();
    }
  }, [csrf, pdlId, id, userProfile]);

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
    if (csrf) {
      getAllSkills();
    }
  }, [csrf]);

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
      if (data && data.length > 0) {
        dispatch({ type: SET_ROLES, payload: data });
      }
    };
    if (csrf) {
      getRoles();
    }
  }, [csrf]);

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

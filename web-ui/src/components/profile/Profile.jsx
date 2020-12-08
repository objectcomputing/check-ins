import React, { useContext, useEffect, useState } from "react";

import {
  AppContext,
  ADD_MEMBER_SKILL,
  ADD_SKILL,
  DELETE_MEMBER_SKILL,
  selectMySkills,
  selectCurrentUser,
  UPDATE_USER_BIO,
} from "../../context/AppContext";
import Search from "./Search";
import { getAvatarURL } from "../../api/api.js";
import { getSkill, createSkill } from "../../api/skill.js";
import { createMemberSkill, deleteMemberSkill } from "../../api/memberskill.js";
import { getMember } from "../../api/member";

import EditIcon from "@material-ui/icons/Edit";
import Button from "@material-ui/core/Button";
import CancelIcon from "@material-ui/icons/Cancel";
import Avatar from "@material-ui/core/Avatar";

import "./Profile.css";

const Profile = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, skills } = state;
  const userProfile = selectCurrentUser(state);

  const [mySkills, setMySkills] = useState([]);
  const { bioText, workEmail, name, title, id, pdlId } = userProfile;

  const [pdl, setPDL] = useState();
  const [bio, setBio] = useState();
  const [updating, setUpdating] = useState(false);
  const [disabled, setDisabled] = useState(true);

  const myMemberSkills = selectMySkills(state);

  useEffect(() => {
    async function updateBio() {
      setBio(bioText);
    }
    updateBio();
  }, [bioText]);

  // Get PDL's name
  useEffect(() => {
    async function getPDLName() {
      if (pdlId) {
        let res = await getMember(pdlId, csrf);
        let pdlProfile =
          res.payload.data && !res.error ? res.payload.data : undefined;
        setPDL(pdlProfile ? pdlProfile.name : "");
      }
    }
    if (csrf) {
      getPDLName();
    }
  }, [csrf, pdlId]);

  useEffect(() => {
    const getSkills = async () => {
      const skillsResults = await Promise.all(
        myMemberSkills.map((mSkill) => getSkill(mSkill.skillid, csrf))
      );
      const currentUserSkills = skillsResults.map(
        (result) => result.payload.data
      );
      setMySkills(currentUserSkills);
    };
    if (csrf && myMemberSkills) {
      getSkills();
    }
  }, [csrf, myMemberSkills]);

  const addSkill = async (name) => {
    if (!csrf) {
      return;
    }
    const inSkillsList = skills.find(
      (skill) => skill.name.toUpperCase() === name.toUpperCase()
    );
    let curSkill = inSkillsList;
    if (!inSkillsList) {
      const res = await createSkill({ name: name, pending: true }, csrf);
      const data =
        res && res.payload && res.payload.data ? res.payload.data : null;
      data && dispatch({ type: ADD_SKILL, payload: data });
      curSkill = data;
    }
    if (curSkill && curSkill.id && id) {
      if (
        Object.values(mySkills).find(
          (skill) => skill.name.toUpperCase === curSkill.name.toUpperCase()
        )
      ) {
        return;
      }
      const res = await createMemberSkill(
        { skillid: curSkill.id, memberid: id },
        csrf
      );
      const data =
        res && res.payload && res.payload.data ? res.payload.data : null;
      data && dispatch({ type: ADD_MEMBER_SKILL, payload: data });
    }
  };

  const updateProfile = () => {
    dispatch({
      type: UPDATE_USER_BIO,
      payload: bio,
    });
  };

  const removeSkill = async (id, csrf) => {
    const mSkill = myMemberSkills.find((s) => s.skillid === id);
    await deleteMemberSkill(mSkill.id, csrf);
    dispatch({ type: DELETE_MEMBER_SKILL, payload: id });
  };

  return (
    <div className="Profile">
      <div className="flex-row" style={{ marginTop: "20px" }}>
        <div className="profile-image">
          <Avatar
            alt="Profile"
            src={getAvatarURL(workEmail)}
            style={{ width: "180px", height: "180px" }}
          />
        </div>
        <div className="flex-row">
          <div style={{ textAlign: "left" }}>
            <h2 style={{ margin: 0 }}>
              {name}
              {updating && (
                <Button
                  style={{
                    backgroundColor: "green",
                    color: "white",
                    marginLeft: "20px",
                  }}
                  onClick={() => {
                    setDisabled(!disabled);
                    setUpdating(!updating);
                    updateProfile();
                  }}
                >
                  Update
                </Button>
              )}
              {!updating && (
                <EditIcon
                  onClick={() => {
                    setDisabled(!disabled);
                    setUpdating(!updating);
                  }}
                  style={{
                    color: "black",
                    marginLeft: "20px",
                  }}
                />
              )}
            </h2>
            <div>
              <span>Job Title: </span>
              {title}
            </div>
            <div>
              <span>Email: </span>
              {workEmail}
            </div>
            <div>
              <span>PDL: </span>
              {pdl}
            </div>
            <div>
              <span>Bio</span>
              <textarea
                disabled={disabled}
                id="Bio"
                onChange={(e) => setBio(e.target.value)}
                value={bio}
              ></textarea>
            </div>
          </div>
        </div>
      </div>
      <div>
        <div className="skills-section">
          <h2>Skills</h2>
          {mySkills &&
            mySkills.map((memberSkill) => {
              let { id, name } = memberSkill;
              return (
                <div className="current-skills" key={name}>
                  {name}
                  <CancelIcon
                    onClick={() => {
                      if (csrf) {
                        removeSkill(id, csrf);
                      }
                    }}
                    style={{
                      cursor: "pointer",
                      fontSize: "1rem",
                      marginLeft: "5px",
                    }}
                  />
                </div>
              );
            })}
          <Search mySkills={Object.values(mySkills)} addSkill={addSkill} />
        </div>
      </div>
    </div>
  );
};

export default Profile;

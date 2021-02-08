import React, { useContext, useEffect, useState } from "react";

import {
  AppContext,
  selectCurrentUser,
  UPDATE_USER_BIO,
} from "../../context/AppContext";
import { getAvatarURL } from "../../api/api.js";
import { getMember } from "../../api/member";

import { Edit } from "@material-ui/icons";
import { Avatar, Button } from "@material-ui/core";

import "./Profile.css";
import SkillSection from '../skills/SkillSection'

const Profile = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf } = state;
  const userProfile = selectCurrentUser(state);

  const { bioText, workEmail, name, title, id, pdlId } = userProfile;

  const [pdl, setPDL] = useState();
  const [bio, setBio] = useState();
  const [updating, setUpdating] = useState(false);
  const [disabled, setDisabled] = useState(true);

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


  const updateProfile = () => {
    dispatch({
      type: UPDATE_USER_BIO,
      payload: bio,
    });
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
                <Edit
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
      <div className="skills-section">
        <SkillSection userId={id} />
      </div>
    </div>
  );
};

export default Profile;

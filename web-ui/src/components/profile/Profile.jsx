import React, { useContext, useState } from "react";
import EditIcon from "@material-ui/icons/Edit";
import Button from "@material-ui/core/Button";
import CancelIcon from "@material-ui/icons/Cancel";
import Avatar from "@material-ui/core/Avatar";
import { AppContext, UPDATE_USER_BIO } from "../../context/AppContext";
import Search from "./Search";
import { getSkills, getSkill, createSkill } from "../../api/skill.js";
import {
  getMemberSkills,
  createMemberSkill,
  deleteMemberSkill,
} from "../../api/memberskill.js";
import { getMember } from "../../api/member.js";

import "./Profile.css";

const Profile = () => {
  const { state, dispatch } = useContext(AppContext);
  const { userProfile, userData } = state;
  const [mySkills, setMySkills] = useState([]);
  const { bioText, workEmail, name, role, id } = userProfile;
  const { image_url } = userData;

  const [pdl, setPDL] = useState();
  const [bio, setBio] = useState();
  const [updating, setUpdating] = useState(false);
  const [disabled, setDisabled] = useState(true);
  const [skillsList, setSkillsList] = useState([]);

  // Get PDL's name
  React.useEffect(() => {
    async function getPDLName() {
      if (userProfile.pdlId) {
        let res = await getMember(userProfile.pdlId);
        let pdlProfile =
          res.payload.data && !res.error ? res.payload.data : undefined;
        setPDL(pdlProfile ? pdlProfile.name : "");
      }
    }
    getPDLName();
  }, [userProfile]);

  // Get skills list
  React.useEffect(() => {
    async function updateSkillsList() {
      let res = await getSkills();
      setSkillsList(res.payload && res.payload.data ? res.payload.data : []);
    }
    updateSkillsList();
  }, []);

  React.useEffect(() => {
    async function updateBio() {
      setBio(bioText);
    }
    updateBio();
  }, [bioText]);

  React.useEffect(() => {
    async function updateMySkills() {
      let updatedMySkills = {};
      if (id) {
        let res = await getMemberSkills(id);

        let data =
          res.payload && res.payload.status === 200 ? res.payload.data : null;

        updatedMySkills =
          data && !res.error && data.length > 0
            ? Object.assign(
                ...(await Promise.all(
                  data.map(async (memberSkill) => {
                    let res = await getSkill(memberSkill.skillid);
                    let data =
                      res &&
                      res.payload &&
                      res.payload.status === 200 &&
                      !res.error
                        ? res.payload.data
                        : null;
                    return { [memberSkill.id]: data };
                  })
                ))
              )
            : {};
      }

      setMySkills(updatedMySkills);
    }
    updateMySkills();
  }, [id]);

  const addSkill = async (name) => {
    const inSkillsList = skillsList.find(
      (skill) => skill.name.toUpperCase() === name.toUpperCase()
    );

    let curSkill = inSkillsList;
    if (!inSkillsList) {
      let res = await createSkill({ name: name, pending: true });
      let data =
        res && res.payload && res.payload.status === 201
          ? res.payload.data
          : null;
      curSkill = data;
    }

    let mySkillsTemp = { ...mySkills };
    if (curSkill && curSkill.id && id) {
      if (
        !Object.values(mySkills).find(
          (skill) => skill.name.toUpperCase === curSkill.name.toUpperCase()
        )
      ) {
        let res = await createMemberSkill({
          skillid: curSkill.id,
          memberid: id,
        });
        let data =
          res && res.payload && res.payload.status === 201
            ? res.payload.data
            : null;

        if (data) {
          mySkillsTemp[data.id] = curSkill;
          setMySkills(mySkillsTemp);
        }
      }
    }
  };

  const updateProfile = () => {
    dispatch({
      type: UPDATE_USER_BIO,
      payload: bio,
    });
  };

  const removeSkill = async (id) => {
    await deleteMemberSkill(id);
    let mySkillsTemp = { ...mySkills };
    delete mySkillsTemp[id];
    setMySkills(mySkillsTemp);
  };

  return (
    <div>
      <div className="flex-row" style={{ marginTop: "20px" }}>
        <div className="profile-image">
          <Avatar
            alt="Profile"
            src={image_url ? image_url : "/default_profile.jpg"}
            style={{ width: "200px", height: "220px" }}
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
              <span>Role: </span>
              {role}
            </div>
            <div>
              <span>Email: </span>
              {workEmail}
            </div>
            <div>
              <span>PDL: </span>
              {pdl}
            </div>
            <textarea
              disabled={disabled}
              id="Bio"
              onChange={(e) => setBio(e.target.value)}
              value={bio}
            ></textarea>
          </div>
        </div>
      </div>
      <div>
        <div className="skills-section">
          <h2>Skills</h2>
          {Object.entries(mySkills).map((memberSkill) => {
            let [id, skill] = memberSkill;
            return (
              <div className="current-skills" key={skill.name}>
                {skill.name}
                <CancelIcon
                  onClick={() => {
                    removeSkill(id);
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
          <Search
            skillsList={skillsList}
            mySkills={Object.values(mySkills)}
            addSkill={addSkill}
          />
        </div>
      </div>
    </div>
  );
};

export default Profile;

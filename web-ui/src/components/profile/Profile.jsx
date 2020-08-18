import React, { useContext, useState } from "react";
import EditIcon from "@material-ui/icons/Edit";
import Button from "@material-ui/core/Button";
import CancelIcon from "@material-ui/icons/Cancel";
import Avatar from "@material-ui/core/Avatar";
import { AppContext, MY_PROFILE_UPDATE } from "../../context/AppContext";
import Search from "./Search";
import Input from "./Input";
import { getSkills, getSkillById, createSkill } from "../../api/skill.js";
import {
  getMemberSkills,
  createMemberSkill,
  deleteMemberSkill,
} from "../../api/memberskill.js";

import "./Profile.css";

const Profile = () => {
  const { state, dispatch } = useContext(AppContext);
  const { defaultProfile, user } = state;
  const [mySkills, setMySkills] = useState([]);
  const { bio, email, image_url, name, pdl, role } = defaultProfile;

  const [Role, setRole] = useState(role);
  const [Email, setEmail] = useState(email);
  const [Bio, setBio] = useState(bio);
  const [updating, setUpdating] = useState(false);
  const [disabled, setDisabled] = useState(true);
  const [skillsList, setSkillsList] = useState([]);

  // Get skills list
  React.useEffect(() => {
    async function updateSkillsList() {
      let res = await getSkills();
      setSkillsList(res.payload && res.payload.data ? res.payload.data : []);
    }
    updateSkillsList();
  }, []);

  React.useEffect(() => {
    async function updateMySkills() {
      let res = await getMemberSkills(user.uuid);

      let data =
        res && res.payload && res.payload.status === 200
          ? res.payload.data
          : null;

      let updatedMySkills =
        data && !res.error && data.length > 0
          ? Object.assign(
              ...(await Promise.all(
                data.map(async (memberSkill) => {
                  let res = await getSkillById(memberSkill.skillid);
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
          : [];

      setMySkills(updatedMySkills);
    }
    updateMySkills();
  }, [user.uuid]);

  const addSkill = async (name) => {
    const inSkillsList = skillsList.find(
      (skill) => skill.name.toUpperCase() === name.toUpperCase()
    );

    let mySkillsTemp = { ...mySkills };
    let curSkill = inSkillsList;
    if (!inSkillsList) {
      let res = await createSkill({ name: name, pending: true });
      let data =
        res && res.payload && res.payload.status === 201
          ? res.payload.data
          : null;
      curSkill = data;
    }

    if (curSkill && curSkill.skillid) {
      if (
        !Object.values(mySkills).find(
          (skill) => skill.name.toUpperCase === curSkill.name.toUpperCase()
        )
      ) {
        let res = await createMemberSkill({
          skillid: curSkill.skillid,
          memberid: user.uuid,
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
    const updatedProfile = {
      role: Role,
      email: Email,
      name: name,
      pdl: pdl,
      bio: Bio,
    };
    dispatch({
      type: MY_PROFILE_UPDATE,
      payload: updatedProfile,
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
            src={
              image_url
                ? image_url
                : require("../../images/default_profile.jpg")
            }
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
            <Input
              disabled={disabled}
              label="Role: "
              value={Role}
              setValue={setRole}
            />
            <Input
              disabled={disabled}
              label="Email: "
              value={Email}
              setValue={setEmail}
            />
            <div>
              <span>PDL: </span>
              {pdl}
            </div>
            <Input
              disabled={disabled}
              label="Bio: "
              rows={2}
              value={Bio}
              setValue={setBio}
            />
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

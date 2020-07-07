import React, { useContext, useState } from "react";
import ProfileContext from "../../context/ProfileContext";
import EditIcon from "@material-ui/icons/Edit";
import Button from "@material-ui/core/Button";
import CancelIcon from "@material-ui/icons/Cancel";
import {
  SkillsContext,
  MY_SKILL_ADD,
  MY_SKILL_REMOVE,
  MY_SKILL_TOGGLE,
} from "../../context/SkillsContext";
import Search from "./Search";
import Input from "./Input";

import "./Profile.css";

const Profile = () => {
  const context = useContext(ProfileContext);
  const { bio, email, image_url, name, pdl, role } = context.defaultProfile;

  const [Role, setRole] = useState(role);
  const [Email, setEmail] = useState(email);
  const [PDL, setPDL] = useState(pdl);
  const [Bio, setBio] = useState(bio);
  const [updating, setUpdating] = useState(false);
  const [disabled, setDisabled] = useState(true);

  const { state, dispatch } = useContext(SkillsContext);
  const { mySkills } = state;

  const onClick = (skill) => {
    console.log({ skill });
    dispatch({ type: MY_SKILL_TOGGLE, payload: skill });
  };

  const removeSkill = (skill) => {
    dispatch({ type: MY_SKILL_REMOVE, payload: skill });
  };

  return (
    <div>
      <div className="flex-row" style={{ marginTop: "20px" }}>
        <div className="profile-image">
          <img
            alt="Profile"
            src={image_url ? image_url : "https://i.imgur.com/TkSNOpF.jpg"}
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
            <Input disabled label="PDL: " value={PDL} setValue={setPDL} />
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
        <Search onClick={onClick} />
        <h2>Skills</h2>
        {mySkills.map((e) => {
          return (
            <div className="current-skills" key={e}>
              {e}
              <CancelIcon
                onClick={() => {
                  removeSkill(e);
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
      </div>
    </div>
  );
};

export default Profile;
